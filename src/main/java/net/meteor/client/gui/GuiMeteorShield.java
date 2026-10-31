package net.meteor.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.meteor.MeteorsMod;
import net.meteor.common.EnumMeteor;
import net.meteor.common.MeteorItems;
import net.meteor.common.block.container.ContainerMeteorShield;
import net.meteor.common.tileentity.TileEntityMeteorShield;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class GuiMeteorShield extends AbstractContainerScreen<ContainerMeteorShield> {

	private static final ResourceLocation shieldGuiTexture = ResourceLocation.fromNamespaceAndPath(MeteorsMod.MOD_ID, "textures/gui/container/meteor_shield.png");

	private final TileEntityMeteorShield shield;
	private Button cometButton;
	private boolean hoveringComet;

	public GuiMeteorShield(ContainerMeteorShield container, Inventory inventoryPlayer, Component title) {
		super(container, inventoryPlayer, title);
		this.shield = container.getShield();
	}

	private static Component getCometButtonText(boolean blockComets) {
		Component status = blockComets ? Component.translatable("options.on").withStyle(ChatFormatting.GREEN) : Component.translatable("options.off").withStyle(ChatFormatting.RED);
		return Component.translatable("gui.toggleCometButton", status);
	}

	@Override
	protected void init() {
		super.init();
		// id, xpos, ypos, width, height, string
		this.cometButton = this.addRenderableWidget(Button.builder(getCometButtonText(shield.getPreventComets()), button -> {
			shield.pressButton(0);
			button.setMessage(getCometButtonText(!shield.getPreventComets()));
		}).bounds(leftPos + 2, topPos - 22, imageWidth - 4, 20).build());
	}

	@Override
	protected void containerTick() {
		super.containerTick();
		this.cometButton.setMessage(getCometButtonText(shield.getPreventComets()));
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
		graphics.blit(shieldGuiTexture, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight);
		if (shield.getPowerLevel() > 0) {
			graphics.blit(shieldGuiTexture, leftPos + 47, topPos + 60, 0, 166, 16, 16);
		}
		// Faded items show what goes in each slot
		if (shield.getPowerLevel() == 0) {
			renderGhostItem(graphics, new ItemStack(MeteorItems.itemMeteorChips.get()), leftPos + 47, topPos + 60);
		}
		for (int i = 0; i < 4; ++i) {
			renderGhostItem(graphics, new ItemStack(MeteorItems.itemRedMeteorGem.get()), leftPos + 67 + i * 29, topPos + 60);
		}
	}

	private void renderGhostItem(GuiGraphics graphics, ItemStack stack, int x, int y) {
		graphics.renderFakeItem(stack, x, y);
		graphics.fill(RenderType.guiGhostRecipeOverlay(), x, y, x + 16, y + 16, 0x668B8B8B);
	}

	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
		List<Component> displayInfo = shield.getDisplayInfo();
		float f = 0.65F;
		graphics.pose().pushPose();
		graphics.pose().scale(f, f, 1.0F);
		for (int i = 0; i < 3; i++) {
			graphics.drawString(this.font, displayInfo.get(i), 74, 4 + (i * this.font.lineHeight + 8), -1, false);
		}
		if (displayInfo.size() > 3) {
			graphics.drawString(this.font, displayInfo.get(3), 74, 50, -1, false);
		}
		if (displayInfo.size() > 4) {
			graphics.drawString(this.font, displayInfo.get(4), 80, 67, -1, false);
		}
		// Comet prevention may be enabled so should double-check list size
		if (displayInfo.size() > 5) {
			graphics.drawString(this.font, displayInfo.get(5), 150, 67, -1, false);
		}
		graphics.pose().popPose();

		this.hoveringComet = false;
		if (displayInfo.size() > 5) {
			graphics.renderItem(shield.getCometType().getRepresentingItem(), 148, 37);
			this.hoveringComet = this.isHovering(148, 37, 16, 16, mouseX, mouseY);
		}
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		this.renderBackground(graphics);
		super.render(graphics, mouseX, mouseY, partialTick);
		if (this.hoveringComet) {
			EnumMeteor type = shield.getCometType();
			List<Component> info = new ArrayList<Component>();
			info.add(Component.translatable("info.meteorShield.cometType"));
			info.add(EnumMeteor.getLocalName(type).copy().withStyle(type.getChatColor()));
			graphics.renderComponentTooltip(this.font, info, mouseX, mouseY);
		} else {
			this.renderTooltip(graphics, mouseX, mouseY);
		}
	}

}
