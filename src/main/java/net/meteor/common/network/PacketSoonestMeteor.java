package net.meteor.common.network;

import java.util.function.Supplier;

import net.meteor.common.ClientHandler;
import net.meteor.common.climate.GhostMeteor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

/** Where the next meteor will fall, for the Time Detector. */
public class PacketSoonestMeteor {

	private final BlockPos soonestMeteorLoc;

	public PacketSoonestMeteor(GhostMeteor gMeteor) {
		if (gMeteor != null) {
			this.soonestMeteorLoc = new BlockPos(gMeteor.x, 0, gMeteor.z);
		} else {
			this.soonestMeteorLoc = null;
		}
	}

	public PacketSoonestMeteor(FriendlyByteBuf buffer) {
		int x = buffer.readInt();
		int y = buffer.readInt();
		int z = buffer.readInt();
		this.soonestMeteorLoc = y == -1 ? null : new BlockPos(x, y, z);
	}

	public void toBytes(FriendlyByteBuf buffer) {
		if (soonestMeteorLoc == null) {
			buffer.writeInt(-1);
			buffer.writeInt(-1);
			buffer.writeInt(-1);
		} else {
			buffer.writeInt(soonestMeteorLoc.getX());
			buffer.writeInt(soonestMeteorLoc.getY());
			buffer.writeInt(soonestMeteorLoc.getZ());
		}
	}

	// Client Side
	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ClientHandler.nearestTimeLocation = soonestMeteorLoc;
	}

}
