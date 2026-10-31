package net.meteor.common.entity;

import net.meteor.common.EnumMeteor;
import net.meteor.common.MeteorEntities;
import net.meteor.common.crash.CrashComet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

public class EntityComet extends Entity implements IEntityAdditionalSpawnData {

	public EnumMeteor meteorType;
	public int spawnPauseTicks = 0;

	public EntityComet(EntityType<? extends EntityComet> type, Level world) {
		super(type, world);
		this.blocksBuilding = true;
		this.meteorType = EnumMeteor.METEORITE;

		this.setDeltaMovement((random.nextDouble() - random.nextDouble()) * 1.2D, 0.0D, (random.nextDouble() - random.nextDouble()) * 1.2D);
		this.setYRot((float)(Math.random() * 360D));
		this.setXRot((float)(Math.random() * 360D));
	}

	public EntityComet(Level world, double x, double z, EnumMeteor metType) {
		this(MeteorEntities.FALLING_COMET.get(), world);
		this.meteorType = metType;
		this.setPos(x, 250.0D, z);
		this.xo = x;
		this.yo = 250.0D;
		this.zo = z;
	}

	@Override
	protected void defineSynchedData() {}

	@Override
	protected MovementEmission getMovementEmission() {
		return MovementEmission.NONE;
	}

	@Override
	public boolean isPickable() {
		return !this.isRemoved();
	}

	@Override
	public boolean shouldRenderAtSqrDistance(double distance) {
		return distance < 16384.0D;
	}

	@Override
	public void tick() {
		if (!this.level().dimensionType().natural()) {
			this.discard();
			return;
		}

		xRotO = getXRot();
		yRotO = getYRot();
		setXRot((float)((getXRot() + 3D) % 360D));
		setYRot((float)((getXRot() + 3D) % 360D));

		if (this.spawnPauseTicks > 0) {
			this.spawnPauseTicks--;
			return;
		}

		xo = getX();
		yo = getY();
		zo = getZ();
		setDeltaMovement(getDeltaMovement().add(0.0D, -0.039999999105930328D, 0.0D));
		move(MoverType.SELF, getDeltaMovement());
		setDeltaMovement(getDeltaMovement().multiply(1.0D, 0.98000001907348633D, 1.0D));

		if (onGround()) {
			discard();
			if(!level().isClientSide) {
				level().explode(this, getX(), getY(), getZ(), 0.5F, false, Level.ExplosionInteraction.BLOCK);
				CrashComet crash = new CrashComet(this.meteorType);
				crash.generate(level(), random, Mth.floor(getX()), Mth.floor(getY()), Mth.floor(getZ()));
			}
		} else if (level().isClientSide) {
			level().addParticle(ParticleTypes.EXPLOSION, getX(), getY() + EntityMeteor.Y_OFFSET + 2.5D, getZ(), 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag nbttagcompound) {
		this.meteorType = EnumMeteor.getTypeFromID(nbttagcompound.getInt("metTypeID"));
		this.spawnPauseTicks = nbttagcompound.getInt("pauseTicks");
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag nbttagcompound) {
		nbttagcompound.putInt("metTypeID", this.meteorType.getID());
		nbttagcompound.putInt("pauseTicks", this.spawnPauseTicks);
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public void writeSpawnData(FriendlyByteBuf buffer) {
		buffer.writeInt(this.meteorType.getID());
		buffer.writeInt(this.spawnPauseTicks);
	}

	@Override
	public void readSpawnData(FriendlyByteBuf additionalData) {
		this.meteorType = EnumMeteor.getTypeFromID(additionalData.readInt());
		this.spawnPauseTicks = additionalData.readInt();
	}

}
