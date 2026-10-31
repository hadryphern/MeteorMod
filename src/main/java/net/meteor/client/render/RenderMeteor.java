package net.meteor.client.render;

import java.util.HashMap;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.meteor.MeteorsMod;
import net.meteor.client.model.ModelMeteor;
import net.meteor.common.entity.EntityMeteor;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RenderMeteor extends EntityRenderer<EntityMeteor> {

	public static final HashMap<Integer, ResourceLocation> skins = new HashMap<Integer, ResourceLocation>();

	static {
		skins.put(0, new ResourceLocation(MeteorsMod.MOD_ID, "textures/entity/meteorite_meteor.png"));
		skins.put(1, new ResourceLocation(MeteorsMod.MOD_ID, "textures/entity/frezarite_meteor.png"));
		skins.put(2, new ResourceLocation(MeteorsMod.MOD_ID, "textures/entity/kreknorite_meteor.png"));
		skins.put(3, new ResourceLocation(MeteorsMod.MOD_ID, "textures/entity/unknown_meteor.png"));
		skins.put(4, new ResourceLocation(MeteorsMod.MOD_ID, "textures/entity/kitty_meteor.png"));
	}

	private final ModelMeteor modelMeteor;

	public RenderMeteor(EntityRendererProvider.Context context) {
		super(context);
		this.modelMeteor = new ModelMeteor(context.bakeLayer(ModelMeteor.LAYER));
		this.shadowRadius = 0.0F;
	}

	public static ResourceLocation getSkin(int metID) {
		return skins.getOrDefault(metID, skins.get(0));
	}

	@Override
	public void render(EntityMeteor entityMeteor, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light) {
		poseStack.pushPose();
		poseStack.translate(0.0D, EntityMeteor.Y_OFFSET, 0.0D);
		poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entityMeteor.yRotO, entityMeteor.getYRot())));
		poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entityMeteor.xRotO, entityMeteor.getXRot())));
		float f2 = 1.0F * (float)entityMeteor.size;
		poseStack.scale(f2, f2, f2);
		modelMeteor.renderWithSize(entityMeteor.size, poseStack, buffer.getBuffer(RenderType.entityCutout(getTextureLocation(entityMeteor))), light, OverlayTexture.NO_OVERLAY);
		poseStack.popPose();
		super.render(entityMeteor, yaw, partialTicks, poseStack, buffer, light);
	}

	@Override
	public ResourceLocation getTextureLocation(EntityMeteor entity) {
		return getSkin(entity.meteorType.getID());
	}

}
