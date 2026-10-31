package net.meteor.common.block.container;

import net.meteor.common.FreezerRecipes;
import net.meteor.common.FreezerRecipes.RecipeType;
import net.meteor.common.MeteorBlocks;
import net.meteor.common.MeteorItems;
import net.meteor.common.MeteorMenus;
import net.meteor.common.tileentity.TileEntityFreezingMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class ContainerFreezingMachine extends AbstractContainerMenu {

	private final TileEntityFreezingMachine iceMaker;
	private final ContainerData data;

	/** Client side */
	public ContainerFreezingMachine(int id, Inventory inventoryPlayer, BlockPos pos) {
		this(id, inventoryPlayer, inventoryPlayer.player.level().getBlockEntity(pos) instanceof TileEntityFreezingMachine freezer ? freezer
				: new TileEntityFreezingMachine(pos, MeteorBlocks.FREEZER.get().defaultBlockState()), new SimpleContainerData(5));
	}

	public ContainerFreezingMachine(int id, Inventory inventoryPlayer, TileEntityFreezingMachine iceMaker, ContainerData data) {
		super(MeteorMenus.FREEZER.get(), id);
		this.iceMaker = iceMaker;
		this.data = data;
		this.addSlot(new Slot(iceMaker, 0, 73, 17));
		this.addSlot(new Slot(iceMaker, 1, 73, 53));
		this.addSlot(new SlotTakeOnly(iceMaker, 2, 133, 35, inventoryPlayer.player));
		this.addSlot(new Slot(iceMaker, 3, 38, 7));
		this.addSlot(new SlotTakeOnly(iceMaker, 4, 38, 60));
		int i;

		for (i = 0; i < 3; ++i)
		{
			for (int j = 0; j < 9; ++j)
			{
				this.addSlot(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (i = 0; i < 9; ++i)
		{
			this.addSlot(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
		}

		this.addDataSlots(data);
	}

	public TileEntityFreezingMachine getFreezer() {
		return iceMaker;
	}

	@Override
	public boolean stillValid(Player player) {
		return iceMaker.stillValid(player);
	}

	public int getCookProgressScaled(int i)
	{
		return this.data.get(0) * i / 200;
	}

	public int getBurnTimeRemainingScaled(int i)
	{
		int currentItemBurnTime = this.data.get(2);
		if (currentItemBurnTime == 0)
		{
			currentItemBurnTime = 200;
		}
		return this.data.get(1) * i / currentItemBurnTime;
	}

	public boolean isFreezing() {
		return this.data.get(1) > 0;
	}

	public int getFluidAmount() {
		return this.data.get(3);
	}

	/** The fluid itself comes from the block entity, which is synced when its tank changes. */
	public FluidStack getFluid() {
		return iceMaker.getTank().getFluid();
	}

	public RecipeType getRecipeMode() {
		return RecipeType.fromID(this.data.get(4));
	}

	/**
	 * Called when a player shift-clicks on a slot.
	 */
	@Override
	public ItemStack quickMoveStack(Player p_82846_1_, int slotID)
	{
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(slotID);

		if (slot != null && slot.hasItem())
		{
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();

			if (slotID == 2)
			{
				if (!this.moveItemStackTo(itemstack1, 5, 41, true))
				{
					return ItemStack.EMPTY;
				}

				slot.onQuickCraft(itemstack1, itemstack);
			}
			else if (slotID != 1 && slotID != 0 && slotID != 3 && slotID != 4)
			{
				if (FreezerRecipes.instance().isRequiredItem(itemstack1) || FreezerRecipes.isFreezableItem(itemstack1))
				{
					if (!this.moveItemStackTo(itemstack1, 0, 1, false))
					{
						return ItemStack.EMPTY;
					}
				}
				else if (itemstack1.is(MeteorItems.itemFrezaCrystal.get()))
				{
					if (!this.moveItemStackTo(itemstack1, 1, 2, false))
					{
						return ItemStack.EMPTY;
					}
				}
				else if (TileEntityFreezingMachine.isFluidContainer(itemstack1))
				{
					if (!this.moveItemStackTo(itemstack1, 3, 4, false))
					{
						return ItemStack.EMPTY;
					}
				}
				else if (slotID >= 5 && slotID < 32)
				{
					if (!this.moveItemStackTo(itemstack1, 32, 41, false))
					{
						return ItemStack.EMPTY;
					}
				}
				else if (slotID >= 32 && slotID < 41 && !this.moveItemStackTo(itemstack1, 5, 32, false))
				{
					return ItemStack.EMPTY;
				}
			}
			else if (!this.moveItemStackTo(itemstack1, 5, 41, false))
			{
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty())
			{
				slot.set(ItemStack.EMPTY);
			}
			else
			{
				slot.setChanged();
			}

			if (itemstack1.getCount() == itemstack.getCount())
			{
				return ItemStack.EMPTY;
			}

			slot.onTake(p_82846_1_, itemstack1);
		}

		return itemstack;
	}

}
