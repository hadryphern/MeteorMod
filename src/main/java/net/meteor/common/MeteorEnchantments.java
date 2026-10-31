package net.meteor.common;

import net.meteor.MeteorsMod;
import net.meteor.common.enchantment.EnchantmentColdTouch;
import net.meteor.common.enchantment.EnchantmentMagnetized;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MeteorEnchantments {

	public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, MeteorsMod.MOD_ID);

	public static final RegistryObject<Enchantment> MAGNETIZATION 	= ENCHANTMENTS.register("magnetization", EnchantmentMagnetized::new);
	public static final RegistryObject<Enchantment> COLD_TOUCH 		= ENCHANTMENTS.register("cold_touch", EnchantmentColdTouch::new);

}
