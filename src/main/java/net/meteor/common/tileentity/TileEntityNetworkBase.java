package net.meteor.common.tileentity;

import net.meteor.common.network.MeteorNetwork;
import net.meteor.common.network.PacketButtonPress;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class TileEntityNetworkBase extends BlockEntity {

	public TileEntityNetworkBase(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public void pressButton(int id) {
		if (level != null && level.isClientSide) {
			MeteorNetwork.sendToServer(new PacketButtonPress(this, id));
		}
	}

	public abstract void onButtonPress(int id);

	public void postButtonPress(int buttonID) {
		this.sendUpdate();
	}

	/** markDirty() + markBlockForUpdate() */
	public void sendUpdate() {
		this.setChanged();
		if (this.level != null) {
			this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
		}
	}

}
