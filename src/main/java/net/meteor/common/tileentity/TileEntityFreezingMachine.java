package net.meteor.common.tileentity;

import net.meteor.common.FreezerRecipes;
import net.meteor.common.FreezerRecipes.FreezerRecipe;
import net.meteor.common.FreezerRecipes.RecipeType;
import net.meteor.common.MeteorBlockEntities;
import net.meteor.common.MeteorItems;
import net.meteor.common.block.BlockFreezingMachine;
import net.meteor.common.block.container.ContainerFreezingMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

// This class is sort of a mirror image of a vanilla furnace,
// but has some new attributes such as fluid handling and other recipes.
public class TileEntityFreezingMachine extends TileEntityNetworkBase implements WorldlyContainer, MenuProvider {

	public static final int TANK_CAPACITY = FluidType.BUCKET_VOLUME * 10;

	private static final int[] acccessibleSlots = {0, 1, 2, 3, 4};

	private NonNullList<ItemStack> inv = NonNullList.withSize(5, ItemStack.EMPTY);
	private ItemStack lastKnownItem = ItemStack.EMPTY;
	private final FluidTank tank = new FluidTank(TANK_CAPACITY) {
		@Override
		protected void onContentsChanged() {
			TileEntityFreezingMachine.this.sendUpdate();
		}
	};
	private final LazyOptional<IFluidHandler> fluidHandler = LazyOptional.of(() -> tank);
	private RecipeType acceptedRecipeType = RecipeType.either;
	public int cookTime;
	public int burnTime;
	public int currentItemBurnTime;

	/** Replaces the 1.7.10 sendProgressBarUpdate/updateProgressBar calls of the container. */
	private final ContainerData dataAccess = new ContainerData() {
		@Override
		public int get(int id) {
			switch (id) {
			case 0: return cookTime;
			case 1: return burnTime;
			case 2: return currentItemBurnTime;
			case 3: return tank.getFluidAmount();
			case 4: return acceptedRecipeType.getID();
			default: return 0;
			}
		}

		@Override
		public void set(int id, int value) {
			switch (id) {
			case 0: cookTime = value; break;
			case 1: burnTime = value; break;
			case 2: currentItemBurnTime = value; break;
			case 4: acceptedRecipeType = RecipeType.fromID(value); break;
			default: break;
			}
		}

		@Override
		public int getCount() {
			return 5;
		}
	};

	public TileEntityFreezingMachine(BlockPos pos, BlockState state) {
		super(MeteorBlockEntities.FREEZER.get(), pos, state);
	}

	@Override
	public int getContainerSize() {
		return 5;
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack stack : inv) {
			if (!stack.isEmpty()) return false;
		}
		return true;
	}

	@Override
	public ItemStack getItem(int slot) {
		return inv.get(slot);
	}

	@Override
	public ItemStack removeItem(int slot, int amount) {
		return ContainerHelper.removeItem(this.inv, slot, amount);
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		return ContainerHelper.takeItem(this.inv, slot);
	}

	@Override
	public void setItem(int slot, ItemStack item) {
		this.inv.set(slot, item);
		if (!item.isEmpty() && item.getCount() > this.getMaxStackSize())
		{
			item.setCount(this.getMaxStackSize());
		}
		if (slot == 3) {
			checkFluidContainer();
		}
		this.setChanged();
	}

	public static boolean isFluidContainer(ItemStack item) {
		return !item.isEmpty() && FluidUtil.getFluidHandler(item.copyWithCount(1)).isPresent();
	}

	/** Empties a filled container into the tank, or fills an empty one from it. The container ends up in slot 4. */
	private void checkFluidContainer() {
		if (level == null || level.isClientSide) return;
		ItemStack item = inv.get(3);
		if (!isFluidContainer(item)) return;
		ItemStack single = item.copyWithCount(1);
		FluidStack fluid = FluidUtil.getFluidContained(single).orElse(FluidStack.EMPTY);
		FluidActionResult result;
		if (!fluid.isEmpty()) {
			if (!(fluid.isFluidEqual(tank.getFluid()) || tank.getFluidAmount() == 0) || tank.fill(fluid, IFluidHandler.FluidAction.SIMULATE) != fluid.getAmount()) {
				return;
			}
			result = FluidUtil.tryEmptyContainer(single, tank, fluid.getAmount(), null, false);
			if (result.isSuccess() && canOutputContainer(result.getResult())) {
				result = FluidUtil.tryEmptyContainer(single, tank, fluid.getAmount(), null, true);
			} else {
				return;
			}
		} else {
			if (tank.getFluid().isEmpty()) {
				return;
			}
			result = FluidUtil.tryFillContainer(single, tank, Integer.MAX_VALUE, null, false);
			if (result.isSuccess() && canOutputContainer(result.getResult())) {
				result = FluidUtil.tryFillContainer(single, tank, Integer.MAX_VALUE, null, true);
			} else {
				return;
			}
		}
		if (result.isSuccess()) {
			ItemStack container = result.getResult();
			if (inv.get(4).isEmpty()) {
				inv.set(4, container.copy());
			} else {
				inv.get(4).grow(1);
			}
			item.shrink(1);
			if (item.isEmpty()) {
				inv.set(3, ItemStack.EMPTY);
			}
			this.sendUpdate();
		}
	}

	private boolean canOutputContainer(ItemStack container) {
		ItemStack out = inv.get(4);
		return out.isEmpty() || (ItemStack.isSameItemSameTags(out, container) && out.getCount() + 1 <= out.getMaxStackSize());
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		this.inv = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(nbt, this.inv);
		this.burnTime = nbt.getShort("BurnTime");
		this.cookTime = nbt.getShort("CookTime");
		this.currentItemBurnTime = nbt.getShort("ItemFreezeTime");
		this.acceptedRecipeType = RecipeType.fromID(nbt.getShort("acceptedRecipeType"));
		tank.readFromNBT(nbt);
	}

	@Override
	protected void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		nbt.putShort("BurnTime", (short)this.burnTime);
		nbt.putShort("CookTime", (short)this.cookTime);
		nbt.putShort("ItemFreezeTime", (short)this.currentItemBurnTime);
		nbt.putShort("acceptedRecipeType", (short)this.acceptedRecipeType.getID());
		ContainerHelper.saveAllItems(nbt, this.inv);
		tank.writeToNBT(nbt);
	}

	@Override
	public CompoundTag getUpdateTag() {
		return this.saveWithoutMetadata();
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public int getMaxStackSize() {
		return 64;
	}

	@Override
	public boolean stillValid(Player player) {
		return Container.stillValidBlockEntity(this, player);
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack item) {
		if (slot == 3) return isFluidContainer(item);
		return slot == 2 ? false : (slot == 1 ? getItemFreezeTime(item) > 0 : true);
	}

	@Override
	public void clearContent() {
		inv.clear();
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return acccessibleSlots;
	}

	@Override
	public boolean canPlaceItemThroughFace(int slot, ItemStack item, Direction side) {
		return isFluidContainer(item) ? slot == 3 : slot == 1;
	}

	@Override
	public boolean canTakeItemThroughFace(int slot, ItemStack item, Direction side) {
		return slot == 2 || slot == 4;
	}

	public RecipeType getRecipeMode() {
		return this.acceptedRecipeType;
	}

	public void setRecipeMode(RecipeType type) {
		this.acceptedRecipeType = type;
	}

	public FluidTank getTank() {
		return tank;
	}

	public ContainerData getDataAccess() {
		return dataAccess;
	}

	public boolean isFreezing() {
		return this.burnTime > 0;
	}

	public static void tick(Level world, BlockPos pos, BlockState state, TileEntityFreezingMachine freezer) {
		freezer.updateEntity(state);
	}

	private void updateEntity(BlockState state) {
		boolean flag = this.burnTime > 0;
		boolean flag1 = false;

		if (this.burnTime > 0)
		{
			--this.burnTime;
		}

		if (!inv.get(3).isEmpty()) {
			checkFluidContainer();
		}

		if (this.burnTime != 0 || !this.inv.get(1).isEmpty())
		{
			if (this.burnTime == 0 && this.canFreeze())
			{
				this.currentItemBurnTime = this.burnTime = getItemFreezeTime(this.inv.get(1));

				if (this.burnTime > 0)
				{
					flag1 = true;
					ItemStack fuel = this.inv.get(1);
					if (!fuel.isEmpty())
					{
						ItemStack container = fuel.getCraftingRemainingItem();
						fuel.shrink(1);
						if (fuel.isEmpty())
						{
							this.inv.set(1, container);
						}
					}
				}
			}

			if (this.isFreezing() && this.canFreeze())
			{
				++this.cookTime;

				if (this.cookTime == 200)
				{
					this.cookTime = 0;
					this.freezeItem();
					flag1 = true;
				}
			}
			else
			{
				this.cookTime = 0;
			}
		} else {
			this.cookTime = 0;
		}

		if (flag != this.burnTime > 0)
		{
			flag1 = true;
			state = state.setValue(BlockFreezingMachine.LIT, this.burnTime > 0);
			this.level.setBlock(this.worldPosition, state, 3);
		}

		if (flag1)
		{
			this.setChanged();
		}
	}

	public static int getItemFreezeTime(ItemStack itemStack) {
		if (!itemStack.isEmpty()) {
			Item item = itemStack.getItem();
			if (item == MeteorItems.itemFrezaCrystal.get()) {
				return 1600;
			}
			if (item == Blocks.ICE.asItem()) {
				return 200;
			}
			if (item == Blocks.PACKED_ICE.asItem()) {
				return 400;
			}
		}
		return 0;
	}

	private boolean canFreeze() {
		FreezerRecipe recipe = FreezerRecipes.instance().getFreezingResult(this.inv.get(0), tank.getFluid(), this.acceptedRecipeType);
		if (recipe == null) return false;
		ItemStack result = recipe.getResult(inv.get(0));
		if (this.inv.get(2).isEmpty()) {
			checkLastKnownItem(result);
			return true;
		}
		if (!ItemStack.isSameItemSameTags(this.inv.get(2), result)) return false;
		int resultSize = inv.get(2).getCount() + result.getCount();
		if (resultSize <= getMaxStackSize() && resultSize <= this.inv.get(2).getMaxStackSize()) {
			checkLastKnownItem(result);
			return true;
		}
		return false;
	}

	/** Freezing restarts when the result changes (for example after switching the recipe mode). */
	private void checkLastKnownItem(ItemStack result) {
		if (this.lastKnownItem.isEmpty()) {
			this.lastKnownItem = result.copy();
		} else if (!ItemStack.isSameItemSameTags(result, this.lastKnownItem)) {
			this.cookTime = 0;
			this.lastKnownItem = result.copy();
			this.setChanged();
		}
	}

	public void freezeItem() {
		if (this.canFreeze())
		{
			FreezerRecipe recipe = FreezerRecipes.instance().getFreezingResult(this.inv.get(0), tank.getFluid(), this.acceptedRecipeType);
			ItemStack result = recipe.getResult(inv.get(0));

			if (this.inv.get(2).isEmpty())
			{
				this.inv.set(2, result.copy());
			}
			else if (ItemStack.isSameItemSameTags(this.inv.get(2), result))
			{
				this.inv.get(2).grow(result.getCount());
			}

			if (recipe.requiresItem()) {
				this.inv.get(0).shrink(1);
				if (this.inv.get(0).isEmpty())
				{
					this.inv.set(0, ItemStack.EMPTY);
				}
			}

			if (recipe.requiresFluid()) {
				tank.drain(recipe.getFluidAmount(), IFluidHandler.FluidAction.EXECUTE);
			}
		}
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == ForgeCapabilities.FLUID_HANDLER) {
			return fluidHandler.cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		fluidHandler.invalidate();
	}

	@Override
	public void onButtonPress(int id) {
		if (id == 0) {
			int i = this.acceptedRecipeType.getID();
			if (i == 3) {
				i = 0;
			} else {
				i++;
			}
			this.acceptedRecipeType = RecipeType.values()[i];
		}
	}

	// MenuProvider

	@Override
	public Component getDisplayName() {
		return Component.translatable("block.meteors.freezer");
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player) {
		return new ContainerFreezingMachine(id, playerInv, this, this.dataAccess);
	}

}
