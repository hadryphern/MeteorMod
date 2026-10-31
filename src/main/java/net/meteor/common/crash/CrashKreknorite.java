package net.meteor.common.crash;

import net.meteor.common.EnumMeteor;
import net.meteor.common.MeteorConfig;
import net.meteor.common.SBAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class CrashKreknorite extends CrashMeteorite
{
	public CrashKreknorite(int Size, Explosion expl, EnumMeteor metType)
	{
		super(Size, expl, metType);
	}

	@Override
	public void afterCraterFormed(Level world, RandomSource random, int i, int j, int k) {
		if (this.crashSize >= MeteorConfig.getMinMeteorSizeForPortal()) {
			createPortal(world, i, j, k, random.nextBoolean());
		}
		int blazes = random.nextInt(3);
		for (BlockPos p : getAffectedBlocks()) {
			if (blazes <= 0) break;
			boolean j2 = world.isEmptyBlock(p);
			if (j2 && world.getBlockState(p.below()).isSolidRender(world, p.below()) && (random.nextInt(10) > 4)) {
				Blaze blaze = EntityType.BLAZE.create(world);
				if (blaze != null) {
					blaze.absMoveTo(p.getX(), p.getY(), p.getZ(), 0.0F, 0.0F);
					world.addFreshEntity(blaze);
				}
				blazes--;
			}
		}
	}

	private void createPortal(Level world, int i, int j, int k, boolean directionFlag) {
		if (directionFlag) {
			//Base
			SBAPI.generateCuboid(world, i + 1, j + 1, k, i, j + 1, k, Blocks.NETHER_BRICKS);
			SBAPI.generateCuboid(world, i + 2, j + 2, k + 1, i - 1, j + 2, k - 1, Blocks.NETHER_BRICKS);
			SBAPI.generateCuboid(world, i + 3, j + 3, k + 2, i - 2, j + 3, k - 2, Blocks.NETHER_BRICKS);
			//Portal
			SBAPI.generateCuboid(world, i + 2, j + 3, k, i - 1, j + 7, k, Blocks.OBSIDIAN.defaultBlockState(), true);
			//Columns
			SBAPI.generateCuboid(world, i + 3, j + 4, k + 2, i + 3, j + 8, k + 2, Blocks.NETHER_BRICKS);
			SBAPI.generateCuboid(world, i + 3, j + 4, k - 2, i + 3, j + 8, k - 2, Blocks.NETHER_BRICKS);
			SBAPI.generateCuboid(world, i - 2, j + 4, k - 2, i - 2, j + 8, k - 2, Blocks.NETHER_BRICKS);
			SBAPI.generateCuboid(world, i - 2, j + 4, k + 2, i - 2, j + 8, k + 2, Blocks.NETHER_BRICKS);
			//First level of roof
			SBAPI.generateCuboid(world, i + 3, j + 8, k + 3, i - 2, j + 8, k + 3, Blocks.NETHER_BRICKS);
			SBAPI.generateCuboid(world, i + 3, j + 8, k - 3, i - 2, j + 8, k - 3, Blocks.NETHER_BRICKS);
			SBAPI.generateCuboid(world, i + 4, j + 8, k + 2, i + 4, j + 8, k - 2, Blocks.NETHER_BRICKS);
			SBAPI.generateCuboid(world, i - 3, j + 8, k + 2, i - 3, j + 8, k - 2, Blocks.NETHER_BRICKS);
			//Second level of roof
			SBAPI.generateCuboid(world, i + 3, j + 9, k + 2, i - 2, j + 9, k - 2, Blocks.NETHER_BRICKS);
			SBAPI.placeBlock(world, i + 3, j + 9, k + 2, Blocks.AIR);
			SBAPI.placeBlock(world, i + 3, j + 9, k - 2, Blocks.AIR);
			SBAPI.placeBlock(world, i - 2, j + 9, k - 2, Blocks.AIR);
			SBAPI.placeBlock(world, i - 2, j + 9, k + 2, Blocks.AIR);
			SBAPI.generateCuboid(world, i + 2, j + 9, k + 1, i - 1, j + 9, k - 1, Blocks.GLOWSTONE);
			//Top level of roof
			SBAPI.generateCuboid(world, i + 2, j + 10, k + 1, i - 1, j + 10, k - 1, Blocks.NETHER_BRICKS);
		} else {
			//Base
			SBAPI.generateCuboid(world, i, j + 1, k + 1, i, j + 1, k, Blocks.NETHER_BRICKS);
			SBAPI.generateCuboid(world, i + 1, j + 2, k + 2, i - 1, j + 2, k - 1, Blocks.NETHER_BRICKS);
			SBAPI.generateCuboid(world, i + 2, j + 3, k + 3, i - 2, j + 3, k - 2, Blocks.NETHER_BRICKS);
			//Portal
			SBAPI.generateCuboid(world, i, j + 3, k + 2, i, j + 7, k - 1, Blocks.OBSIDIAN.defaultBlockState(), true);
			//Columns
			SBAPI.generateCuboid(world, i - 2, j + 4, k + 3, i - 2, j + 8, k + 3, Blocks.NETHER_BRICKS);
			SBAPI.generateCuboid(world, i - 2, j + 4, k - 2, i - 2, j + 8, k - 2, Blocks.NETHER_BRICKS);
			SBAPI.generateCuboid(world, i + 2, j + 4, k - 2, i + 2, j + 8, k - 2, Blocks.NETHER_BRICKS);
			SBAPI.generateCuboid(world, i + 2, j + 4, k + 3, i + 2, j + 8, k + 3, Blocks.NETHER_BRICKS);
			//First level of roof
			SBAPI.generateCuboid(world, i + 3, j + 8, k + 3, i + 3, j + 8, k - 2, Blocks.NETHER_BRICKS);
			SBAPI.generateCuboid(world, i - 3, j + 8, k + 3, i - 3, j + 8, k - 2, Blocks.NETHER_BRICKS);
			SBAPI.generateCuboid(world, i + 2, j + 8, k + 4, i - 2, j + 8, k + 4, Blocks.NETHER_BRICKS);
			SBAPI.generateCuboid(world, i + 2, j + 8, k - 3, i - 2, j + 8, k - 3, Blocks.NETHER_BRICKS);
			//Second level of roof
			SBAPI.generateCuboid(world, i + 2, j + 9, k + 3, i - 2, j + 9, k - 2, Blocks.NETHER_BRICKS);
			SBAPI.placeBlock(world, i - 2, j + 9, k + 3, Blocks.AIR);
			SBAPI.placeBlock(world, i - 2, j + 9, k - 2, Blocks.AIR);
			SBAPI.placeBlock(world, i + 2, j + 9, k - 2, Blocks.AIR);
			SBAPI.placeBlock(world, i + 2, j + 9, k + 3, Blocks.AIR);
			SBAPI.generateCuboid(world, i + 1, j + 9, k + 2, i - 1, j + 9, k - 1, Blocks.GLOWSTONE);
			//Top level of roof
			SBAPI.generateCuboid(world, i + 1, j + 10, k + 2, i - 1, j + 10, k - 1, Blocks.NETHER_BRICKS);
		}
		// Lighting the frame creates the nether portal
		SBAPI.placeBlock(world, i, j + 4, k, Blocks.FIRE);
	}

}
