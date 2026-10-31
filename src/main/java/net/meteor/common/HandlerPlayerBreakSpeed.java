package net.meteor.common;

import net.meteor.MeteorsMod;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Frezarite pickaxes, spades and axes dig five times faster underwater, cancelling the water penalty. */
@Mod.EventBusSubscriber(modid = MeteorsMod.MOD_ID)
public class HandlerPlayerBreakSpeed
{
	@SubscribeEvent
	public static void onBreakSpeedModify(PlayerEvent.BreakSpeed event)
	{
		ItemStack stack = event.getEntity().getMainHandItem();
		if (!stack.isEmpty() && event.getEntity().isEyeInFluid(FluidTags.WATER)) {
			Item i = stack.getItem();
			if ((i == MeteorItems.FrezaritePickaxe.get()) || (i == MeteorItems.FrezariteSpade.get()) || (i == MeteorItems.FrezariteAxe.get()))
				event.setNewSpeed(event.getOriginalSpeed() * 5.0F);
		}
	}
}
