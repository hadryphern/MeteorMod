package net.meteor.common;

public interface IMeteorShield {

	public int getRange();

	public int getPowerLevel();

	public int getX();

	public int getY();

	public int getZ();

	public boolean isTileEntity();

	public String getOwner();

	public boolean getPreventComets();

	/** Shields are identified by their position, the old versions overrode equals() for this. */
	public static boolean samePosition(IMeteorShield a, IMeteorShield b) {
		return a.getX() == b.getX() && a.getY() == b.getY() && a.getZ() == b.getZ();
	}

}
