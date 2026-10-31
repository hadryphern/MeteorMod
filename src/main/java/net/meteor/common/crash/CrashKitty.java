package net.meteor.common.crash;

import net.meteor.common.EnumMeteor;
import net.meteor.common.MeteorEntities;
import net.meteor.common.entity.EntityCometKitty;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

public class CrashKitty extends CrashMeteorite
{
	public CrashKitty(int Size, Explosion expl, EnumMeteor metType)
	{
		super(Size, expl, metType);
	}

	@Override
	public boolean generate(Level world, RandomSource random, int i, int j, int k) {
		return true;
	}

	@Override
	public void afterCrashCompleted(Level world, int i, int j, int k) {
		int kitties = (int)Math.pow(this.crashSize, 2.0D);
		for (int c = 0; c < kitties; c++) {
			EntityCometKitty kitty = MeteorEntities.COMET_KITTY.get().create(world);
			if (kitty != null) {
				kitty.setPos(i, j, k);
				world.addFreshEntity(kitty);
			}
		}
	}
}
