package net.meteor.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.meteor.client.model.ModelMeteor;
import net.meteor.common.entity.EntityComet;
import net.meteor.common.entity.EntityMeteor;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/** Comets look like size 1 meteors. */
public class RenderComet extends EntityRenderer<EntityComet> {

	private final ModelMeteor modelMeteor;

	public RenderComet(EntityRendererProvider.Context context) {
		super(context);
		this.modelMeteor = new ModelMeteor(context.bakeLayer(ModelMeteor.LAYER));
		this.shadowRadius = 0.0F;
	}

	@Override
	public void render(EntityComet entityComet, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light) {
		poseStack.pushPose();
		poseStack.translate(0.0D, EntityMeteor.Y_OFFSET, 0.0D);
		poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entityComet.yRotO, entityComet.getYRot())));
		poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entityComet.xRotO, entityComet.getXRot())));
		modelMeteor.renderWithSize(1, poseStack, buffer.getBuffer(RenderType.entityCutout(getTextureLocation(entityComet))), light, OverlayTexture.NO_OVERLAY);
		poseStack.popPose();
		super.render(entityComet, yaw, partialTicks, poseStack, buffer, light);
	}

	@Override
	public ResourceLocation getTextureLocation(EntityComet entity) {
		return RenderMeteor.getSkin(entity.meteorType.getID());
	}

}
