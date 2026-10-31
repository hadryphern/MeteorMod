package net.meteor.common.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

/** Kreknorite Chips burn in a furnace (the old IFuelHandler in HandlerRecipe). */
public class ItemKreknoChip extends ItemMeteorsMod
{
	public ItemKreknoChip(Properties props)
	{
		super(props);
	}

	@Override
	public int getBurnTime(ItemStack itemStack, RecipeType<?> recipeType) {
		return 3300;
	}
}
