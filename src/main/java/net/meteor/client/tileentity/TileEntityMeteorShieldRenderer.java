package net.meteor.client.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.meteor.MeteorsMod;
import net.meteor.client.model.ModelMeteorShield;
import net.meteor.common.block.BlockMeteorShield;
import net.meteor.common.tileentity.TileEntityMeteorShield;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class TileEntityMeteorShieldRenderer implements BlockEntityRenderer<TileEntityMeteorShield>
{
	public static final ResourceLocation shieldTexture = ResourceLocation.fromNamespaceAndPath(MeteorsMod.MOD_ID, "textures/entity/meteor_shield.png");

	private final ModelMeteorShield modelShield;

	public TileEntityMeteorShieldRenderer(BlockEntityRendererProvider.Context context) {
		this.modelShield = new ModelMeteorShield(context.bakeLayer(ModelMeteorShield.LAYER));
	}

	/** The old metadata the rotation was based on. */
	public static int getMeta(Direction facing) {
		switch (facing) {
		case SOUTH: return 1;
		case WEST: return 0;
		case NORTH: return 3;
		default: return 2;
		}
	}

	@Override
	public void render(TileEntityMeteorShield shield, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
		if (shield.getLevel() == null) {
			return;
		}
		int level = shield.getPowerLevel();
		if (!shield.getLevel().isEmptyBlock(shield.getBlockPos().above())) {
			level = 0;
		}
		int meta = getMeta(shield.getBlockState().getValue(BlockMeteorShield.FACING));
		poseStack.pushPose();
		poseStack.translate(0.5F, 1.5F, 0.5F);
		poseStack.mulPose(Axis.ZP.rotationDegrees(180F));
		poseStack.mulPose(Axis.YP.rotationDegrees(meta * -90F));
		this.modelShield.render(level, partialTick, shield.age, poseStack, buffer.getBuffer(RenderType.entityCutout(shieldTexture)), light, overlay);
		poseStack.popPose();
	}
}
