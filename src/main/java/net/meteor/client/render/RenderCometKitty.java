package net.meteor.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.meteor.MeteorsMod;
import net.meteor.client.model.ModelCometKitty;
import net.meteor.common.entity.EntityCometKitty;
import net.minecraft.client.model.CatModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RenderCometKitty extends MobRenderer<EntityCometKitty, CatModel<EntityCometKitty>> {

	private static final ResourceLocation skin = new ResourceLocation(MeteorsMod.MOD_ID, "textures/entity/comet_kitty.png");

	public RenderCometKitty(EntityRendererProvider.Context context) {
		super(context, new CatModel<EntityCometKitty>(context.bakeLayer(ModelCometKitty.LAYER)), 0.4F);
	}

	@Override
	protected void scale(EntityCometKitty kitty, PoseStack poseStack, float partialTick) {
		// Tamed ocelots were drawn smaller
		if (kitty.isTame()) {
			poseStack.scale(0.8F, 0.8F, 0.8F);
		}
	}

	@Override
	protected void setupRotations(EntityCometKitty kitty, PoseStack poseStack, float bob, float yRot, float partialTick) {
		super.setupRotations(kitty, poseStack, bob, yRot, partialTick);
		float f = kitty.getLieDownAmount(partialTick);
		if (f > 0.0F) {
			poseStack.translate(0.4F * f, 0.15F * f, 0.1F * f);
			poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.rotLerp(f, 0.0F, 90.0F)));
		}
	}

	@Override
	public ResourceLocation getTextureLocation(EntityCometKitty par1Entity)
	{
		return skin;
	}

}
