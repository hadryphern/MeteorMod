package net.meteor.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.meteor.MeteorsMod;
import net.meteor.common.EnumMeteor;
import net.meteor.common.IMeteorShield;
import net.meteor.common.MeteorConfig;
import net.meteor.common.climate.ClimateUpdater;
import net.meteor.common.climate.GhostMeteor;
import net.meteor.common.climate.HandlerMeteor;
import net.meteor.common.climate.HandlerWorld;
import net.meteor.common.climate.MeteorForecast;
import net.meteor.common.entity.EntityComet;
import net.meteor.common.entity.EntityMeteor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * The five 1.7.10 commands: /kittyattack, /listShields, /listMeteors [clear],
 * /meteor <x> <y> <z> <size> [delay] [type] [summoned] and /comet <x> <y> <z> [type] [delay].
 */
@Mod.EventBusSubscriber(modid = MeteorsMod.MOD_ID)
public class MeteorCommands {

	@SubscribeEvent
	public static void onRegisterCommands(RegisterCommandsEvent event) {
		CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

		dispatcher.register(Commands.literal("kittyattack").requires(s -> s.hasPermission(2)).executes(ctx -> {
			HandlerMeteor metH = HandlerWorld.get(ctx.getSource().getLevel());
			if (metH != null) {
				metH.kittyAttack();
			}
			return 1;
		}));

		dispatcher.register(Commands.literal("listShields").requires(s -> s.hasPermission(2)).executes(ctx -> {
			HandlerMeteor metH = HandlerWorld.get(ctx.getSource().getLevel());
			if (metH != null) {
				for (IMeteorShield shield : metH.getShieldManager().meteorShields) {
					send(ctx, "x:" + shield.getX() + " y:" + shield.getY() + " z:" + shield.getZ() + " r:" + shield.getRange() + " o:" + shield.getOwner());
				}
			}
			return 1;
		}));

		dispatcher.register(Commands.literal("listMeteors").requires(s -> s.hasPermission(2))
				.executes(ctx -> listMeteors(ctx, false))
				.then(Commands.literal("clear").executes(ctx -> listMeteors(ctx, true))));

		dispatcher.register(Commands.literal("meteor").requires(s -> s.hasPermission(2))
				.then(Commands.argument("pos", Vec3Argument.vec3())
				.then(Commands.argument("size", IntegerArgumentType.integer(1, 3))
						.executes(ctx -> spawnMeteor(ctx, 0, -1, false))
				.then(Commands.argument("delay", IntegerArgumentType.integer(0))
						.executes(ctx -> spawnMeteor(ctx, IntegerArgumentType.getInteger(ctx, "delay"), -1, false))
				.then(Commands.argument("type", IntegerArgumentType.integer(0, EnumMeteor.values().length - 1))
						.executes(ctx -> spawnMeteor(ctx, IntegerArgumentType.getInteger(ctx, "delay"), IntegerArgumentType.getInteger(ctx, "type"), false))
				.then(Commands.argument("summoned", BoolArgumentType.bool())
						.executes(ctx -> spawnMeteor(ctx, IntegerArgumentType.getInteger(ctx, "delay"), IntegerArgumentType.getInteger(ctx, "type"), BoolArgumentType.getBool(ctx, "summoned")))))))));

		dispatcher.register(Commands.literal("comet").requires(s -> s.hasPermission(2))
				.then(Commands.argument("pos", Vec3Argument.vec3())
						.executes(ctx -> spawnComet(ctx, -1, 0))
				.then(Commands.argument("type", IntegerArgumentType.integer(0, EnumMeteor.values().length - 1))
						.executes(ctx -> spawnComet(ctx, IntegerArgumentType.getInteger(ctx, "type"), 0))
				.then(Commands.argument("delay", IntegerArgumentType.integer(0))
						.executes(ctx -> spawnComet(ctx, IntegerArgumentType.getInteger(ctx, "type"), IntegerArgumentType.getInteger(ctx, "delay")))))));
	}

	private static void send(CommandContext<CommandSourceStack> ctx, String message) {
		ctx.getSource().sendSuccess(() -> Component.literal(message), false);
	}

	private static int listMeteors(CommandContext<CommandSourceStack> ctx, boolean clear) {
		HandlerMeteor metH = HandlerWorld.get(ctx.getSource().getLevel());
		if (metH != null) {
			MeteorForecast forecast = metH.getForecast();
			if (clear) {
				forecast.clearMeteors();
				send(ctx, "Meteors Cleared");
				return 1;
			}
			int secs = forecast.getSecondsUntilNewMeteor();
			send(ctx, secs + " Seconds Until Possible New Meteor");
			send(ctx, metH.ghostMets.size() + " Meteor(s) in Orbit");
			for (GhostMeteor met : metH.ghostMets) {
				send(ctx, "T:" + met.type.toString() + " S:" + met.size + " X:" + met.x + " Z:" + met.z + " RT:" + met.getRemainingTicks());
			}
		}
		return 1;
	}

	private static int spawnMeteor(CommandContext<CommandSourceStack> ctx, int delay, int type, boolean summoned) {
		ServerLevel world = ctx.getSource().getLevel();
		Vec3 pos = Vec3Argument.getVec3(ctx, "pos");
		if (type < 0) {
			type = world.random.nextInt(EnumMeteor.values().length);
		}
		EntityMeteor meteor = new EntityMeteor(world, IntegerArgumentType.getInteger(ctx, "size"), pos.x, pos.z, EnumMeteor.getTypeFromID(type), summoned);
		meteor.setPos(pos.x, pos.y, pos.z);
		meteor.yo = pos.y;
		meteor.spawnPauseTicks = delay;
		world.addFreshEntity(meteor);
		if (!MeteorConfig.isDimensionWhitelisted(world.dimension())) {
			send(ctx, "The Meteor isn't techincally allowed in this dimension, but I'll spawn it for you anyway.");
		}
		send(ctx, "Meteor spawned.");
		return 1;
	}

	private static int spawnComet(CommandContext<CommandSourceStack> ctx, int type, int delay) {
		ServerLevel world = ctx.getSource().getLevel();
		Vec3 pos = Vec3Argument.getVec3(ctx, "pos");
		if (type < 0) {
			type = world.random.nextInt(EnumMeteor.values().length);
		}
		EntityComet comet = new EntityComet(world, pos.x, pos.z, EnumMeteor.getTypeFromID(type));
		comet.setPos(pos.x, pos.y, pos.z);
		comet.yo = pos.y;
		comet.spawnPauseTicks = delay;
		HandlerMeteor meteorHandler = HandlerWorld.get(world);
		if (meteorHandler == null || ClimateUpdater.spawnComet(world, meteorHandler, comet, (int)pos.x, (int)pos.z)) {
			if (meteorHandler == null) {
				world.addFreshEntity(comet);
			}
			send(ctx, "Comet spawned.");
		} else {
			send(ctx, "Comet was blocked by a Meteor Shield.");
		}
		return 1;
	}

}
