package net.meteor.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.meteor.common.block.BlockSlippery;
import net.meteor.common.block.BlockSlipperyStairs;
import net.meteor.common.item.ItemBlockSlippery;
import net.meteor.common.tileentity.TileEntitySlippery;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

public class FreezerRecipes {

	private static FreezerRecipes theInstance;

	public enum RecipeType {
		item(0), fluid(1), both(2), either(3);

		private int id; // Use RecipeType.values() to get a RecipeType using this id

		private RecipeType(int id) {
			this.id = id;
		}

		public int getID() { return id; }

		public static RecipeType fromID(int id) {
			RecipeType[] types = values();
			return id >= 0 && id < types.length ? types[id] : either;
		}
	}

	public static class FreezerRecipe {

		protected Fluid fluid;
		protected int fluidAmountNeeded;
		protected ItemStack itemNeeded;
		protected ItemStack result;
		protected RecipeType type = RecipeType.both;

		public FreezerRecipe(ItemStack item, Fluid fluid, int fluidAmount, ItemStack result) {
			this.itemNeeded = item;
			this.fluid = fluid;
			this.fluidAmountNeeded = fluidAmount;
			this.result = result;
		}

		public FreezerRecipe(ItemStack item, ItemStack result) {
			this(item, null, 0, result);
			setRecipeType(RecipeType.item);
		}

		public FreezerRecipe(Fluid fluid, int fluidAmount, ItemStack result) {
			this(ItemStack.EMPTY, fluid, fluidAmount, result);
			setRecipeType(RecipeType.fluid);
		}

		public FreezerRecipe setRecipeType(RecipeType type) {
			this.type = type;
			return this;
		}

		public RecipeType getRecipeType() { return type; }
		public Fluid getFluid() { return fluid; }
		public int getFluidAmount() { return fluidAmountNeeded; }
		public ItemStack getItemStack() { return itemNeeded; }
		public ItemStack getResult(ItemStack neededItem) { return result; }
		public boolean requiresFluid() { return fluid != null; }
		public boolean requiresItem() { return !itemNeeded.isEmpty(); }

		public boolean hasRequiredMaterials(ItemStack item, Fluid fluid, int fluidAmount) {
			if (fluidAmount >= fluidAmountNeeded) {
				if (requiresFluid()) {
					if (fluid != null && this.fluid.isSame(fluid)) {
						return requiresItem() ? (!item.isEmpty() ? ItemStack.isSameItem(this.itemNeeded, item) : false) : true;
					}
				} else if (requiresItem() && !item.isEmpty()) {
					return ItemStack.isSameItem(this.itemNeeded, item);
				}
			}
			return false;
		}
	}

	/** Water + almost any full block (or stairs) makes it slippery. Slippery blocks get one level more slippery. */
	public static class SlipperyRecipe extends FreezerRecipe {

		public SlipperyRecipe() {
			super(ItemStack.EMPTY, Fluids.WATER, 250, ItemStack.EMPTY);
			setRecipeType(RecipeType.both);
		}

		@Override
		public boolean requiresItem() { return true; }

		@Override
		public ItemStack getResult(ItemStack neededItem) {
			Block block = ((BlockItem) neededItem.getItem()).getBlock();
			if (block instanceof BlockSlippery slippery) {
				return ItemBlockSlippery.withFacadeName(MeteorBlocks.getSlipperyBlock(slippery.getSlipperyLevel() + 1), ItemBlockSlippery.getFacadeName(neededItem));
			} else if (block instanceof BlockSlipperyStairs slippery) {
				return ItemBlockSlippery.withFacadeName(MeteorBlocks.getSlipperyStairs(slippery.getSlipperyLevel() + 1), ItemBlockSlippery.getFacadeName(neededItem));
			} else if (block instanceof StairBlock) {
				return ItemBlockSlippery.withFacadeName(MeteorBlocks.SLIPPERY_STAIRS.get(), TileEntitySlippery.getNameFromBlock(block));
			}
			return ItemBlockSlippery.withFacadeName(MeteorBlocks.SLIPPERY_BLOCK.get(), TileEntitySlippery.getNameFromBlock(block));
		}

		@Override
		public boolean hasRequiredMaterials(ItemStack item, Fluid fluid, int fluidAmount) {
			if (fluidAmount >= fluidAmountNeeded && fluid != null && this.fluid.isSame(fluid)) {
				return isFreezableItem(item);
			}
			return false;
		}
	}

	private List<FreezerRecipe> recipes = new ArrayList<FreezerRecipe>();
	private Set<Item> neededItems = new HashSet<Item>();  		// Used for shift-clicking items into top slot

	public static FreezerRecipes instance() {
		// Created lazily since the recipes use registered items
		if (theInstance == null) {
			theInstance = new FreezerRecipes();
		}
		return theInstance;
	}

	private FreezerRecipes() {
		addRecipe(new ItemStack(Blocks.ICE, 1), Fluids.WATER, 1000, new ItemStack(Blocks.PACKED_ICE, 1));
		addRecipe(Items.IRON_INGOT, new ItemStack(MeteorItems.FrozenIron.get(), 1));
		addRecipe(new SlipperyRecipe());
		addRecipe(Fluids.WATER, 1000, new ItemStack(Blocks.ICE, 1));
		addRecipe(Fluids.LAVA, 1000, new ItemStack(Blocks.OBSIDIAN, 1));
	}

	public void addRecipe(Block block, ItemStack result) {
		this.addRecipe(block.asItem(), result);
	}

	public void addRecipe(Item item, ItemStack result) {
		this.addRecipe(new ItemStack(item, 1), result);
	}

	public void addRecipe(ItemStack need, ItemStack result) {
		this.recipes.add(new FreezerRecipe(need, result));
		this.neededItems.add(need.getItem());
	}

	public void addRecipe(Fluid fluid, int fluidAmount, ItemStack result) {
		this.recipes.add(new FreezerRecipe(fluid, fluidAmount, result));
	}

	public void addRecipe(ItemStack need, Fluid fluid, int fluidAmount, ItemStack result) {
		this.recipes.add(new FreezerRecipe(need, fluid, fluidAmount, result));
		this.neededItems.add(need.getItem());
	}

	public void addRecipe(FreezerRecipe recipe) {
		this.recipes.add(recipe);
		if (recipe.requiresItem() && !(recipe instanceof SlipperyRecipe)) {
			this.neededItems.add(recipe.getItemStack().getItem());
		}
	}

	private boolean canUseRecipe(FreezerRecipe recipe, RecipeType typeAvailable) {
		if (typeAvailable != RecipeType.either) {
			return recipe.getRecipeType() == typeAvailable;
		}
		return true;
	}

	public FreezerRecipe getFreezingResult(ItemStack item, FluidStack fluidStack, RecipeType typeAvailable) {
		for (FreezerRecipe recipe : recipes) {
			if (canUseRecipe(recipe, typeAvailable)) {
				if (fluidStack != null && !fluidStack.isEmpty()) {
					if (recipe.hasRequiredMaterials(item, fluidStack.getFluid(), fluidStack.getAmount())) {
						return recipe;
					}
				} else {
					if (recipe.hasRequiredMaterials(item, null, 0)) {
						return recipe;
					}
				}
			}
		}
		return null;
	}

	public boolean isRequiredItem(ItemStack item) {
		return !item.isEmpty() && this.neededItems.contains(item.getItem());
	}

	public static boolean isFreezableItem(ItemStack item) {
		if (MeteorConfig.safeGet(MeteorConfig.slipperyBlocksEnabled, true) && !item.isEmpty() && item.getItem() instanceof BlockItem itemBlock) {
			return BlockSlippery.canBeSlippery(itemBlock.getBlock());
		}
		return false;
	}

}
