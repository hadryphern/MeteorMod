package net.meteor.common.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.meteor.common.ClientHandler;
import net.meteor.common.EnumMeteor;
import net.meteor.common.IMeteorShield;
import net.meteor.common.MeteorBlockEntities;
import net.meteor.common.MeteorConfig;
import net.meteor.common.MeteorItems;
import net.meteor.common.MeteorParticles;
import net.meteor.common.MeteorSounds;
import net.meteor.common.MeteorTriggers;
import net.meteor.common.block.container.ContainerMeteorShield;
import net.meteor.common.climate.HandlerMeteor;
import net.meteor.common.climate.HandlerWorld;
import net.meteor.common.climate.MeteorShieldData;
import net.meteor.common.entity.EntityComet;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class TileEntityMeteorShield extends TileEntityNetworkBase implements WorldlyContainer, MenuProvider, IMeteorShield
{
	public static final int CHARGE_TIME = 1600;

	private static final int[] OUTPUT_SLOTS = { 5, 6, 7, 8, 9, 10, 11, 12 };

	private boolean shieldedChunks;
	private boolean blockComets;
	public String owner = "None";
	private int range;
	private int powerLevel;
	private int cometX;
	private int cometZ;
	private int cometType = -1;
	public int age;

	// Slot 0 takes a Meteor Chip to charge the shield, 1 - 4 hold Red Meteor Gems and 5 - 12 hold blocked meteor materials
	private NonNullList<ItemStack> inv = NonNullList.withSize(13, ItemStack.EMPTY);

	public TileEntityMeteorShield(BlockPos pos, BlockState state) {
		super(MeteorBlockEntities.METEOR_SHIELD.get(), pos, state);
		this.range = 0;
		this.powerLevel = 0;
		this.age = 0;
		this.shieldedChunks = false;
		this.blockComets = false;
	}

	public static void tick(Level world, BlockPos pos, BlockState state, TileEntityMeteorShield shield) {
		shield.updateEntity();
	}

	private void updateEntity()
	{
		++age;
		if (!this.shieldedChunks) {
			if (powerLevel > 0) {
				HandlerMeteor metHandler = HandlerWorld.get(level);
				if (metHandler != null) {
					metHandler.getShieldManager().addShield(this);
				}
				this.shieldedChunks = true;
				this.sendUpdate();
			} else if (level.isClientSide) {
				GenerateParticles(level, worldPosition, level.random);
			} else if (age >= CHARGE_TIME) {
				setCharged();
				if (owner != null && owner.length() > 0) {
					Player player = HandlerMeteor.getPlayerByName(level, owner);
					if (player != null) {
						player.sendSystemMessage(ClientHandler.createMessage(Component.translatable("MeteorShield.howToUpgrade"), ChatFormatting.GOLD));
					}
				}
			}
		}
		if (this.shieldedChunks) {
			range = powerLevel * MeteorConfig.safeGet(MeteorConfig.shieldRadiusMultiplier, 64);	// update range (may load as 0 for old updates, thus check with this)
		}
	}

	public void setCharged() {
		if (powerLevel == 0) {
			powerLevel = 1;
			this.setChanged();
		}
	}

	@Override
	public boolean getPreventComets() {
		return this.blockComets;
	}

	public EnumMeteor getCometType() {
		if (cometType == -1) return EnumMeteor.METEORITE;
		return EnumMeteor.getTypeFromID(cometType);
	}

	public List<Component> getDisplayInfo() {
		List<Component> info = new ArrayList<Component>();
		if (powerLevel == 0) {
			info.add(Component.translatable("info.meteorShield.charging"));
			info.add(Component.translatable("info.meteorShield.charged", (int)((float)age / (float)CHARGE_TIME * 100)));
		} else {
			info.add(Component.translatable("info.meteorShield.powerLevel", powerLevel, 5));
			info.add(Component.translatable("info.meteorShield.range", range));
		}
		info.add(Component.translatable("info.meteorShield.owner", owner));
		if (powerLevel != 0) {
			if (blockComets) {
				info.add(Component.translatable("info.meteorShield.cometsBeingBlocked"));
			} else {
				if (cometType != -1) {
					info.add(Component.translatable("info.meteorShield.cometEnteredOrbit"));
					info.add(Component.translatable("info.meteorShield.cometX", cometX));
					info.add(Component.translatable("info.meteorShield.cometZ", cometZ));
				} else {
					info.add(Component.translatable("info.meteorShield.noComets"));
				}
			}
		}
		return info;
	}

	public void addMeteorMaterials(List<ItemStack> items) {
		for (ItemStack par1ItemStack : items) {
			ItemStack itemstack1;
			int k = 5;
			if (par1ItemStack.isStackable())
			{
				while (!par1ItemStack.isEmpty() && k >= 5 && k < this.getContainerSize())
				{
					itemstack1 = inv.get(k);
					if (!itemstack1.isEmpty() && ItemStack.isSameItemSameTags(par1ItemStack, itemstack1))
					{
						int l = itemstack1.getCount() + par1ItemStack.getCount();
						if (l <= par1ItemStack.getMaxStackSize())
						{
							par1ItemStack.setCount(0);
							itemstack1.setCount(l);
						}
						else if (itemstack1.getCount() < par1ItemStack.getMaxStackSize())
						{
							par1ItemStack.shrink(par1ItemStack.getMaxStackSize() - itemstack1.getCount());
							itemstack1.setCount(par1ItemStack.getMaxStackSize());
						}
					}
					++k;
				}
			}
			if (!par1ItemStack.isEmpty())
			{
				k = 5;
				while (k >= 5 && k < this.getContainerSize())
				{
					itemstack1 = inv.get(k);
					if (itemstack1.isEmpty())
					{
						inv.set(k, par1ItemStack.copy());
						par1ItemStack.setCount(0);
						break;
					}
					++k;
				}
			}
		}
		this.sendUpdate();
	}

	public void detectComet(EntityComet comet) {
		this.cometX = (int)comet.getX();
		this.cometZ = (int)comet.getZ();
		this.cometType = comet.meteorType.getID();
		this.sendUpdate();
	}

	private void GenerateParticles(Level world, BlockPos pos, RandomSource random)
	{
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		if (world.getBlockState(pos.above()).isSolidRender(world, pos.above())) return;
		for (int currX = x - 2; currX <= x + 2; currX++)
		{
			for (int currZ = z - 2; currZ <= z + 2; currZ++)
			{
				if ((currX > x - 2) && (currX < x + 2) && (currZ == z - 1))
				{
					currZ = z + 2;
				}
				if (random.nextInt(100) == 25)
				{
					for (int currY = y; currY <= y + 1; currY++)
					{
						if (!world.isEmptyBlock(new BlockPos((currX - x) / 2 + x, currY, (currZ - z) / 2 + z)))
						{
							break;
						}
						world.addParticle(MeteorParticles.METEOR_SHIELD.get(), x + 0.5D, y + 2.0D, z + 0.5D, currX - x + random.nextFloat() - 0.5D, currY - y - random.nextFloat() - 1.0F, currZ - z + random.nextFloat() - 0.5D);
					}
				}
			}
		}
	}

	@Override
	public void load(CompoundTag nbt)
	{
		super.load(nbt);
		this.owner = nbt.getString("owner");
		if (owner == null || owner.trim().isEmpty()) {
			owner = "None";
		}
		this.powerLevel = nbt.getInt("powerLevel");
		this.range = MeteorConfig.safeGet(MeteorConfig.shieldRadiusMultiplier, 64) * powerLevel;
		this.blockComets = nbt.getBoolean("blockComets");
		if (nbt.contains("cometType")) {
			this.cometType = nbt.getInt("cometType");
			this.cometX = nbt.getInt("cometX");
			this.cometZ = nbt.getInt("cometZ");
		}
		this.inv = NonNullList.withSize(13, ItemStack.EMPTY);
		ContainerHelper.loadAllItems(nbt, this.inv);
	}

	@Override
	protected void saveAdditional(CompoundTag nbt)
	{
		super.saveAdditional(nbt);
		nbt.putString("owner", this.owner);
		nbt.putInt("powerLevel", powerLevel);
		nbt.putBoolean("blockComets", blockComets);
		if (cometType != -1) {
			nbt.putInt("cometType", cometType);
			nbt.putInt("cometX", cometX);
			nbt.putInt("cometZ", cometZ);
		}
		ContainerHelper.saveAllItems(nbt, this.inv);
	}

	@Override
	public CompoundTag getUpdateTag() {
		return this.saveWithoutMetadata();
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public AABB getRenderBoundingBox() {
		return new AABB(worldPosition, worldPosition.offset(1, 2, 1));
	}

	// Container

	@Override
	public int getContainerSize() {
		return inv.size();
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack stack : inv) {
			if (!stack.isEmpty()) return false;
		}
		return true;
	}

	@Override
	public ItemStack getItem(int i) {
		return inv.get(i);
	}

	@Override
	public ItemStack removeItem(int i, int j) {
		ItemStack stack = getItem(i);
		if (!stack.isEmpty()) {
			if (stack.getCount() <= j) {
				setItem(i, ItemStack.EMPTY);
			} else {
				ItemStack stack2 = stack.split(j);
				if (stack.isEmpty()) {
					setItem(i, ItemStack.EMPTY);
				}
				stack = stack2;
			}
		}
		return stack;
	}

	@Override
	public ItemStack removeItemNoUpdate(int i) {
		ItemStack stack = inv.get(i);
		inv.set(i, ItemStack.EMPTY);
		return stack;
	}

	@Override
	public void setItem(int i, ItemStack itemstack) {
		if (itemstack.isEmpty()) {
			inv.set(i, ItemStack.EMPTY);
			if (i > 0 && i < 5 && powerLevel > 1) {
				this.updateRange();
			}
		} else if (canPlaceItem(i, itemstack)) {
			if (i < 5 && itemstack.getCount() > 1) {
				itemstack.setCount(1);
			}
			if (i == 0) {
				// The chip is consumed to charge the shield
				this.setCharged();
				if (level != null && !level.isClientSide) {
					level.playSound(null, worldPosition, MeteorSounds.SHIELD_POWERUP.get(), SoundSource.BLOCKS, 1.0F, 0.6F);
				}
				this.setChanged();
			} else if (i > 0 && i < 5) {
				inv.set(i, itemstack);
				this.updateRange();
			} else {
				inv.set(i, itemstack);
			}
		}
		this.setChanged();
	}

	@Override
	public int getMaxStackSize() {
		return 64;
	}

	@Override
	public boolean stillValid(Player player) {
		return Container.stillValidBlockEntity(this, player);
	}

	@Override
	public boolean canPlaceItem(int i, ItemStack itemstack) {
		if (itemstack.is(MeteorItems.itemRedMeteorGem.get()) && i > 0 && i < 5) {
			return powerLevel > 0 && powerLevel < 5 && inv.get(i).isEmpty();
		} else if (itemstack.is(MeteorItems.itemMeteorChips.get()) && i == 0) {
			return powerLevel == 0;
		}
		return true;
	}

	@Override
	public void clearContent() {
		inv.clear();
	}

	private void updateRange() {
		int powerCrystals = 0;
		for (int i = 1; i <= 4; i++) {
			if (inv.get(i).is(MeteorItems.itemRedMeteorGem.get())) {
				powerCrystals++;
			}
		}
		int oldLevel = this.powerLevel;
		this.powerLevel = 1 + powerCrystals;
		int multiplier = MeteorConfig.safeGet(MeteorConfig.shieldRadiusMultiplier, 64);
		this.range = multiplier * powerLevel;
		if (level != null && !level.isClientSide) {
			this.sendUpdate();
			if (powerLevel > oldLevel) {
				level.playSound(null, worldPosition, MeteorSounds.SHIELD_POWERUP.get(), SoundSource.BLOCKS, 1.0F, powerLevel / 10.0F + 0.5F);
				Player player = HandlerMeteor.getPlayerByName(level, owner);
				if (powerLevel == 5 && player != null) {
					MeteorTriggers.trigger(player, MeteorTriggers.SHIELD_FULLY_UPGRADED);
				}
				if (multiplier <= 0 && player != null) {
					player.sendSystemMessage(Component.translatable("MeteorShield.noUpgrade"));
				}
			}
			HandlerMeteor metHandler = HandlerWorld.get(level);
			if (metHandler != null && shieldedChunks) {
				metHandler.getShieldManager().addShield(this);
			}
		}
		this.setChanged();
	}

	@Override
	public int getRange() {
		return this.range;
	}

	@Override
	public int getX() {
		return this.worldPosition.getX();
	}

	@Override
	public int getY() {
		return this.worldPosition.getY();
	}

	@Override
	public int getZ() {
		return this.worldPosition.getZ();
	}

	@Override
	public boolean isTileEntity() {
		return true;
	}

	@Override
	public String getOwner() {
		return this.owner;
	}

	@Override
	public int getPowerLevel() {
		return this.powerLevel;
	}

	@Override
	public void onChunkUnloaded() {
		super.onChunkUnloaded();
		HandlerMeteor metHandler = HandlerWorld.get(level);
		if (metHandler != null) {
			metHandler.getShieldManager().addShield(new MeteorShieldData(getX(), getY(), getZ(), powerLevel, owner, blockComets));
		}
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return OUTPUT_SLOTS;
	}

	@Override
	public boolean canPlaceItemThroughFace(int slot, ItemStack item, Direction side) {
		return slot < 5 && canPlaceItem(slot, item);
	}

	@Override
	public boolean canTakeItemThroughFace(int slot, ItemStack item, Direction side) {
		return slot > 4;
	}

	@Override
	public void onButtonPress(int id) {
		if (id == 0) {
			this.blockComets = !blockComets;
			HandlerMeteor metHandler = HandlerWorld.get(level);
			if (metHandler != null && shieldedChunks) {
				metHandler.getShieldManager().addShield(this);
			}
		}
	}

	// MenuProvider

	@Override
	public Component getDisplayName() {
		return Component.translatable("block.meteors.meteor_shield");
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player) {
		return new ContainerMeteorShield(id, playerInv, this);
	}

}
