package net.meteor.common.item;

import java.util.function.Consumer;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

/** Block items drawn with the block's 3D model (the Meteor Shield and Meteor Timer IItemRenderers of 1.7.10). */
public class ItemBlockRendered extends BlockItem {

	public ItemBlockRendered(Block block, Properties props) {
		super(block, props);
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new IClientItemExtensions() {
			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return net.meteor.client.render.MeteorItemRenderer.getInstance();
			}
		});
	}

}
