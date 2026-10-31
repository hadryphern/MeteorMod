package net.meteor.common.item;

import java.util.function.Supplier;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * Meteorite, Frezarite and Kreknorite gear comes with an enchantment. In 1.7.10 it was added the first time the
 * item's damage was read; here it is added when the item is created, crafted or sits in an inventory.
 * The "enchant-set" tag keeps it from coming back after the enchantment is removed.
 */
public final class AutoEnchant {

	public static final String TAG = "enchant-set";

	private AutoEnchant() {}

	public static void apply(ItemStack stack, Supplier<Enchantment> enchantment, int level) {
		if (stack.isEmpty() || enchantment == null) return;
		if (!stack.isEnchanted() && !isRestricted(stack)) {
			stack.enchant(enchantment.get(), level);
			stack.getOrCreateTag().putBoolean(TAG, true);
		}
	}

	private static boolean isRestricted(ItemStack item) {
		CompoundTag tag = item.getTag();
		if (tag != null) {
			if (tag.contains(TAG)) {
				return tag.getBoolean(TAG);
			} else {
				tag.putBoolean(TAG, false);
			}
		}
		return false;
	}

}
