package net.meteor.common;

import net.meteor.MeteorsMod;
import net.meteor.common.block.BlockFreezingMachine;
import net.meteor.common.block.BlockFrezarite;
import net.meteor.common.block.BlockKreknorite;
import net.meteor.common.block.BlockMeteor;
import net.meteor.common.block.BlockMeteorShield;
import net.meteor.common.block.BlockMeteorShieldTorch;
import net.meteor.common.block.BlockMeteorShieldWallTorch;
import net.meteor.common.block.BlockMeteorTimer;
import net.meteor.common.block.BlockRareFallenMeteor;
import net.meteor.common.block.BlockSlippery;
import net.meteor.common.block.BlockSlipperyStairs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Hardness values match 1.7.10. Explosion resistance is the old setResistance value * 0.6,
 * since 1.7.10 multiplied it by 3 and divided by 5 when exploding.
 */
public class MeteorBlocks {

	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MeteorsMod.MOD_ID);

	public static final RegistryObject<Block> METEOR_ORE = BLOCKS.register("meteor_ore",
			() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(10F, 120F).sound(SoundType.STONE).requiresCorrectToolForDrops()));
	public static final RegistryObject<Block> METEOR = BLOCKS.register("meteor",
			() -> new BlockMeteor(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(10F, 120F).sound(SoundType.STONE).requiresCorrectToolForDrops().lightLevel(BlockMeteor.heatLight(7))));
	public static final RegistryObject<Block> RARE_METEOR = BLOCKS.register("rare_meteor",
			() -> new BlockRareFallenMeteor(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(10F, 120F).sound(SoundType.STONE).requiresCorrectToolForDrops().lightLevel(BlockMeteor.heatLight(7))));
	public static final RegistryObject<Block> METEOR_SHIELD = BLOCKS.register("meteor_shield",
			() -> new BlockMeteorShield(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(2.5F).sound(SoundType.STONE).requiresCorrectToolForDrops().lightLevel(s -> 7).noOcclusion()));
	public static final RegistryObject<Block> PROTECTED_LAND_TESTER = BLOCKS.register("protected_land_tester",
			() -> new BlockMeteorShieldTorch(BlockBehaviour.Properties.of().noCollission().instabreak().sound(SoundType.WOOD).lightLevel(BlockMeteorShieldTorch.activeLight(7)).pushReaction(PushReaction.DESTROY).noLootTable()));
	public static final RegistryObject<Block> PROTECTED_LAND_TESTER_WALL = BLOCKS.register("protected_land_tester_wall",
			() -> new BlockMeteorShieldWallTorch(BlockBehaviour.Properties.of().noCollission().instabreak().sound(SoundType.WOOD).lightLevel(BlockMeteorShieldTorch.activeLight(7)).pushReaction(PushReaction.DESTROY).noLootTable()));
	public static final RegistryObject<Block> FREZARITE = BLOCKS.register("frezarite",
			() -> new BlockFrezarite(BlockBehaviour.Properties.of().mapColor(MapColor.ICE).strength(8.5F, 90F).sound(SoundType.GLASS).requiresCorrectToolForDrops().lightLevel(s -> 3).friction(0.98F)));
	public static final RegistryObject<Block> KREKNORITE = BLOCKS.register("kreknorite",
			() -> new BlockKreknorite(BlockBehaviour.Properties.of().mapColor(MapColor.NETHER).strength(11F, 210F).sound(SoundType.STONE).requiresCorrectToolForDrops().lightLevel(BlockMeteor.heatLight(10))));
	public static final RegistryObject<Block> METEOR_TIMER = BLOCKS.register("meteor_timer",
			() -> new BlockMeteorTimer(BlockBehaviour.Properties.of().instabreak().sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.DESTROY)));
	public static final RegistryObject<Block> RED_METEOR_GEM_BLOCK = BLOCKS.register("red_meteor_gem_block",
			() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).strength(5.0F, 6.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()));
	public static final RegistryObject<Block> METEORITE_BLOCK = BLOCKS.register("meteorite_block",
			() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(5.0F, 6.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()));
	public static final RegistryObject<Block> FROZEN_IRON_BLOCK = BLOCKS.register("frozen_iron_block",
			() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.ICE).strength(5.0F, 6.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()));
	public static final RegistryObject<Block> KREKNORITE_BLOCK = BLOCKS.register("kreknorite_block",
			() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).strength(5.0F, 6.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()));
	public static final RegistryObject<Block> FREEZER = BLOCKS.register("freezer",
			() -> new BlockFreezingMachine(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.5F).sound(SoundType.METAL).requiresCorrectToolForDrops()));

	public static final RegistryObject<Block> SLIPPERY_BLOCK 		= slippery("slippery_block", 1, 0.98F);
	public static final RegistryObject<Block> SLIPPERY_BLOCK_TWO 	= slippery("slippery_block_two", 2, 1.03F);
	public static final RegistryObject<Block> SLIPPERY_BLOCK_THREE 	= slippery("slippery_block_three", 3, 1.07F);
	public static final RegistryObject<Block> SLIPPERY_BLOCK_FOUR 	= slippery("slippery_block_four", 4, 1.10F);
	public static final RegistryObject<Block> SLIPPERY_STAIRS 		= slipperyStairs("slippery_stairs", 1, 0.98F);
	public static final RegistryObject<Block> SLIPPERY_STAIRS_TWO 	= slipperyStairs("slippery_stairs_two", 2, 1.03F);
	public static final RegistryObject<Block> SLIPPERY_STAIRS_THREE = slipperyStairs("slippery_stairs_three", 3, 1.07F);
	public static final RegistryObject<Block> SLIPPERY_STAIRS_FOUR 	= slipperyStairs("slippery_stairs_four", 4, 1.10F);

	public static final RegistryObject<Block> FREZARITE_ORE = BLOCKS.register("frezarite_ore",
			() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(10F, 120F).sound(SoundType.STONE).requiresCorrectToolForDrops()));

	private static RegistryObject<Block> slippery(String name, int level, float slipperiness) {
		return BLOCKS.register(name, () -> new BlockSlippery(level, slipperyProps(slipperiness)));
	}

	private static RegistryObject<Block> slipperyStairs(String name, int level, float slipperiness) {
		return BLOCKS.register(name, () -> new BlockSlipperyStairs(level, () -> Blocks.PACKED_ICE.defaultBlockState(), slipperyProps(slipperiness)));
	}

	private static BlockBehaviour.Properties slipperyProps(float slipperiness) {
		return BlockBehaviour.Properties.of().mapColor(MapColor.ICE).strength(1.0F).sound(SoundType.GLASS).friction(slipperiness).noOcclusion();
	}

	/** Slippery blocks ordered by level, used by the freezer to "upgrade" a slippery block. */
	public static Block getSlipperyBlock(int level) {
		switch (level) {
		case 2: return SLIPPERY_BLOCK_TWO.get();
		case 3: return SLIPPERY_BLOCK_THREE.get();
		case 4: return SLIPPERY_BLOCK_FOUR.get();
		default: return SLIPPERY_BLOCK.get();
		}
	}

	public static Block getSlipperyStairs(int level) {
		switch (level) {
		case 2: return SLIPPERY_STAIRS_TWO.get();
		case 3: return SLIPPERY_STAIRS_THREE.get();
		case 4: return SLIPPERY_STAIRS_FOUR.get();
		default: return SLIPPERY_STAIRS.get();
		}
	}

}
