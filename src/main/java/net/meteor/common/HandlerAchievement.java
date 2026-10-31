package net.meteor.common;

import net.meteor.MeteorsMod;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * The achievements are advancements now (data/meteors/advancements). Most use vanilla triggers
 * (picking up or crafting items, taming a Comet Kitty); this grants "Magnetized".
 */
@Mod.EventBusSubscriber(modid = MeteorsMod.MOD_ID)
public class HandlerAchievement
{
	@SubscribeEvent
	public static void notifyPickup(PlayerEvent.ItemPickupEvent event) {
		if (HandlerPlayerTick.getMagnetizationLevel(event.getEntity()) > 0) {
			MeteorTriggers.trigger(event.getEntity(), MeteorTriggers.ATTRACTED_DROP);
		}
	}
}
