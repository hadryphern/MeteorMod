package net.meteor.common.item;

import java.util.function.Supplier;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

public class ItemEnchSword extends SwordItem
{
	protected Supplier<Enchantment> enchantment;
	protected int level;

	public ItemEnchSword(Tier toolMaterial, Supplier<Enchantment> ench, int lvl)
	{
		super(toolMaterial, 3, -2.4F, new Properties());
		this.enchantment = ench;
		this.level = lvl;
	}

	@Override
	public ItemStack getDefaultInstance() {
		ItemStack stack = super.getDefaultInstance();
		AutoEnchant.apply(stack, enchantment, level);
		return stack;
	}

	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
		if (!world.isClientSide) {
			AutoEnchant.apply(stack, enchantment, level);
		}
	}

	@Override
	public void onCraftedBy(ItemStack stack, Level world, Player player) {
		AutoEnchant.apply(stack, enchantment, level);
	}

}
