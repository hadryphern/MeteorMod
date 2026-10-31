package net.meteor.common.climate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.meteor.common.IMeteorShield;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

/**
 * Per-dimension saved data. Combines the three 1.7.10 WorldSavedData classes (GhostMeteorData,
 * CrashedChunkSetData and MeteorShieldSavedData) and keeps their tag names.
 */
public class MeteorSavedData extends SavedData {

	public static final String NAME = "meteors";

	public final ArrayList<GhostMeteor> ghostMets = new ArrayList<GhostMeteor>();
	public final ArrayList<CrashedChunkSet> crashedChunks = new ArrayList<CrashedChunkSet>();
	public final ArrayList<IMeteorShield> meteorShields = new ArrayList<IMeteorShield>();
	public CrashLocation lastCrash = null;

	public static MeteorSavedData load(CompoundTag tag) {
		MeteorSavedData data = new MeteorSavedData();
		for (int i = 1; i <= 3; i++) {
			if (tag.contains("GMet" + i)) {
				GhostMeteor gmet = GhostMeteor.fromNBTString(tag.getString("GMet" + i));
				if (gmet != null) {
					data.ghostMets.add(gmet);
				}
			}
		}
		for (int i = 1; i <= 20; i++) {
			if (tag.contains("CCSet" + i)) {
				CrashedChunkSet ccSet = CrashedChunkSet.fromNBTString(tag.getString("CCSet" + i));
				if (ccSet != null) {
					data.crashedChunks.add(ccSet);
				}
			}
		}
		data.lastCrash = CrashLocation.fromNBT(tag);
		int numShields = tag.getInt("numShields");
		for (int i = 0; i < numShields; i++) {
			CompoundTag sNBT = tag.getCompound("s" + i);
			data.meteorShields.add(new MeteorShieldData(sNBT.getInt("x"), sNBT.getInt("y"), sNBT.getInt("z"),
					sNBT.getInt("power"), sNBT.getString("owner"), sNBT.getBoolean("blockComets")));
		}
		return data;
	}

	@Override
	public CompoundTag save(CompoundTag tag) {
		Iterator<GhostMeteor> gIter = ghostMets.iterator();
		int i = 1;
		while (gIter.hasNext() && i <= 3) {
			tag.putString("GMet" + i, gIter.next().toString());
			i++;
		}

		Iterator<CrashedChunkSet> cIter = crashedChunks.iterator();
		i = 1;
		while (cIter.hasNext() && i <= 20) {
			tag.putString("CCSet" + i, cIter.next().toString());
			i++;
		}

		if (lastCrash != null) {
			lastCrash.toNBT(tag);
		}

		List<IMeteorShield> shields = meteorShields;
		tag.putInt("numShields", shields.size());
		for (i = 0; i < shields.size(); i++) {
			CompoundTag sNBT = new CompoundTag();
			IMeteorShield shield = shields.get(i);
			sNBT.putInt("x", shield.getX());
			sNBT.putInt("y", shield.getY());
			sNBT.putInt("z", shield.getZ());
			sNBT.putInt("power", shield.getPowerLevel());
			sNBT.putString("owner", shield.getOwner());
			sNBT.putBoolean("blockComets", shield.getPreventComets());
			tag.put("s" + i, sNBT);
		}
		return tag;
	}

	@Override
	public boolean isDirty() {
		// Meteors and shields change constantly; the 1.7.10 data always saved too
		return true;
	}

}
