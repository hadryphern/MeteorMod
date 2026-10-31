package net.meteor.common.climate;

import net.minecraft.nbt.CompoundTag;

public class CrashLocation {

	public final int x;
	public final int y;
	public final int z;
	public final boolean inOrbit;

	public final CrashLocation prevCrash;

	public CrashLocation(int x, int y, int z, boolean inOrbit, CrashLocation prevCrash) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.inOrbit = inOrbit;
		this.prevCrash = prevCrash;
	}

	public void toNBT(CompoundTag nbt) {
		nbt.putInt("LCX", x);
		nbt.putInt("LCY", y);
		nbt.putInt("LCZ", z);
		nbt.putBoolean("LCO", inOrbit);
	}

	public static CrashLocation fromNBT(CompoundTag nbt) {
		int x = nbt.getInt("LCX");
		int y = nbt.getInt("LCY");
		int z = nbt.getInt("LCZ");
		boolean orbit = nbt.getBoolean("LCO");
		if (x == 0 && y == 0 && z == 0) {
			return null;
		}
		return new CrashLocation(x, y, z, orbit, null);
	}

}
