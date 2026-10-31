package net.meteor.common.block.container;

import net.meteor.common.MeteorBlocks;
import net.meteor.common.MeteorItems;
import net.meteor.common.MeteorMenus;
import net.meteor.common.tileentity.TileEntityMeteorShield;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ContainerMeteorShield extends AbstractContainerMenu {

	private final TileEntityMeteorShield shield;

	/** Client side */
	public ContainerMeteorShield(int id, Inventory inventoryPlayer, BlockPos pos) {
		this(id, inventoryPlayer, inventoryPlayer.player.level().getBlockEntity(pos) instanceof TileEntityMeteorShield shield ? shield
				: new TileEntityMeteorShield(pos, MeteorBlocks.METEOR_SHIELD.get().defaultBlockState()));
	}

	public ContainerMeteorShield(int id, Inventory inventoryPlayer, TileEntityMeteorShield mShield) {
		super(MeteorMenus.METEOR_SHIELD.get(), id);
		this.shield = mShield;

		// Shield Inventory
		this.addSlot(new SlotSingleItem(mShield, 0, 47, 60, MeteorItems.itemMeteorChips));
		for (int i = 0; i < 4; ++i) {
			this.addSlot(new SlotSingleItem(mShield, i + 1, 67 + i * 29, 60, MeteorItems.itemRedMeteorGem));
		}
		for (int i = 0; i < 4; ++i) {
			for (int j = 0; j < 2; ++j) {
				this.addSlot(new SlotTakeOnly(mShield, i * 2 + j + 5, 8 + j * 18, 6 + i * 18));
			}
		}

		// Player inventory
		for (int i = 0; i < 3; ++i)
		{
			for (int j = 0; j < 9; ++j)
			{
				this.addSlot(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; ++i)
		{
			this.addSlot(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
		}
	}

	public TileEntityMeteorShield getShield() {
		return shield;
	}

	@Override
	public boolean stillValid(Player entityplayer) {
		return this.shield.stillValid(entityplayer);
	}

	/**
	 * Called when a player shift-clicks on a slot.
	 */
	@Override
	public ItemStack quickMoveStack(Player par1EntityPlayer, int slotID)
	{
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(slotID);
		if (slot != null && slot.hasItem())
		{
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			if (slotID < shield.getContainerSize()) {
				// last boolean is which direction to search in (last to first, first to last)
				// transfer into player inventory
				if (!this.moveItemStackTo(itemstack1, shield.getContainerSize(), this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (itemstack1.is(MeteorItems.itemMeteorChips.get())) {
				ItemStack chip = new ItemStack(MeteorItems.itemMeteorChips.get(), 1);
				if (shield.canPlaceItem(0, chip)) {
					this.slots.get(0).set(chip);
					itemstack1.shrink(1);
				}
				itemstack = ItemStack.EMPTY;
			} else if (itemstack1.is(MeteorItems.itemRedMeteorGem.get())) {
				for (int i = 1; i < 5 && !itemstack1.isEmpty(); i++) {
					Slot slot1 = this.slots.get(i);
					if (shield.canPlaceItem(i, new ItemStack(MeteorItems.itemRedMeteorGem.get(), 1))) {
						slot1.set(new ItemStack(MeteorItems.itemRedMeteorGem.get(), 1));
						itemstack1.shrink(1);
					}
				}
				itemstack = ItemStack.EMPTY;
			} else {
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
		}
		return itemstack;
	}

}
