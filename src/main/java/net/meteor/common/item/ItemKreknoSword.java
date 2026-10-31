package net.meteor.common.item;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.enchantment.Enchantments;

public class ItemKreknoSword extends ItemEnchSword
{
	public ItemKreknoSword(Tier toolMaterial)
	{
		super(toolMaterial, () -> Enchantments.FIRE_ASPECT, 2);
	}
}
