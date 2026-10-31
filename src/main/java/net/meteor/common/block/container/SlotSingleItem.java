package net.meteor.common.block.container;

import java.util.function.Supplier;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class SlotSingleItem extends Slot {

	private final Supplier<Item> item;

	public SlotSingleItem(Container par1iInventory, int par2, int par3, int par4, Supplier<Item> item) {
		super(par1iInventory, par2, par3, par4);
		this.item = item;
	}

	/**
	 * Check if the stack is a valid item for this slot.
	 */
	@Override
	public boolean mayPlace(ItemStack par1ItemStack) {
		return !par1ItemStack.isEmpty() && par1ItemStack.is(item.get()) && this.container.canPlaceItem(this.getContainerSlot(), par1ItemStack);
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

}
