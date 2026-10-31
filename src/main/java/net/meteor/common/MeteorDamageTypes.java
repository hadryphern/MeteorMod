package net.meteor.common;

import net.meteor.MeteorsMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

/**
 * "It's amazing that I'll do all this for a little death message" - the 1.7.10 version had to copy the whole
 * Explosion class (ExplosionMeteor) to get it. Damage types are data driven now, see data/meteors/damage_type.
 */
public class MeteorDamageTypes {

	public static final ResourceKey<DamageType> METEOR = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(MeteorsMod.MOD_ID, "meteor"));

	public static DamageSource meteor(Level level, Entity source) {
		return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(METEOR), source);
	}

}
