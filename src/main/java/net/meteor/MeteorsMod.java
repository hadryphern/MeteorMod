package net.meteor;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.meteor.common.MeteorBlockEntities;
import net.meteor.common.MeteorBlocks;
import net.meteor.common.MeteorConfig;
import net.meteor.common.MeteorEnchantments;
import net.meteor.common.MeteorEntities;
import net.meteor.common.MeteorItems;
import net.meteor.common.MeteorMenus;
import net.meteor.common.MeteorParticles;
import net.meteor.common.MeteorSounds;
import net.meteor.common.MeteorTabs;
import net.meteor.common.MeteorTriggers;
import net.meteor.common.climate.HandlerWorld;
import net.meteor.common.network.MeteorNetwork;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MeteorsMod.MOD_ID)
public class MeteorsMod {

	public static final String MOD_ID 	= "meteors";
	public static final String MOD_NAME = "Falling Meteors";

	public static final Logger log = LogUtils.getLogger();

	public MeteorsMod(FMLJavaModLoadingContext context) {
		IEventBus modBus = context.getModEventBus();
		MeteorBlocks.BLOCKS.register(modBus);
		MeteorItems.ITEMS.register(modBus);
		MeteorBlockEntities.BLOCK_ENTITIES.register(modBus);
		MeteorEntities.ENTITIES.register(modBus);
		MeteorMenus.MENUS.register(modBus);
		MeteorSounds.SOUNDS.register(modBus);
		MeteorParticles.PARTICLES.register(modBus);
		MeteorEnchantments.ENCHANTMENTS.register(modBus);
		MeteorTabs.TABS.register(modBus);
		modBus.addListener(this::commonSetup);
		modBus.addListener(MeteorEntities::registerAttributes);

		// Server config is synced to clients, which replaces the old PacketSettings
		context.registerConfig(ModConfig.Type.SERVER, MeteorConfig.SPEC);
		MeteorNetwork.register();
	}

	private void commonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			HandlerWorld.registerGameRules();
			MeteorTriggers.register();
		});
	}

}
