package net.meteor.common.climate;

import java.util.List;

import net.meteor.common.EnumMeteor;
import net.meteor.common.IMeteorShield;
import net.meteor.common.MeteorConfig;
import net.meteor.common.entity.EntityComet;
import net.meteor.common.tileentity.TileEntityMeteorShield;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;

/** Decides when a new meteor enters orbit and when comets fall. Updates once per second. */
public class ClimateUpdater {

	private static final RandomSource random = RandomSource.create();
	private int ticks = 0;
	private int tickGoal = -1;
	private final HandlerMeteor meteorHandler;
	private final ServerLevel world;
	private long lastTick = 0L;

	public ClimateUpdater(HandlerMeteor metHandler, ServerLevel world) {
		this.meteorHandler = metHandler;
		this.world = world;
	}

	public void onWorldTick() {
		if (world.getGameRules().getBoolean(HandlerWorld.METEORS_FALL_GAMERULE) && MeteorConfig.isDimensionWhitelisted(world.dimension())) {
			long wTime = world.getGameTime();
			if (wTime % 20L == 0L && wTime != lastTick) {
				lastTick = wTime;
				if (this.tickGoal == -1) {
					this.tickGoal = getNewTickGoal();
				}
				meteorHandler.updateMeteors();
				if ((world.getDayTime() % 24000L >= 12000L || (!MeteorConfig.meteorsFallOnlyAtNight.get()))) {
					this.ticks++;
					if (this.ticks >= this.tickGoal) {
						List<ServerPlayer> players = world.players();
						int fallDistance = MeteorConfig.meteorFallDistance.get();
						if ((meteorHandler.canSpawnNewMeteor()) && (players.size() > 0)) {
							int x = world.random.nextInt(fallDistance);
							int z = world.random.nextInt(fallDistance);
							if (world.random.nextBoolean()) x = -x;
							if (world.random.nextBoolean()) z = -z;
							ServerPlayer player = players.get(world.random.nextInt(players.size()));
							x = (int)(x + player.getX());
							z = (int)(z + player.getZ());
							ChunkPos coords = new ChunkPos(x >> 4, z >> 4);
							if (meteorHandler.canSpawnNewMeteorAt(coords)) {
								if (random.nextInt(100) < MeteorConfig.kittyAttackChance.get()) {
									meteorHandler.readyNewMeteor(x, z, HandlerMeteor.getMeteorSize(), 90, EnumMeteor.KITTY);
								} else {
									meteorHandler.readyNewMeteor(x, z, HandlerMeteor.getMeteorSize(), random.nextInt(MeteorConfig.randTicksUntilMeteorCrashes() + 1) + MeteorConfig.minTicksUntilMeteorCrashes(), HandlerMeteor.getMeteorType());
								}
							}
						}

						if (random.nextInt(100) < MeteorConfig.cometFallChance.get() && players.size() > 0) {

							int x = world.random.nextInt(Math.max(1, fallDistance / 4));
							int z = world.random.nextInt(Math.max(1, fallDistance / 4));
							if (world.random.nextBoolean()) x = -x;
							if (world.random.nextBoolean()) z = -z;
							ServerPlayer player = players.get(world.random.nextInt(players.size()));
							x = (int)(x + player.getX());
							z = (int)(z + player.getZ());
							EntityComet comet = new EntityComet(world, x, z, HandlerMeteor.getCometType());
							spawnComet(world, meteorHandler, comet, x, z);
						}

						this.ticks = 0;
						this.tickGoal = getNewTickGoal();
					}
				}
			}
		}
	}

	/** @return false if a Meteor Shield in range blocks comets */
	public static boolean spawnComet(ServerLevel world, HandlerMeteor meteorHandler, EntityComet comet, int x, int z) {
		List<IMeteorShield> shields = meteorHandler.getShieldManager().getShieldsInRange(x, z);
		for (IMeteorShield ims : shields) {
			if (ims.getPreventComets()) {
				return false;
			}
		}

		for (IMeteorShield ims : shields) {
			if (world.getBlockEntity(new BlockPos(ims.getX(), ims.getY(), ims.getZ())) instanceof TileEntityMeteorShield shield) {
				shield.detectComet(comet);
			}
		}

		world.addFreshEntity(comet);
		return true;
	}

	private int getNewTickGoal() {
		return random.nextInt(MeteorConfig.randTicksUntilMeteorSpawn() + 1) + MeteorConfig.minTicksUntilMeteorSpawn();
	}

	public int getSecondsUntilNewMeteor() {
		return tickGoal - ticks;
	}

}
