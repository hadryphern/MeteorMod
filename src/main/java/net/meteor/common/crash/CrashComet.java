package net.meteor.common.crash;

import net.meteor.common.EnumMeteor;
import net.meteor.common.MeteorEntities;
import net.meteor.common.entity.EntityCometKitty;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

/** Comets leave a few blocks of their material behind. */
public class CrashComet {

	private final EnumMeteor meteorType;

	public CrashComet(EnumMeteor type) {
		this.meteorType = type;
	}

	public boolean generate(Level world, RandomSource random, int i, int j, int k) {

		if (meteorType == EnumMeteor.KITTY) {
			EntityCometKitty kitty = MeteorEntities.COMET_KITTY.get().create(world);
			if (kitty != null) {
				kitty.setPos(i, j + 1, k);
				world.addFreshEntity(kitty);
			}
		}

		int chance = 15;
		int blocksPlaced = 0;
		BlockPos last = new BlockPos(i, j, k);

		for (int y = j - 2; y <= j + 2; y++) {
			for (int x = i - 2; x <= i + 2; x++) {
				for (int z = k - 2; z <= k + 2; z++) {
					BlockPos pos = new BlockPos(x, y, z);
					if (world.getBlockState(pos).canBeReplaced()) {
						last = pos;
						if (random.nextInt(100) + 1 > chance) {
							world.setBlock(pos, EnumMeteor.withHeat(meteorType.getMaterial(), random.nextInt(4) + 1), 3);
							blocksPlaced++;
							chance = 15 + (20 * blocksPlaced);
						}
					}
				}
			}
		}

		// Helps to ensure a block gets placed if extremely unlucky
		if (blocksPlaced == 0) {
			if (world.getBlockState(last).canBeReplaced()) {
				world.setBlock(last, EnumMeteor.withHeat(meteorType.getMaterial(), random.nextInt(4) + 1), 3);
			}
		}

		return true;
	}

}
