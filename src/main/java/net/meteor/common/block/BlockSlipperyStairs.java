package net.meteor.common.block;

import java.util.function.Supplier;

import net.meteor.common.tileentity.TileEntitySlippery;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BlockSlipperyStairs extends StairBlock implements EntityBlock {

	private final int slipperyLevel;

	public BlockSlipperyStairs(int slipperyLevel, Supplier<BlockState> base, Properties props) {
		super(base, props);
		this.slipperyLevel = slipperyLevel;
	}

	public int getSlipperyLevel() {
		return slipperyLevel;
	}

	@Override
	public boolean triggerEvent(BlockState state, Level world, BlockPos pos, int id, int param) {
		super.triggerEvent(state, world, pos, id, param);
		BlockEntity tileentity = world.getBlockEntity(pos);
		return tileentity != null ? tileentity.triggerEvent(id, param) : false;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileEntitySlippery(pos, state);
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack item) {
		BlockSlippery.onSlipperyPlaced(world, pos, item);
		super.setPlacedBy(world, pos, state, entity, item);
	}

	@Override
	public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
		return BlockSlippery.getSlipperyItem(this, world, pos);
	}

}
