package net.meteor.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.meteor.MeteorsMod;
import net.meteor.common.FreezerRecipes.RecipeType;
import net.meteor.common.block.container.ContainerFreezingMachine;
import net.meteor.common.tileentity.TileEntityFreezingMachine;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

public class GuiFreezingMachine extends AbstractContainerScreen<ContainerFreezingMachine> {

	public static final ResourceLocation freezingMachineTextures = ResourceLocation.fromNamespaceAndPath(MeteorsMod.MOD_ID, "textures/gui/container/freezing_machine.png");

	public GuiFreezingMachine(ContainerFreezingMachine container, Inventory playerInv, Component title) {
		super(container, playerInv, title);
	}

	@Override
	protected void init() {
		super.init();
		this.addRenderableWidget(new GuiButtonFreezerRecipeMode(leftPos + 149, topPos + 60, 20, 20, this.menu, button -> this.menu.getFreezer().pressButton(0)));
	}

	/** Stretches the fluid's still texture over the area, tinted like in the world. */
	public static void drawFluid(GuiGraphics graphics, FluidStack fluidStack, int x, int y, int width, int height) {
		if (fluidStack.isEmpty() || height <= 0) return;
		IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(fluidStack.getFluid());
		TextureAtlasSprite icon = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(props.getStillTexture(fluidStack));
		int color = props.getTintColor(fluidStack);
		graphics.setColor((color >> 16 & 255) / 255.0F, (color >> 8 & 255) / 255.0F, (color & 255) / 255.0F, (color >> 24 & 255) / 255.0F);
		graphics.blit(x, y, 0, width, height, icon);
		graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
		int k = (this.width - this.imageWidth) / 2;
		int l = (this.height - this.imageHeight) / 2;
		graphics.blit(freezingMachineTextures, k, l, 0, 0, this.imageWidth, this.imageHeight);

		// Draw Fluids in Tank
		FluidStack fluidStack = this.menu.getFluid();
		int fluidAmount = this.menu.getFluidAmount();
		if (!fluidStack.isEmpty() && fluidAmount > 0) {
			int height = (int) (((double)fluidAmount / (double)TileEntityFreezingMachine.TANK_CAPACITY) * 69);
			int drawnHeight = 0;
			for (int i = height / 16; i >= 0; i--) {
				if (i > 0) {
					drawFluid(graphics, fluidStack, k + 13, l + 76 - 16 - drawnHeight, 20, 16);
					drawnHeight += 16;
				} else {
					drawFluid(graphics, fluidStack, k + 13, l + 76 - drawnHeight - height, 20, height);
				}
				height -= 16;
			}
		}

		graphics.blit(freezingMachineTextures, k + 13, l + 7, 176, 31, 20, 69);

		if (this.menu.isFreezing()) {
			int i1 = this.menu.getBurnTimeRemainingScaled(13);
			graphics.blit(freezingMachineTextures, k + 73, l + 36 + 12 - i1, 176, 12 - i1, 14, i1 + 2);
			i1 = this.menu.getCookProgressScaled(24);
			graphics.blit(freezingMachineTextures, k + 96, l + 34, 176, 14, i1 + 1, 16);
		}
	}

	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
		Component s = Component.translatable("block.meteors.freezer");
		int i = this.font.width(s);
		graphics.drawString(this.font, s, 168 - i, 6, 4210752, false);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		this.renderBackground(graphics);
		super.render(graphics, mouseX, mouseY, partialTick);
		if (this.isHovering(13, 7, 20, 69, mouseX, mouseY)) {
			List<Component> info = new ArrayList<Component>();
			FluidStack fluidStack = this.menu.getFluid();
			if (!fluidStack.isEmpty() && this.menu.getFluidAmount() > 0) {
				info.add(fluidStack.getDisplayName());
			}
			info.add(Component.literal(this.menu.getFluidAmount() + " / " + TileEntityFreezingMachine.TANK_CAPACITY + " mB"));
			graphics.renderComponentTooltip(this.font, info, mouseX, mouseY);
		} else if (this.isHovering(149, 60, 20, 20, mouseX, mouseY)) {
			List<Component> info = new ArrayList<Component>();
			RecipeType type = this.menu.getRecipeMode();
			String key = "info.freezer." + type.name() + "Mode";
			info.add(Component.translatable(key).withStyle(ChatFormatting.AQUA));
			info.addAll(getFormattedLines(key + ".desc", ChatFormatting.WHITE));
			graphics.renderComponentTooltip(this.font, info, mouseX, mouseY);
		} else {
			this.renderTooltip(graphics, mouseX, mouseY);
		}
	}

	/** The descriptions have a %s where the line should break (the old Util.getFormattedLines). */
	private static List<Component> getFormattedLines(String unlocal, ChatFormatting ecf) {
		List<Component> lines = new ArrayList<Component>();
		String s = Component.translatable(unlocal, "\n").getString();
		for (String line : s.split("\\n")) {
			lines.add(Component.literal(line).withStyle(ecf));
		}
		return lines;
	}

}
