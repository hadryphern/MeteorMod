package net.meteor.common;

import net.meteor.MeteorsMod;
import net.meteor.common.block.container.ContainerFreezingMachine;
import net.meteor.common.block.container.ContainerMeteorShield;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/** Replaces the old IGuiHandler (HandlerGui). */
public class MeteorMenus {

	public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MeteorsMod.MOD_ID);

	public static final RegistryObject<MenuType<ContainerMeteorShield>> METEOR_SHIELD = MENUS.register("meteor_shield",
			() -> IForgeMenuType.create((id, inv, buf) -> new ContainerMeteorShield(id, inv, buf.readBlockPos())));
	public static final RegistryObject<MenuType<ContainerFreezingMachine>> FREEZER = MENUS.register("freezer",
			() -> IForgeMenuType.create((id, inv, buf) -> new ContainerFreezingMachine(id, inv, buf.readBlockPos())));

}
