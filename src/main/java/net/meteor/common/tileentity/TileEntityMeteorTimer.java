package net.meteor.common.tileentity;

import net.meteor.common.MeteorBlockEntities;
import net.meteor.common.MeteorConfig;
import net.meteor.common.block.BlockMeteorTimer;
import net.meteor.common.climate.GhostMeteor;
import net.meteor.common.climate.HandlerMeteor;
import net.meteor.common.climate.HandlerWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityMeteorTimer extends BlockEntity {

	public boolean quickMode;

	public TileEntityMeteorTimer(BlockPos pos, BlockState state) {
		super(MeteorBlockEntities.METEOR_TIMER.get(), pos, state);
		this.quickMode = false;
	}

	public static void tick(Level world, BlockPos pos, BlockState state, TileEntityMeteorTimer timer) {
		timer.updateEntity();
	}

	private void updateEntity() {
		// Do calculations for closeness of meteor falling to a power value of 0 to 15
		// Then check if that value is different then when it was updated last
		// If so, update the block so the redstone output gets changed
		if (this.level != null && !this.level.isClientSide && level.getGameTime() % 20L == 0L) {
			HandlerMeteor metHandler = HandlerWorld.get(level);
			if (metHandler == null) {
				return;
			}
			GhostMeteor gMet = metHandler.getForecast().getNearestTimeMeteor();
			int ticksLeft;
			if (gMet != null) {
				ticksLeft = gMet.getRemainingTicks();
			} else {
				updateMeta(0);
				return;
			}
			int calc;
			if (ticksLeft < 450 && MeteorConfig.meteorsFallOnlyAtNight.get()) {
				long time = level.getDayTime() % 24000L;
				int timeTicks = ticksLeft;
				if (time < 12000L) {
					timeTicks += (int)((12000L - time) / 20);
				} else if ((24000L - time) / 20 < ticksLeft) {
					timeTicks += 600;
				}
				calc = (timeTicks / 30);
			} else {
				calc = (ticksLeft / 30);
			}
			if (calc > 15) calc = 15;
			int meta = 15 - calc;
			meta = Mth.clamp(meta, 0, 15);
			if (quickMode) {
				if (meta == 15) {
					updateMeta(meta);
				} else {
					updateMeta(0);
				}
			} else {
				updateMeta(meta);
			}
		}
	}

	private void updateMeta(int meta) {
		BlockState state = this.getBlockState();
		if (state.getValue(BlockMeteorTimer.POWER) != meta) {
			this.level.setBlock(this.worldPosition, state.setValue(BlockMeteorTimer.POWER, meta), 3);
		}
	}

	@Override
	protected void saveAdditional(CompoundTag nbt)
	{
		super.saveAdditional(nbt);
		nbt.putBoolean("mode", quickMode);
	}

	@Override
	public void load(CompoundTag nbt)
	{
		super.load(nbt);
		this.quickMode = nbt.getBoolean("mode");
	}

	@Override
	public CompoundTag getUpdateTag() {
		return this.saveWithoutMetadata();
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

}
