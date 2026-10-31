package net.meteor.common.item;

import java.util.List;
import java.util.function.Consumer;

import net.meteor.common.block.BlockSlippery;
import net.meteor.common.block.BlockSlipperyStairs;
import net.meteor.common.tileentity.TileEntitySlippery;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

/** A slippery block item. The block it looks like is stored in the "slipFacadeBlock" tag. */
public class ItemBlockSlippery extends BlockItem {

	public static final String FACADE_BLOCK_KEY = "slipFacadeBlock";

	public ItemBlockSlippery(Block block, Properties props) {
		super(block, props);
	}

	@Override
	public void appendHoverText(ItemStack itemStack, Level world, List<Component> info, TooltipFlag advancedTip) {
		info.add(Component.translatable("info.slipperyBlock.one", getSlipperiness(this.getBlock())));
	}

	@Override
	public Component getName(ItemStack itemStack) {
		Block storedBlock = getStoredBlock(itemStack);
		Component facadeName = new ItemStack(storedBlock).getHoverName();
		return Component.translatable(this.getDescriptionId(), facadeName);
	}

	@Override
	public void inventoryTick(ItemStack itemStack, Level world, Entity entity, int slot, boolean selected) {
		checkNBT(itemStack);
	}

	@Override
	protected SoundEvent getPlaceSound(BlockState state, Level world, BlockPos pos, Player player) {
		// Sounds like the block it was made from; the block entity already knows it at this point
		if (world.getBlockEntity(pos) instanceof TileEntitySlippery slippery) {
			return slippery.getFacadeBlock().defaultBlockState().getSoundType().getPlaceSound();
		}
		return super.getPlaceSound(state, world, pos, player);
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new IClientItemExtensions() {
			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return net.meteor.client.render.MeteorItemRenderer.getInstance();
			}
		});
	}

	private static Block getDefaultFacade(ItemStack itemStack) {
		if (itemStack.getItem() instanceof ItemBlockSlippery itemBlock && itemBlock.getBlock() instanceof BlockSlipperyStairs) {
			return Blocks.OAK_STAIRS;
		}
		return Blocks.STONE;
	}

	public static void checkNBT(ItemStack itemStack) {
		if (itemStack.isEmpty()) return;
		CompoundTag nbt = itemStack.getOrCreateTag();
		if (!nbt.contains(FACADE_BLOCK_KEY)) {
			nbt.putString(FACADE_BLOCK_KEY, TileEntitySlippery.getNameFromBlock(getDefaultFacade(itemStack)));
		}
	}

	public static Block getStoredBlock(ItemStack itemStack) {
		if (itemStack.hasTag() && itemStack.getTag().contains(FACADE_BLOCK_KEY)) {
			return TileEntitySlippery.getBlockFromName(itemStack.getTag().getString(FACADE_BLOCK_KEY), getDefaultFacade(itemStack));
		}
		return getDefaultFacade(itemStack);
	}

	public static String getFacadeName(ItemStack itemStack) {
		return TileEntitySlippery.getNameFromBlock(getStoredBlock(itemStack));
	}

	public static ItemStack withFacade(Item item, Block facade) {
		ItemStack stack = new ItemStack(item);
		stack.getOrCreateTag().putString(FACADE_BLOCK_KEY, TileEntitySlippery.getNameFromBlock(facade));
		return stack;
	}

	public static ItemStack withFacadeName(Block slipperyBlock, String facadeName) {
		ItemStack stack = new ItemStack(slipperyBlock);
		stack.getOrCreateTag().putString(FACADE_BLOCK_KEY, facadeName);
		return stack;
	}

	/** 1x to 4x, (slipperiness - 0.94) / 0.04 in 1.7.10 */
	public static int getSlipperiness(Block block) {
		if (block instanceof BlockSlippery slippery) {
			return slippery.getSlipperyLevel();
		}
		if (block instanceof BlockSlipperyStairs slippery) {
			return slippery.getSlipperyLevel();
		}
		return (int) ((block.getFriction() - 0.94F) / 0.04F);
	}

}
