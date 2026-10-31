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
import net.minecraft.util.Mth;

/** The Meteor Shield. The three floating layers appear, spin and bob with the shield's power level. */
public class ModelMeteorShield
{
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(MeteorsMod.MOD_ID, "meteor_shield"), "main");

	private final ModelPart BaseShape;
	private final ModelPart TopLayer;
	private final ModelPart MiddleLayer;
	private final ModelPart BottomLayer;
	private final ModelPart[] staticParts;
	private final float topY;
	private final float midY;
	private final float botY;

	public ModelMeteorShield(ModelPart root)
	{
		BaseShape = root.getChild("base_shape");
		TopLayer = root.getChild("top_layer");
		MiddleLayer = root.getChild("middle_layer");
		BottomLayer = root.getChild("bottom_layer");
		topY = TopLayer.y;
		midY = MiddleLayer.y;
		botY = BottomLayer.y;
		String[] names = { "side1", "side2", "side3", "side4", "leg1", "leg2", "leg3", "leg4", "foot1", "foot2", "foot3", "foot4",
				"inner_slope1", "inner_slope2", "inner_slope3", "inner_slope4", "side_panel1", "side_panel2" };
		staticParts = new ModelPart[names.length];
		for (int i = 0; i < names.length; i++) {
			staticParts[i] = root.getChild(names[i]);
		}
	}

	private static void add(PartDefinition root, String name, int u, int v, float x, float y, float z, int w, int h, int d, float px, float py, float pz, float rx, float ry, float rz) {
		root.addOrReplaceChild(name, CubeListBuilder.create().texOffs(u, v).mirror().addBox(x, y, z, w, h, d), PartPose.offsetAndRotation(px, py, pz, rx, ry, rz));
	}

	public static LayerDefinition createLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		add(root, "base_shape", 0, 0, -6F, 0F, -6F, 12, 0, 12, 0F, 15F, 0F, 0F, 0F, 0F);
		add(root, "top_layer", 0, 14, -5F, -8F, -5F, 10, 1, 10, 0F, 15F, 0F, 0F, 0F, 0F);
		add(root, "middle_layer", 0, 26, -4F, -10F, -4F, 8, 1, 8, 0F, 15F, 0F, 0F, 0F, 0F);
		add(root, "bottom_layer", 0, 35, -3F, -12F, -3F, 6, 1, 6, 0F, 15F, 0F, 0F, 0F, 0F);
		add(root, "side1", 66, 0, -7F, -7F, -7F, 14, 10, 1, 0F, 15F, 0F, 0F, 0F, 0F);
		add(root, "side2", 66, 0, -7F, -7F, 6F, 14, 10, 1, 0F, 15F, 0F, 0F, 0F, 0F);
		add(root, "side3", 83, 0, -7F, -7F, -7F, 1, 10, 14, 0F, 15F, 0F, 0F, 0F, 0F);
		add(root, "side4", 83, 0, 6F, -7F, -7F, 1, 10, 14, 0F, 15F, 0F, 0F, 0F, 0F);
		add(root, "leg1", 48, 0, -0.5F, 0F, -0.5F, 1, 6, 1, -6F, 18F, 6F, 0.2617994F, -0.7853982F, 0F);
		add(root, "leg2", 48, 0, -0.5F, 0F, -0.5F, 1, 6, 1, 6F, 18F, 6F, 0.2617994F, 0.7853982F, 0F);
		add(root, "leg3", 48, 0, -0.5F, 0F, -0.5F, 1, 6, 1, 6F, 18F, -6F, -0.2617994F, -0.7853982F, 0F);
		add(root, "leg4", 48, 0, -0.5F, 0F, -0.5F, 1, 6, 1, -6F, 18F, -6F, -0.2617994F, 0.7853982F, 0F);
		add(root, "foot1", 52, 0, -0.5F, 0F, -0.5F, 1, 1, 2, -7F, 23F, 7F, 0F, -0.7853982F, 0F);
		add(root, "foot2", 52, 0, -0.5F, 0F, -0.5F, 1, 1, 2, 7F, 23F, 7F, 0F, 0.7853982F, 0F);
		add(root, "foot3", 52, 0, -0.5F, 0F, -0.5F, 1, 1, 2, -7F, 23F, -7F, 0F, -2.356194F, 0F);
		add(root, "foot4", 52, 0, -0.5F, 0F, -0.5F, 1, 1, 2, 7F, 23F, -7F, 0F, 2.356194F, 0F);
		add(root, "inner_slope1", 32, 26, -6F, 1F, 1F, 12, 0, 7, 0F, 15F, 0F, 0.8203047F, 0F, 0F);
		add(root, "inner_slope2", 32, 26, -6F, 1F, -8F, 12, 0, 7, 0F, 15F, 0F, -0.8203047F, 0F, 0F);
		add(root, "inner_slope3", 32, 14, -8F, 1F, -6F, 7, 0, 12, 0F, 15F, 0F, 0F, 0F, 0.8203047F);
		add(root, "inner_slope4", 32, 14, 1F, 1F, -6F, 7, 0, 12, 0F, 15F, 0F, 0F, 0F, -0.8203047F);
		add(root, "side_panel1", 32, 34, 6F, 1F, -2F, 1, 4, 4, 0F, 12F, 0F, 0F, 0F, -0.2617994F);
		add(root, "side_panel2", 32, 34, -7F, 1F, -2F, 1, 4, 4, 0F, 12F, 0F, 0F, 0F, 0.2617994F);
		return LayerDefinition.create(mesh, 128, 64);
	}

	public void render(int pLevel, float partialTick, int age, PoseStack poseStack, VertexConsumer buffer, int light, int overlay)
	{
		setRotationAngles(partialTick, pLevel, age);
		float bobModifier = Mth.sin(((float)age + partialTick) / 1600F * 360F) * 0.5F - 0.3F;
		BottomLayer.y = botY + bobModifier;
		MiddleLayer.y = midY + bobModifier;
		TopLayer.y = topY + bobModifier;
		BaseShape.render(poseStack, buffer, light, overlay);
		switch (pLevel) {
			case 5:
			case 4:
				BottomLayer.render(poseStack, buffer, light, overlay);
			case 3:
				MiddleLayer.render(poseStack, buffer, light, overlay);
			case 2:
				TopLayer.render(poseStack, buffer, light, overlay);
		}
		for (ModelPart part : staticParts) {
			part.render(poseStack, buffer, light, overlay);
		}
	}

	private void setRotationAngles(float f, int pLevel, int age)
	{
		if (pLevel == 0) {
			return;
		}
		TopLayer.yRot = ((float)age + f) / (140F / pLevel);
		MiddleLayer.yRot = ((float)age + f) / (70F / pLevel);
		BottomLayer.yRot = ((float)age + f) / (40F / pLevel);
	}
}
