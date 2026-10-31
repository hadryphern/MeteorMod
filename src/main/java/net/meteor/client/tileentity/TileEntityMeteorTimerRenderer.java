package net.meteor.client.tileentity;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.meteor.MeteorsMod;
import net.meteor.client.model.ModelMeteorTimer;
import net.meteor.common.tileentity.TileEntityMeteorTimer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class TileEntityMeteorTimerRenderer implements BlockEntityRenderer<TileEntityMeteorTimer> {

	public static final ResourceLocation timerTex = ResourceLocation.fromNamespaceAndPath(MeteorsMod.MOD_ID, "textures/entity/meteor_timer.png");

	// The elements started at a random angle; 1.7.10 then turned them a bit every frame
	private static final float[] START_ANGLES;
	static {
		RandomSource rand = RandomSource.create();
		START_ANGLES = new float[] { rand.nextFloat() * 360F, rand.nextFloat() * 360F, rand.nextFloat() * 360F };
	}

	private final ModelMeteorTimer modelMetTimer;

	public TileEntityMeteorTimerRenderer(BlockEntityRendererProvider.Context context) {
		this.modelMetTimer = new ModelMeteorTimer(context.bakeLayer(ModelMeteorTimer.LAYER));
	}

	@Override
	public void render(TileEntityMeteorTimer timer, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
		float time = timer.getLevel() == null ? 0.0F : timer.getLevel().getGameTime() + partialTick;
		renderMeteorTimer(modelMetTimer, time, poseStack, buffer.getBuffer(RenderType.entityCutout(timerTex)), light, overlay);
		drawStringAbove(timer, poseStack, buffer);
	}

	public static void renderMeteorTimer(ModelMeteorTimer model, float time, PoseStack poseStack, VertexConsumer buffer, int light, int overlay) {
		model.element_meteorite.yRot = START_ANGLES[0] + time * 0.06F;
		model.element_frezarite.yRot = START_ANGLES[1] + time * 0.03F;
		model.element_kreknorite.yRot = START_ANGLES[2] + time * 0.009F;
		model.renderAll(poseStack, buffer, light, overlay);
	}

	protected void drawStringAbove(TileEntityMeteorTimer timer, PoseStack poseStack, MultiBufferSource buffer)
	{
		Minecraft mc = Minecraft.getInstance();
		HitResult mop = mc.hitResult;
		if (Minecraft.renderNames() && mop instanceof BlockHitResult hit && mop.getType() == HitResult.Type.BLOCK && hit.getBlockPos().equals(timer.getBlockPos()))
		{
			float f = 1.6F;
			float f1 = 0.016666668F * f;
			double d3 = mc.gameRenderer.getMainCamera().getPosition().distanceToSqr(Vec3.atCenterOf(timer.getBlockPos()));
			float f2 = 32F;

			if (d3 < (double)(f2 * f2))
			{
				Component s = timer.quickMode ? Component.translatable("info.meteorTimer.quickMode") : Component.translatable("info.meteorTimer.powerMode");
				Font fontrenderer = mc.font;
				poseStack.pushPose();
				poseStack.translate(0.5F, 0.65F + 0.5F, 0.5F);
				poseStack.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());
				poseStack.scale(-f1, -f1, f1);
				Matrix4f matrix = poseStack.last().pose();
				float x = -fontrenderer.width(s) / 2;
				int background = (int)(0.25F * 255.0F) << 24;
				fontrenderer.drawInBatch(s, x, 0, 553648127, false, matrix, buffer, Font.DisplayMode.SEE_THROUGH, background, LightTexture.FULL_BRIGHT);
				fontrenderer.drawInBatch(s, x, 0, -1, false, matrix, buffer, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
				poseStack.popPose();
			}
		}
	}

}
