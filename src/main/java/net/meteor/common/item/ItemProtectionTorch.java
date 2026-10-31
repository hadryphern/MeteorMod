package net.meteor.common.item;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

/** The Protected Land Tester item. It doesn't drop when broken. */
public class ItemProtectionTorch extends StandingAndWallBlockItem
{
	public ItemProtectionTorch(Block standing, Block wall, Properties props, Direction attachment)
	{
		super(standing, wall, props, attachment);
	}

	@Override
	public void appendHoverText(ItemStack par1ItemStack, Level world, List<Component> par3List, TooltipFlag flag) {
		par3List.add(Component.translatable("ProtectionTorch.usage").withStyle(ChatFormatting.LIGHT_PURPLE));
	}
}
