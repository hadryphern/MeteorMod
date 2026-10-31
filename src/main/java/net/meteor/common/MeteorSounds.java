package net.meteor.common;

import net.meteor.MeteorsMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MeteorSounds {

	public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MeteorsMod.MOD_ID);

	public static final RegistryObject<SoundEvent> METEOR_CRASH 	= register("meteor.crash");
	public static final RegistryObject<SoundEvent> SHIELD_POWERUP 	= register("shield.powerup");
	public static final RegistryObject<SoundEvent> SHIELD_POWERDOWN = register("shield.powerdown");
	public static final RegistryObject<SoundEvent> SHIELD_HUMM 		= register("shield.humm");

	private static RegistryObject<SoundEvent> register(String name) {
		return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MeteorsMod.MOD_ID, name)));
	}

}
