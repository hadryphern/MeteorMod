package net.meteor.common.climate;

import java.util.HashMap;
import java.util.Map;

import net.meteor.MeteorsMod;
import net.meteor.common.network.MeteorNetwork;
import net.meteor.common.network.PacketGhostMeteor;
import net.meteor.common.network.PacketLastCrash;
import net.meteor.common.network.PacketSoonestMeteor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Creates a {@link HandlerMeteor} for each loaded server dimension (MeteorsMod.proxy.metHandlers in 1.7.10),
 * ticks them (HandlerMeteorTick) and adds the meteor game rules.
 */
@Mod.EventBusSubscriber(modid = MeteorsMod.MOD_ID)
public class HandlerWorld {

	public static GameRules.Key<GameRules.BooleanValue> METEORS_FALL_GAMERULE;
	public static GameRules.Key<GameRules.BooleanValue> SUMMON_METEORS_GAMERULE;

	private static final Map<ResourceKey<Level>, HandlerMeteor> metHandlers = new HashMap<ResourceKey<Level>, HandlerMeteor>();

	public static void registerGameRules() {
		METEORS_FALL_GAMERULE = GameRules.register("meteorsFall", GameRules.Category.SPAWNING, GameRules.BooleanValue.create(true));
		SUMMON_METEORS_GAMERULE = GameRules.register("summonMeteors", GameRules.Category.SPAWNING, GameRules.BooleanValue.create(true));
	}

	/** @return the meteor handler of a server level, or null on the client */
	public static HandlerMeteor get(Level world) {
		if (world instanceof ServerLevel) {
			return metHandlers.get(world.dimension());
		}
		return null;
	}

	@SubscribeEvent
	public static void onWorldLoad(LevelEvent.Load event) {
		if (event.getLevel() instanceof ServerLevel world) {
			metHandlers.put(world.dimension(), new HandlerMeteor(world));
		}
	}

	@SubscribeEvent
	public static void onWorldUnload(LevelEvent.Unload event) {
		if (event.getLevel() instanceof ServerLevel world) {
			metHandlers.remove(world.dimension());
		}
	}

	@SubscribeEvent
	public static void onWorldTick(TickEvent.LevelTickEvent event) {
		if (event.phase == TickEvent.Phase.END && event.level instanceof ServerLevel world) {
			HandlerMeteor metHandler = metHandlers.get(world.dimension());
			if (metHandler != null) {
				metHandler.getClimateUpdater().onWorldTick();
			}
		}
	}

	@SubscribeEvent
	public static void entityJoinWorld(EntityJoinLevelEvent event) {
		if (event.getEntity() instanceof ServerPlayer player && event.getLevel() instanceof ServerLevel world) {
			HandlerMeteor metHandler = metHandlers.get(world.dimension());
			if (metHandler != null) {
				MeteorNetwork.sendTo(new PacketGhostMeteor(), player);		// Clear Ghost Meteors
				metHandler.sendGhostMeteorPackets(player);
				MeteorNetwork.sendTo(new PacketLastCrash(metHandler.getForecast().getLastCrashLocation()), player);
				MeteorNetwork.sendTo(new PacketSoonestMeteor(metHandler.getForecast().getNearestTimeMeteor()), player);
			}
		}
	}

}
