package net.meteor.client.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.meteor.common.tileentity.TileEntitySlippery;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.ForgeHooksClient;

/** Draws the facade block in place of the slippery block, with the world's lighting like RenderBlocks did. */
public class TileEntitySlipperyRenderer implements BlockEntityRenderer<TileEntitySlippery> {

	private final BlockRenderDispatcher blockRenderer;

	public TileEntitySlipperyRenderer(BlockEntityRendererProvider.Context context) {
		this.blockRenderer = context.getBlockRenderDispatcher();
	}

	@Override
	public void render(TileEntitySlippery slip, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
		if (slip.getLevel() == null) {
			return;
		}
		BlockState facade = getFacadeState(slip.getFacadeBlock(), slip.getBlockState());
		ForgeHooksClient.renderPistonMovedBlocks(slip.getBlockPos(), facade, poseStack, buffer, slip.getLevel(), true, overlay, blockRenderer);
	}

	/** Slippery stairs use the facade stairs turned the same way. */
	public static BlockState getFacadeState(Block facadeBlock, BlockState slipperyState) {
		BlockState state = facadeBlock.defaultBlockState();
		if (slipperyState.getBlock() instanceof StairBlock) {
			state = copy(slipperyState, state, StairBlock.FACING);
			state = copy(slipperyState, state, StairBlock.HALF);
			state = copy(slipperyState, state, StairBlock.SHAPE);
		}
		return state;
	}

	private static <T extends Comparable<T>> BlockState copy(BlockState from, BlockState to, Property<T> property) {
		if (from.hasProperty(property) && to.hasProperty(property)) {
			return to.setValue(property, from.getValue(property));
		}
		return to;
	}

}
