package net.meteor.common.crash;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.meteor.common.EnumMeteor;
import net.meteor.common.MeteorConfig;
import net.meteor.common.MeteorEntities;
import net.meteor.common.entity.EntityAlienCreeper;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** Spreads meteor ore around the crater a meteor left behind. */
public class CrashMeteorite
{
	protected int crashSize;
	protected Explosion explosion;
	protected EnumMeteor meteorType;

	public CrashMeteorite(int Size, Explosion expl, EnumMeteor metType)
	{
		this.crashSize = Mth.clamp(Size, 1, 3);
		this.explosion = expl;
		this.meteorType = metType;
	}

	public boolean generate(Level world, RandomSource random, int i, int j, int k)
	{
		Block meteor = this.meteorType.getMaterial();
		Block rareMeteor = this.meteorType.getRareMaterial();
		int spread = MeteorConfig.impactSpread.get() * this.crashSize;
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

		//Initial spreading
		for (int y = j + spread; y >= j - spread; y--) {
			for (int startX = i + spread; startX >= i - spread; startX--) {
				for (int startZ = k + spread; startZ >= k - spread; startZ--) {
					pos.set(startX, y, startZ);
					if ((!world.isEmptyBlock(pos)) && (meteorsAboveAndBelow(world, pos) == 0) && (random.nextInt(10) + 1 > 7) && (checkBlockIDs(world, pos))) {
						placeMeteor(world, random, pos, random.nextInt(45) == 25 ? rareMeteor : meteor);
					}
				}
			}

		}

		//Bottom layer spreading for a higher concentration in center
		for (int y = j - spread; y >= j - (spread + 1); y--) {
			for (int startX = i + spread; startX >= i - spread; startX--) {
				for (int startZ = k + spread; startZ >= k - spread; startZ--) {
					pos.set(startX, y, startZ);
					if ((!world.isEmptyBlock(pos)) && (meteorsAboveAndBelow(world, pos) == 0) && (checkBlockIDs(world, pos))) {
						placeMeteor(world, random, pos, random.nextInt(45) == 25 ? rareMeteor : meteor);
					}
				}

			}

		}

		afterCraterFormed(world, random, i, j, k);

		return true;
	}

	private void placeMeteor(Level world, RandomSource random, BlockPos pos, Block theBlock) {
		if (theBlock == Blocks.ICE || theBlock == Blocks.LAVA) {
			world.setBlock(pos, theBlock.defaultBlockState(), 2);
		} else {
			world.setBlock(pos, EnumMeteor.withHeat(theBlock, random.nextInt(4) + 1), 3);
		}
	}

	/** Blocks the explosion destroyed, newest first like the 1.7.10 loops over affectedBlockPositions. */
	protected List<BlockPos> getAffectedBlocks() {
		List<BlockPos> list = new ArrayList<BlockPos>(this.explosion.getToBlow());
		Collections.reverse(list);
		return list;
	}

	public void afterCraterFormed(Level world, RandomSource random, int i, int j, int k)
	{
		int aliencreepers = random.nextInt(5);
		for (BlockPos p : getAffectedBlocks()) {
			if (aliencreepers <= 0) break;
			boolean j2 = world.isEmptyBlock(p);
			BlockState k2 = world.getBlockState(p.below());
			if (j2 && k2.isSolidRender(world, p.below()) && (random.nextInt(10) > 4)) {
				EntityAlienCreeper creeper = MeteorEntities.ALIEN_CREEPER.get().create(world);
				if (creeper != null) {
					creeper.moveTo(p.getX(), p.getY(), p.getZ(), 0.0F, 0.0F);
					world.addFreshEntity(creeper);
				}
				aliencreepers--;
			}
		}
	}

	public void afterCrashCompleted(Level world, int i, int j, int k) {}

	public int meteorsAbove(Level world, BlockPos pos) {
		int ceiling = pos.getY() + 6 * this.crashSize;
		int mAbove = 0;
		BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos(pos.getX(), pos.getY(), pos.getZ());
		for (int y1 = pos.getY(); y1 <= ceiling; y1++) {
			p.setY(y1);
			if (isMeteorMaterial(world.getBlockState(p))) {
				mAbove++;
			}
		}
		return mAbove;
	}

	public int meteorsBelow(Level world, BlockPos pos)
	{
		int floor = pos.getY() - 6 * this.crashSize;
		int mBelow = 0;
		BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos(pos.getX(), pos.getY(), pos.getZ());
		for (int y1 = pos.getY(); y1 >= floor; y1--) {
			p.setY(y1);
			if (isMeteorMaterial(world.getBlockState(p))) {
				mBelow++;
			}
		}
		return mBelow;
	}

	private boolean isMeteorMaterial(BlockState state) {
		return state.is(this.meteorType.getMaterial()) || state.is(this.meteorType.getRareMaterial());
	}

	public int meteorsAboveAndBelow(Level world, BlockPos pos) {
		BlockState bl = world.getBlockState(pos);
		return 0 - (bl.is(meteorType.getRareMaterial()) ? 1 : meteorsAbove(world, pos) + meteorsBelow(world, pos) - (bl.is(meteorType.getMaterial()) ? 1 : 0));
	}

	protected boolean checkBlockIDs(Level world, BlockPos pos) {
		BlockState bl = world.getBlockState(pos);
		return (!bl.is(Blocks.BEDROCK) && !bl.is(meteorType.getMaterial()) &&
				!bl.is(meteorType.getRareMaterial()) && !bl.is(Blocks.WATER));
	}
}
