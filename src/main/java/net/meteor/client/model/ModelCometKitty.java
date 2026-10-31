package net.meteor.client.model;

import net.meteor.MeteorsMod;
import net.minecraft.client.model.OcelotModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.resources.ResourceLocation;

/**
 * The ocelot model wearing a space helmet. The helmet used the head's rotation point and angles, so it is
 * a child of the head now. Used with the vanilla CatModel.
 */
public class ModelCometKitty
{
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(MeteorsMod.MOD_ID, "comet_kitty"), "main");

	public static LayerDefinition createLayer() {
		MeshDefinition mesh = OcelotModel.createBodyMesh(CubeDeformation.NONE);
		mesh.getRoot().getChild("head").addOrReplaceChild("helmet", CubeListBuilder.create()
				.texOffs(34, 17).addBox("main", -3.5F, -4.0F, -5.0F, 7, 7, 8)
				.texOffs(48, 12).addBox("top", -1F, -6F, -3F, 2, 1, 4)
				.texOffs(14, 25).addBox("left", 4F, -2F, -3F, 1, 3, 4)
				.texOffs(24, 25).addBox("right", -5F, -2F, -3F, 1, 3, 4)
				.texOffs(6, 29).addBox("right2", -6F, -1F, -2F, 1, 1, 2)
				.texOffs(0, 29).addBox("left2", 5F, -1F, -2F, 1, 1, 2), PartPose.ZERO);
		return LayerDefinition.create(mesh, 64, 32);
	}
}
