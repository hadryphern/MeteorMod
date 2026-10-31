package net.meteor.client;

import net.meteor.MeteorsMod;
import net.meteor.client.effect.EntityMeteorShieldParticleFX;
import net.meteor.client.effect.EntityMeteordustFX;
import net.meteor.client.gui.GuiFreezingMachine;
import net.meteor.client.gui.GuiMeteorShield;
import net.meteor.client.model.ModelAlienCreeper;
import net.meteor.client.model.ModelCometKitty;
import net.meteor.client.model.ModelMeteor;
import net.meteor.client.model.ModelMeteorShield;
import net.meteor.client.model.ModelMeteorTimer;
import net.meteor.client.render.RenderAlienCreeper;
import net.meteor.client.render.RenderComet;
import net.meteor.client.render.RenderCometKitty;
import net.meteor.client.render.RenderMeteor;
import net.meteor.client.tileentity.TileEntityMeteorShieldRenderer;
import net.meteor.client.tileentity.TileEntityMeteorTimerRenderer;
import net.meteor.client.tileentity.TileEntitySlipperyRenderer;
import net.meteor.common.ClientHandler;
import net.meteor.common.MeteorBlockEntities;
import net.meteor.common.MeteorEntities;
import net.meteor.common.MeteorItems;
import net.meteor.common.MeteorMenus;
import net.meteor.common.MeteorParticles;
import net.meteor.common.item.ItemDetector;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/** Client registrations: renderers, models, screens, particles and the detector needles. */
public class ClientProxy
{
	@Mod.EventBusSubscriber(modid = MeteorsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class ModEvents {

		@SubscribeEvent
		public static void onClientSetup(FMLClientSetupEvent event) {
			event.enqueueWork(() -> {
				MenuScreens.register(MeteorMenus.METEOR_SHIELD.get(), GuiMeteorShield::new);
				MenuScreens.register(MeteorMenus.FREEZER.get(), GuiFreezingMachine::new);
				registerDetector(MeteorItems.itemMeteorProximityDetector.get());
				registerDetector(MeteorItems.itemMeteorTimeDetector.get());
				registerDetector(MeteorItems.itemMeteorCrashDetector.get());
			});
		}

		private static void registerDetector(Item item) {
			int type = ((ItemDetector) item).getDetectorType();
			ItemProperties.register(item, new ResourceLocation(MeteorsMod.MOD_ID, "angle"), (stack, level, entity, seed) -> TextureDetector.getAngle(type));
		}

		@SubscribeEvent
		public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
			event.registerEntityRenderer(MeteorEntities.FALLING_METEOR.get(), RenderMeteor::new);
			event.registerEntityRenderer(MeteorEntities.ALIEN_CREEPER.get(), RenderAlienCreeper::new);
			event.registerEntityRenderer(MeteorEntities.COMET_KITTY.get(), RenderCometKitty::new);
			event.registerEntityRenderer(MeteorEntities.METEOR_SUMMONER.get(), ThrownItemRenderer::new);
			event.registerEntityRenderer(MeteorEntities.FALLING_COMET.get(), RenderComet::new);
			event.registerBlockEntityRenderer(MeteorBlockEntities.METEOR_SHIELD.get(), TileEntityMeteorShieldRenderer::new);
			event.registerBlockEntityRenderer(MeteorBlockEntities.METEOR_TIMER.get(), TileEntityMeteorTimerRenderer::new);
			event.registerBlockEntityRenderer(MeteorBlockEntities.SLIPPERY.get(), TileEntitySlipperyRenderer::new);
		}

		@SubscribeEvent
		public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
			event.registerLayerDefinition(ModelMeteor.LAYER, ModelMeteor::createLayer);
			event.registerLayerDefinition(ModelMeteorShield.LAYER, ModelMeteorShield::createLayer);
			event.registerLayerDefinition(ModelMeteorTimer.LAYER, ModelMeteorTimer::createLayer);
			event.registerLayerDefinition(ModelAlienCreeper.LAYER, () -> ModelAlienCreeper.createLayer(CubeDeformation.NONE));
			event.registerLayerDefinition(ModelAlienCreeper.ARMOR_LAYER, () -> ModelAlienCreeper.createLayer(new CubeDeformation(2.0F)));
			event.registerLayerDefinition(ModelCometKitty.LAYER, ModelCometKitty::createLayer);
		}

		@SubscribeEvent
		public static void registerParticles(RegisterParticleProvidersEvent event) {
			event.registerSpriteSet(MeteorParticles.METEOR_DUST.get(), EntityMeteordustFX.MeteorDustProvider::new);
			event.registerSpriteSet(MeteorParticles.FREZA_DUST.get(), EntityMeteordustFX.FrezaDustProvider::new);
			event.registerSpriteSet(MeteorParticles.METEOR_SHIELD.get(), EntityMeteorShieldParticleFX.Provider::new);
		}
	}

	@Mod.EventBusSubscriber(modid = MeteorsMod.MOD_ID, value = Dist.CLIENT)
	public static class ForgeEvents {

		@SubscribeEvent
		public static void onClientTick(TickEvent.ClientTickEvent event) {
			if (event.phase == TickEvent.Phase.END) {
				TextureDetector.updateAnimations();
			}
		}

		@SubscribeEvent
		public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
			ClientHandler.reset();
		}
	}
}
