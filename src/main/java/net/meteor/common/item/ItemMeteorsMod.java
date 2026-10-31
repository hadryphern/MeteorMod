package net.meteor.common.item;

import java.util.List;

import net.meteor.common.MeteorItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class ItemMeteorsMod extends Item
{
	public ItemMeteorsMod(Properties props)
	{
		super(props);
	}

	@Override
	public void appendHoverText(ItemStack par1ItemStack, Level world, List<Component> par3List, TooltipFlag flag) {
		if (this == MeteorItems.itemRedMeteorGem.get()) {
			par3List.add(Component.translatable("item.RedMeteorGem.desc.one").withStyle(ChatFormatting.WHITE));
			par3List.add(Component.translatable("item.RedMeteorGem.desc.two").withStyle(ChatFormatting.WHITE));
		}
	}
}
