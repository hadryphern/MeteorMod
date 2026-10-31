package net.meteor.common;

import net.meteor.MeteorsMod;
import net.meteor.common.entity.EntityAlienCreeper;
import net.meteor.common.entity.EntityComet;
import net.meteor.common.entity.EntityCometKitty;
import net.meteor.common.entity.EntityMeteor;
import net.meteor.common.entity.EntitySummoner;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MeteorEntities {

	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MeteorsMod.MOD_ID);

	public static final RegistryObject<EntityType<EntityMeteor>> FALLING_METEOR = ENTITIES.register("falling_meteor",
			() -> EntityType.Builder.<EntityMeteor>of(EntityMeteor::new, MobCategory.MISC).sized(0.98F, 0.98F).clientTrackingRange(8).updateInterval(8).build("falling_meteor"));
	public static final RegistryObject<EntityType<EntityAlienCreeper>> ALIEN_CREEPER = ENTITIES.register("alien_creeper",
			() -> EntityType.Builder.<EntityAlienCreeper>of(EntityAlienCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).fireImmune().clientTrackingRange(8).build("alien_creeper"));
	public static final RegistryObject<EntityType<EntityCometKitty>> COMET_KITTY = ENTITIES.register("comet_kitty",
			() -> EntityType.Builder.<EntityCometKitty>of(EntityCometKitty::new, MobCategory.CREATURE).sized(0.6F, 0.7F).fireImmune().clientTrackingRange(8).build("comet_kitty"));
	public static final RegistryObject<EntityType<EntitySummoner>> METEOR_SUMMONER = ENTITIES.register("meteor_summoner",
			() -> EntityType.Builder.<EntitySummoner>of(EntitySummoner::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build("meteor_summoner"));
	public static final RegistryObject<EntityType<EntityComet>> FALLING_COMET = ENTITIES.register("falling_comet",
			() -> EntityType.Builder.<EntityComet>of(EntityComet::new, MobCategory.MISC).sized(0.98F, 0.98F).clientTrackingRange(8).updateInterval(8).build("falling_comet"));

	public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(ALIEN_CREEPER.get(), Creeper.createAttributes().build());
		event.put(COMET_KITTY.get(), EntityCometKitty.createAttributes().build());
	}

}
