package net.meteor.common.crash;

import net.meteor.common.EnumMeteor;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;

public class CrashFrezarite extends CrashMeteorite
{
	public CrashFrezarite(int Size, Explosion expl, EnumMeteor metType)
	{
		super(Size, expl, metType);
	}

	@Override
	public void afterCraterFormed(Level world, RandomSource random, int i, int j, int k) {
		for (BlockPos p : getAffectedBlocks()) {
			boolean j2 = world.isEmptyBlock(p);
			if (j2 && world.getBlockState(p.below()).isSolidRender(world, p.below()) && (random.nextInt(2) == 0))
				world.setBlock(p, Blocks.SNOW.defaultBlockState().setValue(SnowLayerBlock.LAYERS, random.nextInt(5) + 1), 2);
		}
	}
}
