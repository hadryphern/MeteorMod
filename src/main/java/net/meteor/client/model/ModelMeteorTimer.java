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

/** The Meteor Timer: a half slab with a meteorite, frezarite and kreknorite element floating above it. */
public class ModelMeteorTimer {

	public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(MeteorsMod.MOD_ID, "meteor_timer"), "main");

	public final ModelPart Shape1;
	public final ModelPart base;
	public final ModelPart element_meteorite;
	public final ModelPart element_frezarite;
	public final ModelPart element_kreknorite;

	public ModelMeteorTimer(ModelPart root) {
		this.Shape1 = root.getChild("shape1");
		this.base = root.getChild("base");
		this.element_meteorite = root.getChild("element_meteorite");
		this.element_frezarite = root.getChild("element_frezarite");
		this.element_kreknorite = root.getChild("element_kreknorite");
	}

	public static LayerDefinition createLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		root.addOrReplaceChild("shape1", CubeListBuilder.create().texOffs(0, 36).addBox(0F, 0F, 0F, 16, 8, 16), PartPose.offset(0F, 0F, 0F));
		root.addOrReplaceChild("base", CubeListBuilder.create().texOffs(4, 0).addBox(0F, 0F, 0F, 14, 1, 14), PartPose.offset(1F, 8F, 1F));
		root.addOrReplaceChild("element_meteorite", CubeListBuilder.create().texOffs(0, 7).addBox(-2F, 0F, -1F, 1, 3, 2), PartPose.offset(8F, 10F, 8F));
		root.addOrReplaceChild("element_frezarite", CubeListBuilder.create().texOffs(9, 0).addBox(-4F, 0F, -1F, 1, 3, 2), PartPose.offset(8F, 10F, 8F));
		root.addOrReplaceChild("element_kreknorite", CubeListBuilder.create().texOffs(0, 0).addBox(6F, 0F, -1F, 1, 3, 2), PartPose.offset(8F, 10F, 8F));
		return LayerDefinition.create(mesh, 64, 64);
	}

	public void renderAll(PoseStack poseStack, VertexConsumer buffer, int light, int overlay) {
		this.Shape1.render(poseStack, buffer, light, overlay);
		this.base.render(poseStack, buffer, light, overlay);
		this.element_meteorite.render(poseStack, buffer, light, overlay);
		this.element_frezarite.render(poseStack, buffer, light, overlay);
		this.element_kreknorite.render(poseStack, buffer, light, overlay);
	}
}
