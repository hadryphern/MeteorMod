package net.meteor.common.climate;

import java.util.ArrayList;
import java.util.List;

import net.meteor.common.ClientHandler;
import net.meteor.common.EnumMeteor;
import net.meteor.common.IMeteorShield;
import net.meteor.common.MeteorConfig;
import net.meteor.common.MeteorSounds;
import net.meteor.common.MeteorTriggers;
import net.meteor.common.entity.EntityMeteor;
import net.meteor.common.network.MeteorNetwork;
import net.meteor.common.network.PacketGhostMeteor;
import net.meteor.common.network.PacketLastCrash;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

/** Handles the meteors of one dimension: the ones in orbit, the ones falling, and where they crashed. */
public class HandlerMeteor
{
	private final ServerLevel theWorld;
	private final ClimateUpdater climateUpdater;
	private final MeteorForecast forecast;
	private final ShieldManager shieldManager;

	private static final RandomSource random = RandomSource.create();

	public final ArrayList<GhostMeteor> ghostMets;
	public final ArrayList<CrashedChunkSet> crashedChunks;

	public HandlerMeteor(ServerLevel world) {
		this.theWorld = world;
		MeteorSavedData data = world.getDataStorage().computeIfAbsent(MeteorSavedData::load, MeteorSavedData::new, MeteorSavedData.NAME);
		this.ghostMets = data.ghostMets;
		this.crashedChunks = data.crashedChunks;
		this.climateUpdater = new ClimateUpdater(this, world);
		this.forecast = new MeteorForecast(climateUpdater, ghostMets, data, theWorld);
		this.shieldManager = new ShieldManager(theWorld, data.meteorShields);
	}

	public void updateMeteors() {
		if (!(theWorld.getDayTime() % 24000L >= 12000L || (!MeteorConfig.meteorsFallOnlyAtNight.get()))) {
			return;
		}
		for (int i = 0; i < this.ghostMets.size(); i++) {
			GhostMeteor gMeteor = this.ghostMets.get(i);
			ChunkPos coords = new ChunkPos(gMeteor.x >> 4, gMeteor.z >> 4);
			if (!canSpawnNewMeteorAt(coords)) {
				sendGhostMeteorRemovePacket(gMeteor);
				this.ghostMets.remove(i--);
				forecast.updateNearestTimeForClients();
			} else {
				gMeteor.update();
				if (gMeteor.ready) {
					IMeteorShield shield = shieldManager.getClosestShieldInRange(gMeteor.x, gMeteor.z);
					if (shield != null) {
						Player player = getPlayerByName(theWorld, shield.getOwner());
						if (player != null) {
							player.sendSystemMessage(ClientHandler.createMessage(Component.translatable("MeteorShield.meteorBlocked"), ChatFormatting.GREEN));
							MeteorTriggers.trigger(player, MeteorTriggers.METEOR_BLOCKED);
						}
						shieldManager.sendMeteorMaterialsToShield(shield, gMeteor);
					} else if (gMeteor.type == EnumMeteor.KITTY) {
						kittyAttack();
					} else {
						EntityMeteor meteor = new EntityMeteor(this.theWorld, gMeteor.size, gMeteor.x, gMeteor.z, gMeteor.type, false);
						this.theWorld.addFreshEntity(meteor);
						applyMeteorCrash(gMeteor.x, 0, gMeteor.z);
						playCrashSound(meteor);
					}
					sendGhostMeteorRemovePacket(gMeteor);
					this.ghostMets.remove(i--);
					forecast.updateNearestTimeForClients();
				}
			}
		}
	}

	public void kittyAttack() {
		theWorld.getServer().getPlayerList().broadcastSystemMessage(ClientHandler.createMessage(Component.translatable("Meteor.kittiesIncoming"), ChatFormatting.DARK_RED), false);
		for (ServerPlayer player : new ArrayList<ServerPlayer>(this.theWorld.players())) {
			for (int r = random.nextInt(64) + 50; r >= 0; r--) {
				int x = random.nextInt(64);
				int z = random.nextInt(64);
				if (random.nextBoolean()) x = -x;
				if (random.nextBoolean()) z = -z;
				x = (int)(x + player.getX());
				z = (int)(z + player.getZ());
				IMeteorShield shield = shieldManager.getClosestShieldInRange(x, z);
				if (shield != null) {
					Player playerOwner = getPlayerByName(theWorld, shield.getOwner());
					if (playerOwner != null) {
						playerOwner.sendSystemMessage(ClientHandler.createMessage(Component.translatable("MeteorShield.meteorBlocked"), ChatFormatting.GREEN));
						MeteorTriggers.trigger(playerOwner, MeteorTriggers.METEOR_BLOCKED);
					}
					shieldManager.sendMeteorMaterialsToShield(shield, new GhostMeteor(x, z, 1, 0, EnumMeteor.KITTY));
				} else {
					EntityMeteor fKitty = new EntityMeteor(this.theWorld, 1, x, z, EnumMeteor.KITTY, false);
					fKitty.spawnPauseTicks = random.nextInt(100);
					this.theWorld.addFreshEntity(fKitty);
				}
			}
			MeteorTriggers.trigger(player, MeteorTriggers.KITTY_EVENT);
		}
	}

	public void applyMeteorCrash(int x, int y, int z) {
		this.crashedChunks.removeIf(set -> ++set.age >= 20);

		ChunkPos coords = new ChunkPos(x >> 4, z >> 4);
		this.crashedChunks.add(new CrashedChunkSet(coords.x, coords.z, x, y, z));

		forecast.setLastCrashLocation(new CrashLocation(x, y, z, true, forecast.getLastCrashLocation()));
		MeteorNetwork.sendToDimension(new PacketLastCrash(forecast.getLastCrashLocation()), theWorld);
		if (MeteorConfig.textNotifyCrash.get()) {
			theWorld.getServer().getPlayerList().broadcastSystemMessage(Component.translatable("Meteor.crashed"), false);
		}

		AABB aabb = new AABB(x - 60D, theWorld.getMinBuildHeight(), z - 60D, x + 60D, theWorld.getMaxBuildHeight(), z + 60D);
		List<Player> players = this.theWorld.getEntitiesOfClass(Player.class, aabb);
		for (Player player : players) {
			MeteorTriggers.trigger(player, MeteorTriggers.FOUND_METEOR);
		}
	}

	public MeteorForecast getForecast() {
		return this.forecast;
	}

	public ShieldManager getShieldManager() {
		return this.shieldManager;
	}

	public ClimateUpdater getClimateUpdater() {
		return this.climateUpdater;
	}

	public boolean canSpawnNewMeteor() {
		return this.ghostMets.size() < 3;
	}

	public boolean canSpawnNewMeteorAt(ChunkPos coords) {
		for (int i = 0; i < this.crashedChunks.size(); i++) {
			if (this.crashedChunks.get(i).containsChunk(coords)) {
				return false;
			}
		}
		return true;
	}

	public void readyNewMeteor(int x, int z, int size, int tGoal, EnumMeteor type) {
		if (canSpawnNewMeteor()) {
			GhostMeteor gMeteor = new GhostMeteor(x, z, size, tGoal, type);
			this.ghostMets.add(gMeteor);
			sendGhostMeteorAddPacket(gMeteor);
			forecast.updateNearestTimeForClients();
			if (type == EnumMeteor.KITTY) {
				for (ServerPlayer player : theWorld.players()) {
					player.sendSystemMessage(ClientHandler.createMessage(Component.translatable("Meteor.kittiesDetected.one"), ChatFormatting.DARK_RED));
					player.sendSystemMessage(ClientHandler.createMessage(Component.translatable("Meteor.kittiesDetected.two"), ChatFormatting.DARK_RED));
				}
			}
		}
	}

	public static int getMeteorSize() {
		int r = random.nextInt(26);
		int maxSize = MeteorConfig.getMaxMeteorSize();
		int minSize = MeteorConfig.getMinMeteorSize();
		if ((maxSize == 3) && (minSize == 1)) {
			if (r == 25)
				return 3;
			if (r > 15) {
				return 2;
			}
			return 1;
		}
		if ((maxSize == 3) && (minSize == 2)) {
			if (r > 15) {
				return 3;
			}
			return 2;
		}
		if ((minSize == 1) && (maxSize == 2)) {
			if (r > 15) {
				return 2;
			}
			return 1;
		}

		return minSize;
	}

	public static EnumMeteor getMeteorType() {
		int r = random.nextInt(63);
		if ((r >= 60) && (MeteorConfig.unknownEnabled.get())) {
			return EnumMeteor.UNKNOWN;
		}
		if ((r >= 52) && (MeteorConfig.kreknoriteEnabled.get())) {
			return EnumMeteor.KREKNORITE;
		}
		if ((r >= 35) && (MeteorConfig.frezariteEnabled.get())) {
			return EnumMeteor.FREZARITE;
		}
		return MeteorConfig.getDefaultType();
	}

	public static EnumMeteor getCometType() {
		EnumMeteor type = getMeteorType();
		if (type == EnumMeteor.UNKNOWN) {
			return EnumMeteor.getTypeFromID(random.nextInt(3));
		}
		return type;
	}

	public static Player getPlayerByName(Level world, String name) {
		for (Player player : world.players()) {
			if (player.getGameProfile().getName().equals(name)) {
				return player;
			}
		}
		return null;
	}

	public void sendGhostMeteorPackets(ServerPlayer player) {
		for (GhostMeteor met : this.ghostMets) {
			MeteorNetwork.sendTo(new PacketGhostMeteor(true, met), player);
		}
	}

	private void sendGhostMeteorAddPacket(GhostMeteor met) {
		MeteorNetwork.sendToDimension(new PacketGhostMeteor(true, met), theWorld);
	}

	private void sendGhostMeteorRemovePacket(GhostMeteor met) {
		MeteorNetwork.sendToDimension(new PacketGhostMeteor(false, met), theWorld);
	}

	private void playCrashSound(EntityMeteor meteor) {
		for (ServerPlayer player : theWorld.players()) {
			double xDiff = meteor.getX() - player.getX();
			double zDiff = meteor.getZ() - player.getZ();
			double xMod = xDiff / 128.0D * 4.0D;
			double zMod = zDiff / 128.0D * 4.0D;
			theWorld.playSound(null, player.getX() + xMod, player.getY() + 1.0D, player.getZ() + zMod, MeteorSounds.METEOR_CRASH.get(), SoundSource.AMBIENT, 1.0F, 1.0F);
		}
	}
}
