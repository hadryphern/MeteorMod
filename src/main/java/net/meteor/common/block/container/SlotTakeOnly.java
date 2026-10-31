package net.meteor.common.block.container;

import net.meteor.common.MeteorTriggers;
import net.meteor.common.block.BlockSlippery;
import net.meteor.common.block.BlockSlipperyStairs;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/** An output slot. When a player is given, taking ice, packed ice or slippery blocks grants the Freezer advancements. */
public class SlotTakeOnly extends Slot {

	private Player thePlayer;

	public SlotTakeOnly(Container par1iInventory, int par2, int par3, int par4) {
		super(par1iInventory, par2, par3, par4);
	}

	public SlotTakeOnly(Container par1iInventory, int par2, int par3, int par4, Player player) {
		this(par1iInventory, par2, par3, par4);
		this.thePlayer = player;
	}

	@Override
	public boolean mayPlace(ItemStack par1ItemStack) {
		return false;
	}

	@Override
	protected void onQuickCraft(ItemStack stack, int amount) {
		this.checkTakeAchievements(stack);
	}

	@Override
	protected void checkTakeAchievements(ItemStack item) {
		if (!item.isEmpty() && this.thePlayer != null) {
			if (item.is(Blocks.ICE.asItem())) {
				MeteorTriggers.trigger(this.thePlayer, MeteorTriggers.FREEZE_WATER);
			} else if (item.is(Blocks.PACKED_ICE.asItem())) {
				MeteorTriggers.trigger(this.thePlayer, MeteorTriggers.FREEZE_ICE);
			} else if (item.getItem() instanceof BlockItem blockItem) {
				Block block = blockItem.getBlock();
				if (block instanceof BlockSlippery || block instanceof BlockSlipperyStairs) {
					MeteorTriggers.trigger(this.thePlayer, MeteorTriggers.FREEZE_BLOCKS);
				}
			}
		}
		super.checkTakeAchievements(item);
	}

	@Override
	public void onTake(Player player, ItemStack stack) {
		this.checkTakeAchievements(stack);
		super.onTake(player, stack);
	}

}
