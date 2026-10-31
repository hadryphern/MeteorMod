package net.meteor.common.network;

import java.util.function.Supplier;

import net.meteor.common.ClientHandler;
import net.meteor.common.climate.CrashLocation;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

/** The last crash location, for the Crash Detector. All -1 means "go back to the previous crash". */
public class PacketLastCrash {

	private final int x, y, z;
	private final boolean inOrbit;

	public PacketLastCrash(CrashLocation loc) {
		if (loc == null) {
			loc = new CrashLocation(-1, -1, -1, false, null);
		}
		this.x = loc.x;
		this.y = loc.y;
		this.z = loc.z;
		this.inOrbit = loc.inOrbit;
	}

	public PacketLastCrash(FriendlyByteBuf buffer) {
		this.x = buffer.readInt();
		this.y = buffer.readInt();
		this.z = buffer.readInt();
		this.inOrbit = buffer.readBoolean();
	}

	public void toBytes(FriendlyByteBuf buffer) {
		buffer.writeInt(x);
		buffer.writeInt(y);
		buffer.writeInt(z);
		buffer.writeBoolean(inOrbit);
	}

	// Client Side
	public void handle(Supplier<NetworkEvent.Context> ctx) {
		if (x == -1 && y == -1 && z == -1) {
			if (ClientHandler.lastCrashLocation != null) {
				ClientHandler.lastCrashLocation = ClientHandler.lastCrashLocation.prevCrash;
			} else {
				ClientHandler.lastCrashLocation = null;
			}
		} else {
			ClientHandler.lastCrashLocation = new CrashLocation(x, y, z, inOrbit, ClientHandler.lastCrashLocation);
		}
	}

}
