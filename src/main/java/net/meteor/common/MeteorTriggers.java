package net.meteor.common;

import com.google.gson.JsonObject;

import net.meteor.MeteorsMod;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;

/**
 * The 1.7.10 achievements became advancements. Achievements that vanilla triggers can't describe
 * (blocking a meteor, summoning one, ...) are granted through this trigger, using the old achievement names.
 */
public class MeteorTriggers {

	public static final String METEOR_BLOCKED 		= "meteor_blocked";
	public static final String SHIELD_FULLY_UPGRADED = "shield_fully_upgraded";
	public static final String ATTRACTED_DROP 		= "attracted_drop";
	public static final String FOUND_METEOR 		= "found_meteor";
	public static final String SUMMON_METEOR 		= "summon_meteor";
	public static final String KITTY_EVENT 			= "comet_kitty_event";
	public static final String FREEZE_WATER 		= "freeze_water";
	public static final String FREEZE_BLOCKS 		= "freeze_blocks";
	public static final String FREEZE_ICE 			= "freeze_ice";

	public static final EventTrigger EVENT = new EventTrigger();

	public static void register() {
		CriteriaTriggers.register(EVENT);
	}

	/** Equivalent of the old player.addStat(achievement, 1). Safe to call on either side. */
	public static void trigger(Player player, String event) {
		if (player instanceof ServerPlayer serverPlayer) {
			EVENT.trigger(serverPlayer, event);
		}
	}

	public static class EventTrigger extends SimpleCriterionTrigger<EventTrigger.Instance> {

		private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(MeteorsMod.MOD_ID, "event");

		@Override
		public ResourceLocation getId() {
			return ID;
		}

		@Override
		protected Instance createInstance(JsonObject json, ContextAwarePredicate player, DeserializationContext context) {
			return new Instance(player, GsonHelper.getAsString(json, "event"));
		}

		public void trigger(ServerPlayer player, String event) {
			this.trigger(player, instance -> instance.event.equals(event));
		}

		public static class Instance extends AbstractCriterionTriggerInstance {

			private final String event;

			public Instance(ContextAwarePredicate player, String event) {
				super(ID, player);
				this.event = event;
			}

			@Override
			public JsonObject serializeToJson(SerializationContext context) {
				JsonObject json = super.serializeToJson(context);
				json.addProperty("event", this.event);
				return json;
			}
		}
	}

}
