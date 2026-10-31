package net.meteor.common.item;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/** Faster underwater, see HandlerPlayerBreakSpeed. */
public class ItemFrezariteAxe extends AxeItem
{
	public ItemFrezariteAxe(Tier toolMaterial)
	{
		super(toolMaterial, 6.0F, -3.1F, new Properties());
	}

	@Override
	public void appendHoverText(ItemStack par1ItemStack, Level world, List<Component> par3List, TooltipFlag flag)
	{
		ItemFrezaritePickaxe.addFrezariteInfo(par3List, "enchantment.frezAxe");
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
