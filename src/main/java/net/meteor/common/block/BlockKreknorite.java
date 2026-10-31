package net.meteor.common.block;

import net.meteor.common.MeteorItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BlockKreknorite extends BlockMeteor
{
	private static final Direction[] HARDEN_SIDES = { Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.UP };

	public BlockKreknorite(Properties props)
	{
		super(props);
	}

	@Override
	protected Item getItemDropped()
	{
		return MeteorItems.itemKreknoChip.get();
	}

	@Override
	public void destroy(LevelAccessor world, BlockPos pos, BlockState state)
	{
		if ((world instanceof ServerLevel level) && (level.random.nextInt(100) == 95)) {
			Blaze blaze = EntityType.BLAZE.create(level);
			if (blaze != null) {
				blaze.moveTo(pos.getX(), pos.getY(), pos.getZ(), 0.0F, 0.0F);
				level.addFreshEntity(blaze);
				blaze.spawnAnim();
			}
		}
	}

	@Override
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random)
	{
		int meta = state.getValue(HEAT);
		if (meta > 0) {
			world.setBlock(pos, state.setValue(HEAT, --meta), 2);
			if (meta <= 0) {
				world.setBlock(pos, Blocks.OBSIDIAN.defaultBlockState(), 2);
				triggerLavaMixEffects(world, pos);
			} else {
				checkForHarden(world, pos);
			}
		}
	}

	@Override
	public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean isMoving)
	{
		checkForHarden(world, pos);
	}

	@Override
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving)
	{
		checkForHarden(world, pos);
	}

	private void checkForHarden(Level world, BlockPos pos)
	{
		if (world.isClientSide || !world.getBlockState(pos).is(this))
		{
			return;
		}
		boolean flag = false;
		for (Direction side : HARDEN_SIDES) {
			if (world.getFluidState(pos.relative(side)).is(FluidTags.WATER)) {
				flag = true;
				break;
			}
		}
		if (flag)
		{
			world.setBlock(pos, Blocks.OBSIDIAN.defaultBlockState(), 2);
			triggerLavaMixEffects(world, pos);
		}
	}

	@Override
	public int getExpDrop(BlockState state, LevelReader world, RandomSource random, BlockPos pos, int fortune, int silkTouch) {
		return silkTouch > 0 ? 0 : Mth.nextInt(random, 3, 6);
	}

}
