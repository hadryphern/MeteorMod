package net.meteor.common.item;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/** Faster underwater, see HandlerPlayerBreakSpeed. */
public class ItemFrezariteSpade extends ShovelItem
{
	public ItemFrezariteSpade(Tier toolMaterial)
	{
		super(toolMaterial, 1.5F, -3.0F, new Properties());
	}

	@Override
	public void appendHoverText(ItemStack par1ItemStack, Level world, List<Component> par3List, TooltipFlag flag)
	{
		ItemFrezaritePickaxe.addFrezariteInfo(par3List, "enchantment.frezSpade");
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
