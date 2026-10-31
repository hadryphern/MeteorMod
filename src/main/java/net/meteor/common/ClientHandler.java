package net.meteor.common;

import java.util.ArrayList;

import net.meteor.common.climate.CrashLocation;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;

/**
 * What the client knows about incoming meteors, filled in by the packets in net.meteor.common.network.
 * Read by the detectors and their textures. Contains no client-only classes so items can use it.
 */
public class ClientHandler
{
	public static CrashLocation lastCrashLocation = null;
	public static BlockPos nearestTimeLocation = null;
	public static final ArrayList<BlockPos> ghostMetLocs = new ArrayList<BlockPos>();

	public static BlockPos getClosestIncomingMeteor(double pX, double pZ) {
		BlockPos coords = null;
		double y = 50.0D;
		for (int i = 0; i < ghostMetLocs.size(); i++) {
			if (coords != null) {
				BlockPos loc = ghostMetLocs.get(i);
				double var1 = getDistance(pX, y, pZ, loc.getX(), y, loc.getZ());
				double var2 = getDistance(pX, y, pZ, coords.getX(), y, coords.getZ());
				if (var1 < var2)
					coords = loc;
			}
			else {
				coords = ghostMetLocs.get(i);
			}
		}
		return coords;
	}

	private static double getDistance(double x1, double y1, double z1, double x2, double y2, double z2) {
		double var7 = x1 - x2;
		double var9 = y1 - y2;
		double var11 = z1 - z2;
		return Mth.sqrt((float)(var7 * var7 + var9 * var9 + var11 * var11));
	}

	public static MutableComponent createMessage(Component s, ChatFormatting ecf) {
		return s.copy().withStyle(ecf);
	}

	public static void reset() {
		lastCrashLocation = null;
		nearestTimeLocation = null;
		ghostMetLocs.clear();
	}

}
