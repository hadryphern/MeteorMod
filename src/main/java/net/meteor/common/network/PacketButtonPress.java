package net.meteor.common.network;

import java.util.function.Supplier;

import net.meteor.common.tileentity.TileEntityNetworkBase;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

/** A GUI button press on a Meteor Shield or Freezer. The dimension is the sender's, so it isn't sent. */
public class PacketButtonPress {

	private final BlockPos pos;
	private final int buttonID;

	public PacketButtonPress(TileEntityNetworkBase tile, int buttonID) {
		this.pos = tile.getBlockPos();
		this.buttonID = buttonID;
	}

	public PacketButtonPress(FriendlyByteBuf buffer) {
		this.pos = buffer.readBlockPos();
		this.buttonID = buffer.readInt();
	}

	public void toBytes(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(pos);
		buffer.writeInt(buttonID);
	}

	// Server side
	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ServerPlayer player = ctx.get().getSender();
		if (player == null || !player.level().isLoaded(pos) || player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) > 64.0D) {
			return;
		}
		if (player.level().getBlockEntity(pos) instanceof TileEntityNetworkBase tileEntity) {
			tileEntity.onButtonPress(buttonID);
			tileEntity.postButtonPress(buttonID);
		}
	}

}
