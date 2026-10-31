package net.meteor.common.block;

import net.meteor.common.item.ItemBlockSlippery;
import net.meteor.common.tileentity.TileEntitySlippery;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** A block made slippery by the Freezer. It looks like the block it was made from (the facade). */
public class BlockSlippery extends BaseEntityBlock {

	private final int slipperyLevel;

	public BlockSlippery(int slipperyLevel, Properties props) {
		super(props);
		this.slipperyLevel = slipperyLevel;
	}

	public int getSlipperyLevel() {
		return slipperyLevel;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileEntitySlippery(pos, state);
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		// Drawn by TileEntitySlipperyRenderer using the facade block's model
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack item) {
		onSlipperyPlaced(world, pos, item);
		super.setPlacedBy(world, pos, state, entity, item);
	}

	@Override
	public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
		return getSlipperyItem(this, world, pos);
	}

	static void onSlipperyPlaced(Level world, BlockPos pos, ItemStack item) {
		if (world.getBlockEntity(pos) instanceof TileEntitySlippery tileEntity) {
			tileEntity.setFacadeBlockName(ItemBlockSlippery.getFacadeName(item));
			tileEntity.setChanged();
		}
	}

	static ItemStack getSlipperyItem(Block block, BlockGetter world, BlockPos pos) {
		ItemStack stack = new ItemStack(block);
		if (world.getBlockEntity(pos) instanceof TileEntitySlippery tileEntity) {
			stack.getOrCreateTag().putString(ItemBlockSlippery.FACADE_BLOCK_KEY, tileEntity.getFacadeBlockName());
		}
		return stack;
	}

	/** Which blocks the Freezer can turn into slippery blocks. */
	public static boolean canBeSlippery(Block block) {
		if (block instanceof BlockSlippery slippery) {
			return slippery.getSlipperyLevel() < 4;
		}
		if (block instanceof BlockSlipperyStairs slippery) {
			return slippery.getSlipperyLevel() < 4;
		}
		if (block instanceof EntityBlock || block instanceof SlabBlock) {
			return false;
		}
		if (block instanceof StairBlock) {
			return true;
		}
		// 1.7.10 accepted the standard cube render types (normal blocks, logs and pillars)
		BlockState state = block.defaultBlockState();
		if (state.getRenderShape() != RenderShape.MODEL) {
			return false;
		}
		try {
			return Block.isShapeFullBlock(state.getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO));
		} catch (RuntimeException e) {
			return false;
		}
	}

}
