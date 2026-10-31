package net.meteor.common.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/** Pulls nearby items towards the player, see HandlerPlayerTick. Weight 2 is Rarity.RARE. */
public class EnchantmentMagnetized extends Enchantment
{
	public EnchantmentMagnetized()
	{
		super(Rarity.RARE, EnchantmentCategory.BREAKABLE, EquipmentSlot.values());
	}

	@Override
	public int getMinCost(int par1)
	{
		return 15 + (par1 - 1) * 6;
	}

	@Override
	public int getMaxCost(int par1)
	{
		return super.getMinCost(par1) + 40;
	}

	@Override
	public int getMaxLevel()
	{
		return 4;
	}
}
