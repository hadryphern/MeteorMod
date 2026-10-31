package net.meteor.common;

import net.meteor.MeteorsMod;
import net.meteor.common.tileentity.TileEntityFreezingMachine;
import net.meteor.common.tileentity.TileEntityMeteorShield;
import net.meteor.common.tileentity.TileEntityMeteorTimer;
import net.meteor.common.tileentity.TileEntitySlippery;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MeteorBlockEntities {

	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MeteorsMod.MOD_ID);

	public static final RegistryObject<BlockEntityType<TileEntityMeteorShield>> METEOR_SHIELD = BLOCK_ENTITIES.register("meteor_shield",
			() -> BlockEntityType.Builder.of(TileEntityMeteorShield::new, MeteorBlocks.METEOR_SHIELD.get()).build(null));
	public static final RegistryObject<BlockEntityType<TileEntityMeteorTimer>> METEOR_TIMER = BLOCK_ENTITIES.register("meteor_timer",
			() -> BlockEntityType.Builder.of(TileEntityMeteorTimer::new, MeteorBlocks.METEOR_TIMER.get()).build(null));
	public static final RegistryObject<BlockEntityType<TileEntityFreezingMachine>> FREEZER = BLOCK_ENTITIES.register("freezer",
			() -> BlockEntityType.Builder.of(TileEntityFreezingMachine::new, MeteorBlocks.FREEZER.get()).build(null));
	public static final RegistryObject<BlockEntityType<TileEntitySlippery>> SLIPPERY = BLOCK_ENTITIES.register("slippery",
			() -> BlockEntityType.Builder.of(TileEntitySlippery::new,
					MeteorBlocks.SLIPPERY_BLOCK.get(), MeteorBlocks.SLIPPERY_BLOCK_TWO.get(), MeteorBlocks.SLIPPERY_BLOCK_THREE.get(), MeteorBlocks.SLIPPERY_BLOCK_FOUR.get(),
					MeteorBlocks.SLIPPERY_STAIRS.get(), MeteorBlocks.SLIPPERY_STAIRS_TWO.get(), MeteorBlocks.SLIPPERY_STAIRS_THREE.get(), MeteorBlocks.SLIPPERY_STAIRS_FOUR.get()).build(null));

}
