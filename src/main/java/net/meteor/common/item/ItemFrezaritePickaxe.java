package net.meteor.common.item;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/** Faster underwater, see HandlerPlayerBreakSpeed. */
public class ItemFrezaritePickaxe extends PickaxeItem
{
	public ItemFrezaritePickaxe(Tier toolMaterial)
	{
		super(toolMaterial, 1, -2.8F, new Properties());
	}

	@Override
	public void appendHoverText(ItemStack par1ItemStack, Level world, List<Component> par3List, TooltipFlag flag)
	{
		addFrezariteInfo(par3List, "enchantment.frezPickaxe");
	}

	/** The two cyan ("§3") lines every Frezarite tool shows. */
	static void addFrezariteInfo(List<Component> list, String key) {
		list.add(Component.translatable(key + ".one").withStyle(ChatFormatting.DARK_AQUA));
		list.add(Component.translatable(key + ".two").withStyle(ChatFormatting.DARK_AQUA));
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
