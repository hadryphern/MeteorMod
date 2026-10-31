package net.meteor.client.model;

import net.meteor.MeteorsMod;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

/** A creeper with an antenna on its head. Used with the vanilla CreeperModel. */
public class ModelAlienCreeper
{
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(MeteorsMod.MOD_ID, "alien_creeper"), "main");
	public static final ModelLayerLocation ARMOR_LAYER = new ModelLayerLocation(new ResourceLocation(MeteorsMod.MOD_ID, "alien_creeper"), "armor");

	public static LayerDefinition createLayer(CubeDeformation f) {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		// The antenna boxes were added without the inflation, even on the charged layer
		root.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, f)
				.texOffs(33, 6).addBox(-0.5F, -14F, -0.5F, 1, 6, 1)
				.texOffs(33, 1).addBox(-1F, -16F, -1F, 2, 2, 2), PartPose.offset(0.0F, 6.0F, 0.0F));
		root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, f), PartPose.offset(0.0F, 6.0F, 0.0F));
		CubeListBuilder leg = CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, f);
		root.addOrReplaceChild("right_hind_leg", leg, PartPose.offset(-2.0F, 18.0F, 4.0F));
		root.addOrReplaceChild("left_hind_leg", leg, PartPose.offset(2.0F, 18.0F, 4.0F));
		root.addOrReplaceChild("right_front_leg", leg, PartPose.offset(-2.0F, 18.0F, -4.0F));
		root.addOrReplaceChild("left_front_leg", leg, PartPose.offset(2.0F, 18.0F, -4.0F));
		return LayerDefinition.create(mesh, 64, 32);
	}
}
