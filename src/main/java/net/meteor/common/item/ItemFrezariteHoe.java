package net.meteor.common.item;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;

/** Tills like any hoe, but the farmland it makes is already watered. */
public class ItemFrezariteHoe extends HoeItem
{
	public ItemFrezariteHoe(Tier toolMaterial)
	{
		super(toolMaterial, -2, -1.0F, new Properties());
	}

	@Override
	public InteractionResult useOn(UseOnContext context)
	{
		InteractionResult result = super.useOn(context);
		Level world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		if (result.consumesAction() && !world.isClientSide) {
			BlockState state = world.getBlockState(pos);
			if (state.getBlock() instanceof FarmBlock) {
				world.setBlock(pos, state.setValue(FarmBlock.MOISTURE, FarmBlock.MAX_MOISTURE), 3);
			}
		}
		return result;
	}

	@Override
	public void appendHoverText(ItemStack par1ItemStack, Level world, List<Component> par3List, TooltipFlag flag)
	{
		ItemFrezaritePickaxe.addFrezariteInfo(par3List, "enchantment.frezHoe");
	}

	@Override
	public boolean isFoil(ItemStack par1ItemStack)
	{
		return true;
	}

	@Override
	public int getEnchantmentValue()
	{
		return 0;
	}
}
