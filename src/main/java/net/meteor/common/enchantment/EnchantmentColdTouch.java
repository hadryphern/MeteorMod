package net.meteor.common.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/** Freezes water under the player, see HandlerPlayerTick. Leggings and boots only. */
public class EnchantmentColdTouch extends Enchantment
{
	public EnchantmentColdTouch()
	{
		super(Rarity.RARE, EnchantmentCategory.ARMOR, new EquipmentSlot[] { EquipmentSlot.LEGS, EquipmentSlot.FEET });
	}

	@Override
	public int getMinCost(int par1)
	{
		return 15 + (par1 - 1) * 6;
	}

	@Override
	public int getMaxCost(int par1)
	{
		return super.getMinCost(par1) + 45;
	}

	@Override
	public int getMaxLevel()
	{
		return 2;
	}

	@Override
	public boolean canEnchant(ItemStack par1ItemStack) {
		if (par1ItemStack.getItem() instanceof ArmorItem itemarmor) {
			return itemarmor.getType() == ArmorItem.Type.LEGGINGS || itemarmor.getType() == ArmorItem.Type.BOOTS;
		}
		return false;
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack) {
		return canEnchant(stack);
	}
}
