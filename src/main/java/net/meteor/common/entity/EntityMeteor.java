package net.meteor.common.entity;

import java.util.List;

import net.meteor.common.ClientHandler;
import net.meteor.common.EnumMeteor;
import net.meteor.common.IMeteorShield;
import net.meteor.common.MeteorConfig;
import net.meteor.common.MeteorDamageTypes;
import net.meteor.common.MeteorEntities;
import net.meteor.common.MeteorTriggers;
import net.meteor.common.climate.CrashLocation;
import net.meteor.common.climate.GhostMeteor;
import net.meteor.common.climate.HandlerMeteor;
import net.meteor.common.climate.HandlerWorld;
import net.meteor.common.crash.CrashFrezarite;
import net.meteor.common.crash.CrashKitty;
import net.meteor.common.crash.CrashKreknorite;
import net.meteor.common.crash.CrashMeteorite;
import net.meteor.common.crash.CrashUnknown;
import net.meteor.common.network.MeteorNetwork;
import net.meteor.common.network.PacketLastCrash;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

public class EntityMeteor extends Entity implements IEntityAdditionalSpawnData
{
	/** In 1.7.10 an entity's position was the middle of its box (yOffset = height / 2); it is the bottom now. */
	public static final double Y_OFFSET = 0.49D;

	public int size = 1;
	public EnumMeteor meteorType;
	public int spawnPauseTicks = 0;

	private int originX;
	private int originZ;

	protected boolean summoned = false;

	public EntityMeteor(EntityType<? extends EntityMeteor> type, Level world) {
		super(type, world);
		this.blocksBuilding = true;
		this.meteorType = EnumMeteor.METEORITE;

		this.setDeltaMovement((random.nextDouble() - random.nextDouble()) * 1.2D, 0.0D, (random.nextDouble() - random.nextDouble()) * 1.2D);
		this.setYRot((float)(Math.random() * 360D));
		this.setXRot((float)(Math.random() * 360D));
	}

	public EntityMeteor(Level world, int mSize, double x, double z, EnumMeteor metType, boolean summon) {
		this(MeteorEntities.FALLING_METEOR.get(), world);
		this.size = mSize;
		this.meteorType = metType;
		this.summoned = summon;
		this.originX = (int)x;
		this.originZ = (int)z;
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
	public boolean isPickable()
	{
		return !this.isRemoved();
	}

	@Override
	public boolean shouldRenderAtSqrDistance(double distance) {
		return distance < 16384.0D;
	}

	@Override
	public void tick()
	{
		if (!this.summoned) {
			if (!level().isClientSide) {
				HandlerMeteor metHandler = HandlerWorld.get(level());
				IMeteorShield shield = metHandler == null ? null : metHandler.getShieldManager().getClosestShieldInRange((int)getX(), (int)getZ());
				if (shield != null) {
					String owner = shield.getOwner();
					Player playerOwner = HandlerMeteor.getPlayerByName(level(), owner);
					if (playerOwner != null) {
						playerOwner.sendSystemMessage(ClientHandler.createMessage(Component.translatable("MeteorShield.meteorBlocked"), ChatFormatting.GREEN));
						MeteorTriggers.trigger(playerOwner, MeteorTriggers.METEOR_BLOCKED);
					}
					metHandler.getShieldManager().sendMeteorMaterialsToShield(shield, new GhostMeteor((int)getX(), (int)getZ(), size, 0, meteorType));
					this.level().playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 5F, (1.0F + (level().random.nextFloat() - level().random.nextFloat()) * 0.2F) * 0.7F);
					((ServerLevel)level()).sendParticles(ParticleTypes.EXPLOSION_EMITTER, getX(), getY() + Y_OFFSET, getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);

					CrashLocation lastCrash = metHandler.getForecast().getLastCrashLocation();
					if (lastCrash != null && lastCrash.x == originX && lastCrash.z == originZ) {
						metHandler.getForecast().setLastCrashLocation(null);
						MeteorNetwork.sendToDimension(new PacketLastCrash(new CrashLocation(-1, -1, -1, false, null)), (ServerLevel)level());
					}

					this.discard();
					return;
				}
			}
		}

		xRotO = getXRot();
		yRotO = getYRot();
		setXRot((float)((getXRot() + 3D) % 360D));
		setYRot((float)((getXRot() + 3D) % 360D));

		if (this.spawnPauseTicks > 0) {
			this.spawnPauseTicks -= 1;
			return;
		}

		Vec3 motion = getDeltaMovement();
		if (summoned) {
			motion = new Vec3(0.0D, motion.y, 0.0D);
		}
		xo = getX();
		yo = getY();
		zo = getZ();
		motion = motion.add(0.0D, -0.039999999105930328D, 0.0D);
		setDeltaMovement(motion);
		move(MoverType.SELF, getDeltaMovement());
		setDeltaMovement(getDeltaMovement().multiply(1.0D, 0.98000001907348633D, 1.0D));

		if (onGround()) {
			discard();
			if(!level().isClientSide) {
				int x = Mth.floor(getX());
				int y = Mth.floor(getY());
				int z = Mth.floor(getZ());
				if (!summoned) {

					AABB aabb = new AABB(getX() - 40D, getY() - 20D, getZ() - 40D, getX() + 40D, getY() + 20D, getZ() + 40D);
					List<Player> players = this.level().getEntitiesOfClass(Player.class, aabb);
					for (Player player : players) {
						MeteorTriggers.trigger(player, MeteorTriggers.FOUND_METEOR);
					}

					HandlerMeteor metHandler = HandlerWorld.get(level());
					if (metHandler != null) {
						CrashLocation cc = metHandler.getForecast().getLastCrashLocation();
						if (cc != null && originX == cc.x && originZ == cc.z) {
							metHandler.getForecast().setLastCrashLocation(new CrashLocation(x, y, z, false, cc.prevCrash));
							MeteorNetwork.sendToDimension(new PacketLastCrash(metHandler.getForecast().getLastCrashLocation()), (ServerLevel)level());
						}
					}

				}
				CrashMeteorite worldGen = getWorldGen();
				if (worldGen.generate(level(), random, x, y, z)) {
					worldGen.afterCrashCompleted(level(), x, y, z);
				}
			}
		} else if (level().isClientSide) {
			if (size == 1) {
				level().addParticle(ParticleTypes.EXPLOSION, getX(), getY() + Y_OFFSET + 2.75D, getZ(), 0.0D, 0.0D, 0.0D);
			} else {
				level().addParticle(ParticleTypes.EXPLOSION_EMITTER, getX(), getY() + Y_OFFSET + 4.0D, getZ(), 0.0D, 0.0D, 0.0D);
			}
		}

		if (getY() < level().getMinBuildHeight() - 64) {
			// Fell into the void (The End)
			discard();
		}
	}

	protected Explosion explode() {
		float f = (float) (this.size * MeteorConfig.impactExplosionMultiplier.get());
		// BLOCK interaction drops blocks with a chance of 1 / size, like 1.7.10's doExplosionB(true)
		return level().explode(this, MeteorDamageTypes.meteor(level(), this), null, getX(), getY(), getZ(), f, meteorType.getFieryExplosion(), Level.ExplosionInteraction.BLOCK);
	}

	protected CrashMeteorite getWorldGen() {
		switch (this.meteorType.getID()) {
		case 0:
			return new CrashMeteorite(this.size, explode(), this.meteorType);
		case 1:
			return new CrashFrezarite(this.size, explode(), this.meteorType);
		case 2:
			return new CrashKreknorite(this.size, explode(), this.meteorType);
		case 3:
			return new CrashUnknown(this.size, explode(), this.meteorType);
		case 4:
			return new CrashKitty(this.size, explode(), this.meteorType);
		}
		return new CrashMeteorite(this.size, explode(), this.meteorType);
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag nbttagcompound)
	{
		nbttagcompound.putInt("size", this.size);
		nbttagcompound.putBoolean("summoned", this.summoned);
		nbttagcompound.putInt("metTypeID", this.meteorType.getID());
		nbttagcompound.putInt("pauseTicks", this.spawnPauseTicks);
		nbttagcompound.putInt("originX", originX);
		nbttagcompound.putInt("originZ", originZ);
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag nbttagcompound)
	{
		this.size = nbttagcompound.getInt("size");
		this.summoned = nbttagcompound.getBoolean("summoned");
		this.meteorType = EnumMeteor.getTypeFromID(nbttagcompound.getInt("metTypeID"));
		this.spawnPauseTicks = nbttagcompound.getInt("pauseTicks");
		this.originX = nbttagcompound.getInt("originX");
		this.originZ = nbttagcompound.getInt("originZ");
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public void writeSpawnData(FriendlyByteBuf buffer) {
		buffer.writeInt(this.meteorType.getID());
		buffer.writeInt(this.size);
		buffer.writeInt(this.spawnPauseTicks);
		buffer.writeBoolean(this.summoned);
	}

	@Override
	public void readSpawnData(FriendlyByteBuf additionalData) {
		this.meteorType = EnumMeteor.getTypeFromID(additionalData.readInt());
		this.size = additionalData.readInt();
		this.spawnPauseTicks = additionalData.readInt();
		this.summoned = additionalData.readBoolean();
	}
}
