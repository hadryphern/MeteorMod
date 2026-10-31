package net.meteor.common.tileentity;

import net.meteor.common.MeteorBlockEntities;
import net.meteor.common.item.ItemBlockSlippery;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;

/** Remembers which block a slippery block looks like. */
public class TileEntitySlippery extends BlockEntity {

	private String blockName = "minecraft:stone";
	private Block facadeBlock = Blocks.STONE;

	public TileEntitySlippery(BlockPos pos, BlockState state) {
		super(MeteorBlockEntities.SLIPPERY.get(), pos, state);
	}

	public static Block getBlockFromName(String name) {
		return getBlockFromName(name, Blocks.STONE);
	}

	public static Block getBlockFromName(String name, Block def) {
		ResourceLocation key = ResourceLocation.tryParse(name);
		if (key == null || !ForgeRegistries.BLOCKS.containsKey(key)) {
			return def;
		}
		Block block = ForgeRegistries.BLOCKS.getValue(key);
		return block == null ? def : block;
	}

	public static String getNameFromBlock(Block block) {
		ResourceLocation key = ForgeRegistries.BLOCKS.getKey(block);
		return key == null ? "minecraft:stone" : key.toString();
	}

	public void setFacadeBlockName(String name) {
		this.blockName = name;
		this.facadeBlock = getBlockFromName(this.blockName);
	}

	public String getFacadeBlockName() {
		return blockName;
	}

	public Block getFacadeBlock() {
		return facadeBlock;
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		if (nbt.contains(ItemBlockSlippery.FACADE_BLOCK_KEY)) {
			setFacadeBlockName(nbt.getString(ItemBlockSlippery.FACADE_BLOCK_KEY));
		}
	}

	@Override
	protected void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		nbt.putString(ItemBlockSlippery.FACADE_BLOCK_KEY, blockName);
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
		return new AABB(worldPosition, worldPosition.offset(1, 1, 1));
	}

}
