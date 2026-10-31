package net.meteor.common.entity;

import net.meteor.common.ClientHandler;
import net.meteor.common.EnumMeteor;
import net.meteor.common.IMeteorShield;
import net.meteor.common.MeteorConfig;
import net.meteor.common.MeteorEntities;
import net.meteor.common.MeteorItems;
import net.meteor.common.MeteorTriggers;
import net.meteor.common.climate.HandlerMeteor;
import net.meteor.common.climate.HandlerWorld;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

/** The thrown Meteor Summoner. Where it lands, a meteor is summoned. */
public class EntitySummoner extends ThrowableItemProjectile implements IEntityAdditionalSpawnData
{
	private static final float[][] spellRGB = {
		{0.4352941176470588F, 0.0784313725490196F, 0.6588235294117647F},
		{0.0588235294117647F, 0.6784313725490196F, 0.6784313725490196F},
		{0.6941176470588235F, 0.0470588235294118F, 0.0470588235294118F},
		{0.392156862745098F, 0.3725490196078431F, 0.3450980392156863F},
		{0.0941176470588235F, 0.6470588235294118F, 0.0941176470588235F} };

	public int mID;
	public boolean isRandom;

	public EntitySummoner(EntityType<? extends EntitySummoner> type, Level world)
	{
		super(type, world);
	}

	public EntitySummoner(Level world, LivingEntity entityliving, int meteorID, boolean r)
	{
		super(MeteorEntities.METEOR_SUMMONER.get(), entityliving, world);
		this.mID = meteorID;
		this.isRandom = r;
		this.setItem(new ItemStack(getSummonerItem(meteorID, r)));
	}

	public static Item getSummonerItem(int meteorID, boolean random) {
		if (random) {
			return MeteorItems.itemMeteorSummoner.get();
		}
		switch (meteorID) {
		case 0: return MeteorItems.itemMeteoriteSummoner.get();
		case 1: return MeteorItems.itemFrezariteSummoner.get();
		case 2: return MeteorItems.itemKreknoriteSummoner.get();
		case 3: return MeteorItems.itemUnknownSummoner.get();
		case 4: return MeteorItems.itemKittySummoner.get();
		default: return MeteorItems.itemMeteorSummoner.get();
		}
	}

	@Override
	protected Item getDefaultItem() {
		return MeteorItems.itemMeteorSummoner.get();
	}

	@Override
	public void tick()
	{
		super.tick();
		if (this.level().isClientSide) {
			int rgbIndex = Math.max(0, Math.min(mID, spellRGB.length - 1));
			if (isRandom) {
				rgbIndex = this.level().random.nextInt(5);
			}
			level().addParticle(ParticleTypes.ENTITY_EFFECT, this.getX(), this.getY(), this.getZ(), spellRGB[rgbIndex][0], spellRGB[rgbIndex][1], spellRGB[rgbIndex][2]);
		}
	}

	@Override
	public void handleEntityEvent(byte id) {
		if (id == 3) {
			for (int i = 0; i < 8; i++)
			{
				this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.SNOWBALL)), getX(), getY(), getZ(), 0.0D, 0.0D, 0.0D);
			}
		}
	}

	@Override
	protected void onHit(HitResult movingobjectposition)
	{
		super.onHit(movingobjectposition);
		if (this.level().isClientSide) {
			return;
		}
		this.level().broadcastEntityEvent(this, (byte)3);

		Player player = this.getOwner() instanceof Player p ? p : null;

		if (!MeteorConfig.isDimensionWhitelisted(level().dimension())) {
			if (player != null) {
				player.sendSystemMessage(Component.translatable("MeteorSummoner.wrongDimension"));
				giveBack(player);
			}

			this.discard();
			return;
		}

		boolean canHit = true;

		if (!level().getGameRules().getBoolean(HandlerWorld.SUMMON_METEORS_GAMERULE)) {
			canHit = false;
			if (player != null) {
				player.sendSystemMessage(ClientHandler.createMessage(Component.translatable("MeteorSummoner.cannotSummon"), ChatFormatting.RED));
				giveBack(player);
			}
		} else if ((!MeteorConfig.allowSummonedMeteorGrief.get()) && (player != null)) {
			HandlerMeteor metHandler = HandlerWorld.get(level());
			IMeteorShield shield = metHandler == null ? null : metHandler.getShieldManager().getClosestShieldInRange((int)this.getX(), (int)this.getZ());
			if (shield != null && (!player.getGameProfile().getName().equalsIgnoreCase(shield.getOwner()))) {
				canHit = false;
				player.sendSystemMessage(Component.translatable("MeteorSummoner.landProtected"));
				giveBack(player);
			}

		}

		if (canHit) {
			if (player != null) {
				player.sendSystemMessage(ClientHandler.createMessage(Component.translatable("MeteorSummoner.incomingMeteor"), ChatFormatting.LIGHT_PURPLE));
				MeteorTriggers.trigger(player, MeteorTriggers.SUMMON_METEOR);
			}
			EntityMeteor meteorToSpawn = new EntityMeteor(this.level(), HandlerMeteor.getMeteorSize(), this.getX(), this.getZ(), EnumMeteor.getTypeFromID(this.mID), true);
			this.level().addFreshEntity(meteorToSpawn);
		}
		this.discard();
	}

	private void giveBack(Player player) {
		if (!player.getAbilities().instabuild) {
			player.getInventory().placeItemBackInInventory(new ItemStack(getSummonerItem(this.mID, this.isRandom)));
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag par1NBTTagCompound)
	{
		super.addAdditionalSaveData(par1NBTTagCompound);
		par1NBTTagCompound.putInt("metType", this.mID);
		par1NBTTagCompound.putBoolean("isRandom", this.isRandom);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag par1NBTTagCompound)
	{
		super.readAdditionalSaveData(par1NBTTagCompound);
		this.mID = par1NBTTagCompound.getInt("metType");
		this.isRandom = par1NBTTagCompound.getBoolean("isRandom");
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public void writeSpawnData(FriendlyByteBuf buffer) {
		buffer.writeInt(this.mID);
		buffer.writeBoolean(this.isRandom);
	}

	@Override
	public void readSpawnData(FriendlyByteBuf additionalData) {
		this.mID = additionalData.readInt();
		this.isRandom = additionalData.readBoolean();
	}

}
