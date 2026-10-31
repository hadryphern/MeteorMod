package net.meteor.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;

import net.meteor.common.MeteorItems;
import net.meteor.common.block.container.ContainerFreezingMachine;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

/** Cycles the Freezer between item, fluid, both and either recipe modes. */
public class GuiButtonFreezerRecipeMode extends Button {

	private static final FluidStack WATER = new FluidStack(Fluids.WATER, 1000);

	private final ContainerFreezingMachine freezer;

	public GuiButtonFreezerRecipeMode(int xPos, int yPos, int width, int height, ContainerFreezingMachine freezer, OnPress onPress) {
		super(xPos, yPos, width, height, Component.empty(), onPress, DEFAULT_NARRATION);
		this.freezer = freezer;
	}

	@Override
	protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		int k = this.isHoveredOrFocused() ? 1 : 0;
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		graphics.blit(GuiFreezingMachine.freezingMachineTextures, this.getX(), this.getY(), k * 20, 166, this.width, this.height);
		int state = freezer.getRecipeMode().getID();
		int x = this.getX();
		int y = this.getY();
		if (state == 0) {	// Item Only - Frozen Iron
			graphics.renderItem(new ItemStack(MeteorItems.FrozenIron.get()), x + 2, y + 2);
		} else if (state == 1) { // Fluid only - Water
			GuiFreezingMachine.drawFluid(graphics, WATER, x + 2, y + 2, 16, 16);
		} else if (state == 2) { // Both - water and stone (slippery stone)
			GuiFreezingMachine.drawFluid(graphics, WATER, x + 2, y + 2, 16, 16);
			graphics.renderItem(new ItemStack(Blocks.STONE), x + 2, y + 2);
		} else if (state == 3) { // Either - water and frozen iron
			GuiFreezingMachine.drawFluid(graphics, WATER, x + 2, y + 2, 16, 16);
			graphics.pose().pushPose();
			graphics.pose().translate(x + 3, y + 3, 0.0F);
			graphics.pose().scale(14.0F / 16.0F, 14.0F / 16.0F, 1.0F);
			graphics.renderItem(new ItemStack(MeteorItems.FrozenIron.get()), 0, 0);
			graphics.pose().popPose();
		}
	}

}
