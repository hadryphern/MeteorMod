package net.meteor.common.block;

import net.meteor.common.MeteorItems;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public class BlockRareFallenMeteor extends BlockMeteor
{
	public BlockRareFallenMeteor(Properties props)
	{
		super(props);
	}

	@Override
	protected Item getItemDropped()
	{
		return MeteorItems.itemRedMeteorGem.get();
	}

	@Override
	protected int quantityDropped(int meta, int fortune, RandomSource random)
	{
		return meta > 0 ? 1 + random.nextInt(fortune + 1) : 0;
	}

	@Override
	public int getExpDrop(BlockState state, LevelReader world, RandomSource random, BlockPos pos, int fortune, int silkTouch) {
		return silkTouch > 0 ? 0 : Mth.nextInt(random, 3, 6);
	}

}
