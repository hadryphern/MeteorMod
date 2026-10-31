package net.meteor.common.network;

import java.util.function.Supplier;

import net.meteor.common.ClientHandler;
import net.meteor.common.climate.GhostMeteor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

/** Adds or removes a meteor in orbit on the client. An empty packet (all -1) clears them. */
public class PacketGhostMeteor {

	private boolean addGhostMeteor;
	private int x;
	private int y;
	private int z;

	public PacketGhostMeteor() {
		this.addGhostMeteor = false;
		this.x = this.y = this.z = -1;
	}

	public PacketGhostMeteor(boolean adding, GhostMeteor gMet) {
		this(adding, gMet.x, 0, gMet.z);
	}

	public PacketGhostMeteor(boolean adding, int x, int y, int z) {
		this.addGhostMeteor = adding;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public PacketGhostMeteor(FriendlyByteBuf buffer) {
		this.addGhostMeteor = buffer.readBoolean();
		this.x = buffer.readInt();
		this.y = buffer.readInt();
		this.z = buffer.readInt();
	}

	public void toBytes(FriendlyByteBuf buffer) {
		buffer.writeBoolean(addGhostMeteor);
		buffer.writeInt(x);
		buffer.writeInt(y);
		buffer.writeInt(z);
	}

	// Client Side
	public void handle(Supplier<NetworkEvent.Context> ctx) {
		BlockPos cc = new BlockPos(x, y, z);
		if (addGhostMeteor) {
			ClientHandler.ghostMetLocs.add(cc);
		} else if (cc.getX() == -1 && cc.getY() == -1 && cc.getZ() == -1) {
			ClientHandler.ghostMetLocs.clear();
		} else {
			for (int i = 0; i < ClientHandler.ghostMetLocs.size(); i++) {
				BlockPos cc2 = ClientHandler.ghostMetLocs.get(i);
				if (cc2.getX() == cc.getX() && cc2.getZ() == cc.getZ()) {
					ClientHandler.ghostMetLocs.remove(i);
					break;
				}
			}
		}
	}

}
