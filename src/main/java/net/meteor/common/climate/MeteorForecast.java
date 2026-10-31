package net.meteor.common.climate;

import java.util.ArrayList;

import net.meteor.common.EnumMeteor;
import net.meteor.common.network.MeteorNetwork;
import net.meteor.common.network.PacketSoonestMeteor;
import net.minecraft.server.level.ServerLevel;

public class MeteorForecast {

	private final ServerLevel theWorld;
	private final ClimateUpdater worldTick;
	private final ArrayList<GhostMeteor> ghostMets;
	private final MeteorSavedData data;

	public MeteorForecast(ClimateUpdater wTick, ArrayList<GhostMeteor> gMets, MeteorSavedData data, ServerLevel world) {
		this.worldTick = wTick;
		this.ghostMets = gMets;
		this.data = data;
		this.theWorld = world;
	}

	public void setLastCrashLocation(CrashLocation loc) {
		this.data.lastCrash = loc;
	}

	public CrashLocation getLastCrashLocation() {
		return this.data.lastCrash;
	}

	public int getSecondsUntilNewMeteor() {
		return worldTick.getSecondsUntilNewMeteor();
	}

	public GhostMeteor getNearestTimeMeteor() {
		GhostMeteor closestMeteor = null;
		for (int i = 0; i < this.ghostMets.size(); i++) {
			if (closestMeteor != null) {
				if (this.ghostMets.get(i).type != EnumMeteor.KITTY) {
					int var1 = closestMeteor.getRemainingTicks();
					int var2 = this.ghostMets.get(i).getRemainingTicks();
					if (var2 < var1)
						closestMeteor = this.ghostMets.get(i);
				}
			} else if (this.ghostMets.get(i).type != EnumMeteor.KITTY) {
				closestMeteor = this.ghostMets.get(i);
			}

		}

		return closestMeteor;
	}

	public void updateNearestTimeForClients() {
		GhostMeteor gMeteor = getNearestTimeMeteor();
		MeteorNetwork.sendToDimension(new PacketSoonestMeteor(gMeteor), theWorld);
	}

	public void clearMeteors() {
		ghostMets.clear();
	}

}
