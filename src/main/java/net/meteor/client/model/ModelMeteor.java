package net.meteor.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.meteor.MeteorsMod;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

/** A size 1 meteor is a single cube, size 2 is eight cubes and size 3 adds six more around them. */
public class ModelMeteor
{
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(MeteorsMod.MOD_ID, "meteor"), "main");

	private static final float[][] BLOCK_PIVOTS = {
		{8F, 16F, 8F}, {8F, 0F, 8F}, {-8F, 0F, -8F}, {8F, 0F, -8F}, {-8F, 16F, -8F}, {8F, 16F, -8F}, {-8F, 0F, 8F},
		{-8F, 16F, 8F}, {0F, 8F, 16F}, {0F, 8F, -16F}, {0F, -8F, 0F}, {0F, 24F, 0F}, {16F, 8F, 0F}, {-16F, 8F, 0F}
	};

	private final ModelPart SmallMet;
	private final ModelPart[] blocks = new ModelPart[14];

	public ModelMeteor(ModelPart root)
	{
		this.SmallMet = root.getChild("small_met");
		for (int i = 0; i < blocks.length; i++) {
			this.blocks[i] = root.getChild("block" + (i + 1));
		}
	}

	public static LayerDefinition createLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		CubeListBuilder cube = CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-8F, -8F, -8F, 16, 16, 16);
		root.addOrReplaceChild("small_met", cube, PartPose.offset(0F, 16F, 0F));
		for (int i = 0; i < BLOCK_PIVOTS.length; i++) {
			root.addOrReplaceChild("block" + (i + 1), cube, PartPose.offset(BLOCK_PIVOTS[i][0], BLOCK_PIVOTS[i][1], BLOCK_PIVOTS[i][2]));
		}
		return LayerDefinition.create(mesh, 64, 32);
	}

	public void renderWithSize(int mSize, PoseStack poseStack, VertexConsumer buffer, int light, int overlay) {
		if (mSize == 1) {
			SmallMet.render(poseStack, buffer, light, overlay);
			return;
		}
		if (mSize >= 2) {
			for (int i = 0; i < 8; i++) {
				blocks[i].render(poseStack, buffer, light, overlay);
			}
		}
		if (mSize == 3) {
			for (int i = 8; i < 14; i++) {
				blocks[i].render(poseStack, buffer, light, overlay);
			}
		}
	}
}
