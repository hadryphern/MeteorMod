package net.meteor.common;

import net.meteor.MeteorsMod;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/** The custom EntityFX classes from 1.7.10 (spawned through ClientProxy.spawnParticle). */
public class MeteorParticles {

	public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MeteorsMod.MOD_ID);

	public static final RegistryObject<SimpleParticleType> METEOR_DUST 	= PARTICLES.register("meteor_dust", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> FREZA_DUST 	= PARTICLES.register("freza_dust", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> METEOR_SHIELD = PARTICLES.register("meteor_shield", () -> new SimpleParticleType(false));

}
