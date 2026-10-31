package net.meteor.common;

import net.meteor.MeteorsMod;
import net.meteor.common.tileentity.TileEntityFreezingMachine;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Shows how long an item keeps the Freezer running. */
@Mod.EventBusSubscriber(modid = MeteorsMod.MOD_ID, value = Dist.CLIENT)
public class TooltipProvider {

	@SubscribeEvent
	public static void onTooltip(ItemTooltipEvent event) {
		int i = TileEntityFreezingMachine.getItemFreezeTime(event.getItemStack());
		if (i > 0) {
			event.getToolTip().add(Component.translatable("info.freezeTime", i));
		}
	}

}
