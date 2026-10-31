package net.meteor.common.network;

import java.util.function.Supplier;

import net.meteor.common.EnumMeteor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

/** Sent when a Meteor Shield destroys a meteor; the client bursts the meteor's block particles over the shield. */
public class PacketBlockedMeteor {

	public int x, y, z;
	public EnumMeteor type;

	public PacketBlockedMeteor(int x, int y, int z, EnumMeteor type) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.type = type;
	}

	public PacketBlockedMeteor(FriendlyByteBuf buffer) {
		this.x = buffer.readInt();
		this.y = buffer.readInt();
		this.z = buffer.readInt();
		this.type = EnumMeteor.getTypeFromID(buffer.readInt());
	}

	public void toBytes(FriendlyByteBuf buffer) {
		buffer.writeInt(x);
		buffer.writeInt(y);
		buffer.writeInt(z);
		buffer.writeInt(type.getID());
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> net.meteor.client.ClientPacketHandler.handleBlockedMeteor(this));
	}

}
