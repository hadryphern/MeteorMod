package net.meteor.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.meteor.client.model.ModelMeteorShield;
import net.meteor.client.model.ModelMeteorTimer;
import net.meteor.client.tileentity.TileEntityMeteorShieldRenderer;
import net.meteor.client.tileentity.TileEntityMeteorTimerRenderer;
import net.meteor.common.MeteorItems;
import net.meteor.common.block.BlockSlipperyStairs;
import net.meteor.common.item.ItemBlockSlippery;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

/** Replaces ShieldItemRenderer, TimerItemRenderer and SlipperyItemRenderer. */
public class MeteorItemRenderer extends BlockEntityWithoutLevelRenderer {

	private static MeteorItemRenderer instance;

	private ModelMeteorShield shieldModel;
	private ModelMeteorTimer timerModel;

	private MeteorItemRenderer() {
		super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
	}

	public static MeteorItemRenderer getInstance() {
		if (instance == null) {
			instance = new MeteorItemRenderer();
		}
		return instance;
	}

	@Override
	public void renderByItem(ItemStack stack, ItemDisplayContext type, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
		Minecraft mc = Minecraft.getInstance();
		Item item = stack.getItem();
		int age = mc.player != null ? mc.player.tickCount : 0;
		float partialTick = mc.getFrameTime();
		if (item == MeteorItems.METEOR_SHIELD.get()) {
			if (shieldModel == null) {
				shieldModel = new ModelMeteorShield(mc.getEntityModels().bakeLayer(ModelMeteorShield.LAYER));
			}
			poseStack.pushPose();
			poseStack.translate(0.5F, 1.5F, 0.5F);
			poseStack.mulPose(Axis.ZP.rotationDegrees(180F));
			shieldModel.render(5, partialTick, age, poseStack, buffer.getBuffer(RenderType.entityCutout(TileEntityMeteorShieldRenderer.shieldTexture)), light, overlay);
			poseStack.popPose();
		} else if (item == MeteorItems.METEOR_TIMER.get()) {
			if (timerModel == null) {
				timerModel = new ModelMeteorTimer(mc.getEntityModels().bakeLayer(ModelMeteorTimer.LAYER));
			}
			TileEntityMeteorTimerRenderer.renderMeteorTimer(timerModel, age + partialTick, poseStack, buffer.getBuffer(RenderType.entityCutout(TileEntityMeteorTimerRenderer.timerTex)), light, overlay);
		} else if (item instanceof ItemBlockSlippery slippery) {
			BlockState facade = ItemBlockSlippery.getStoredBlock(stack).defaultBlockState();
			if (slippery.getBlock() instanceof BlockSlipperyStairs && facade.hasProperty(StairBlock.FACING)) {
				// Same orientation as the vanilla stairs item model
				facade = facade.setValue(StairBlock.FACING, Direction.EAST);
			}
			mc.getBlockRenderer().renderSingleBlock(facade, poseStack, buffer, light, overlay, ModelData.EMPTY, null);
		}
	}

}
