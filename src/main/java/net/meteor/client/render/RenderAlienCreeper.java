package net.meteor.client.render;

import com.mojang.blaze3d.vertex.PoseStack;

import net.meteor.MeteorsMod;
import net.meteor.client.model.ModelAlienCreeper;
import net.meteor.common.entity.EntityAlienCreeper;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EnergySwirlLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/** Swells and flashes like a creeper, and always has the charged swirl. */
public class RenderAlienCreeper extends MobRenderer<EntityAlienCreeper, CreeperModel<EntityAlienCreeper>> {

	private static final ResourceLocation skin = ResourceLocation.fromNamespaceAndPath(MeteorsMod.MOD_ID, "textures/entity/alien_creeper.png");

	public RenderAlienCreeper(EntityRendererProvider.Context context)
	{
		super(context, new CreeperModel<EntityAlienCreeper>(context.bakeLayer(ModelAlienCreeper.LAYER)), 0.5F);
		this.addLayer(new PowerLayer(this, context.getModelSet()));
	}

	@Override
	protected void scale(EntityAlienCreeper entitycreeper, PoseStack poseStack, float f)
	{
		float f1 = entitycreeper.getSwelling(f);
		float f2 = 1.0F + Mth.sin(f1 * 100F) * f1 * 0.01F;
		f1 = Mth.clamp(f1, 0.0F, 1.0F);
		f1 *= f1;
		f1 *= f1;
		float f3 = (1.0F + f1 * 0.4F) * f2;
		float f4 = (1.0F + f1 * 0.1F) / f2;
		poseStack.scale(f3, f4, f3);
	}

	@Override
	protected float getWhiteOverlayProgress(EntityAlienCreeper entityCreeper, float par3)
	{
		float var5 = entityCreeper.getSwelling(par3);
		return (int)(var5 * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(var5, 0.5F, 1.0F);
	}

	@Override
	public ResourceLocation getTextureLocation(EntityAlienCreeper entity) {
		return skin;
	}

	private static class PowerLayer extends EnergySwirlLayer<EntityAlienCreeper, CreeperModel<EntityAlienCreeper>> {

		private static final ResourceLocation electricity = ResourceLocation.withDefaultNamespace("textures/entity/creeper/creeper_armor.png");
		private final CreeperModel<EntityAlienCreeper> creeperModel;

		public PowerLayer(RenderLayerParent<EntityAlienCreeper, CreeperModel<EntityAlienCreeper>> parent, EntityModelSet models) {
			super(parent);
			this.creeperModel = new CreeperModel<EntityAlienCreeper>(models.bakeLayer(ModelAlienCreeper.ARMOR_LAYER));
		}

		@Override
		protected float xOffset(float ticks) {
			return ticks * 0.01F;
		}

		@Override
		protected ResourceLocation getTextureLocation() {
			return electricity;
		}

		@Override
		protected EntityModel<EntityAlienCreeper> model() {
			return creeperModel;
		}
	}
}
