package net.meteor.common;

import java.util.function.Supplier;

import net.meteor.common.block.BlockMeteor;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public enum EnumMeteor
{
	METEORITE(0, () -> MeteorBlocks.METEOR.get(), () -> MeteorBlocks.RARE_METEOR.get(), true, ChatFormatting.LIGHT_PURPLE, () -> new ItemStack(MeteorBlocks.METEOR.get()), () -> MeteorBlocks.METEOR.get()),
	FREZARITE(1, () -> MeteorBlocks.FREZARITE.get(), () -> Blocks.ICE, false, ChatFormatting.AQUA, () -> new ItemStack(MeteorBlocks.FREZARITE.get()), () -> MeteorBlocks.FREZARITE.get()),
	KREKNORITE(2, () -> MeteorBlocks.KREKNORITE.get(), () -> Blocks.LAVA, true, ChatFormatting.RED, () -> new ItemStack(MeteorBlocks.KREKNORITE.get()), () -> MeteorBlocks.KREKNORITE.get()),
	UNKNOWN(3, () -> Blocks.AIR, () -> Blocks.AIR, true, ChatFormatting.GRAY, () -> new ItemStack(Blocks.CHEST), () -> Blocks.GLOWSTONE),
	KITTY(4, () -> Blocks.AIR, () -> Blocks.AIR, false, ChatFormatting.GREEN, () -> new ItemStack(Items.COD), () -> MeteorBlocks.METEOR.get()),
	CUSTOM(5, () -> Blocks.AIR, () -> Blocks.AIR, false, ChatFormatting.GOLD, () -> new ItemStack(Blocks.STONE), () -> Blocks.STONE);

	private final int ID;
	private final Supplier<Block> material;
	private final Supplier<Block> rareMaterial;
	private final boolean fieryExplosion;
	private final ChatFormatting chatColor;
	private final Supplier<ItemStack> representingItem;
	private final Supplier<Block> representingBlock;

	private EnumMeteor(int id, Supplier<Block> mat, Supplier<Block> rMat, boolean fiery, ChatFormatting color, Supplier<ItemStack> item, Supplier<Block> block) {
		this.ID = id;
		this.material = mat;
		this.rareMaterial = rMat;
		this.fieryExplosion = fiery;
		this.chatColor = color;
		this.representingItem = item;
		this.representingBlock = block;
	}

	public Block getMaterial() {
		return this.material.get();
	}

	public Block getRareMaterial() {
		return this.rareMaterial.get();
	}

	public int getID() {
		return this.ID;
	}

	public boolean getFieryExplosion() {
		return this.fieryExplosion;
	}

	public ChatFormatting getChatColor() {
		return this.chatColor;
	}

	public ItemStack getRepresentingItem() {
		return this.representingItem.get();
	}

	public Block getRepresentingBlock() {
		return this.representingBlock.get();
	}

	/** Replaces the old metadata values (0 - 4) used to describe how hot a fallen meteor block is. */
	public static BlockState withHeat(Block block, int heat) {
		BlockState state = block.defaultBlockState();
		if (state.hasProperty(BlockMeteor.HEAT)) {
			state = state.setValue(BlockMeteor.HEAT, heat);
		}
		return state;
	}

	public static EnumMeteor getTypeFromID(int i) {
		switch (i) {
		case 0:
			return METEORITE;
		case 1:
			return FREZARITE;
		case 2:
			return KREKNORITE;
		case 3:
			return UNKNOWN;
		case 4:
			return KITTY;
		}
		return METEORITE;
	}

	public static Component getLocalName(EnumMeteor type) {
		return Component.translatable("meteor." + type.toString().toLowerCase() + ".name");
	}

}
