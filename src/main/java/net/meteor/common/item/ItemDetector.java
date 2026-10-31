package net.meteor.common.item;

import java.util.List;

import net.meteor.common.ClientHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

/**
 * Meteor detectors work like compasses (see client.DetectorAngle).
 * Type 0 = Proximity (closest incoming meteor), 1 = Time (next meteor to fall), 2 = Crash (last crash zone).
 */
public class ItemDetector extends ItemMeteorsMod {

	private final int type;
	private String dots = ".";
	private long lastDot = 0L;

	public ItemDetector(int t) {
		super(new Properties().stacksTo(1));
		this.type = t;
	}

	public int getDetectorType() {
		return type;
	}

	@Override
	public void appendHoverText(ItemStack par1ItemStack, Level world, List<Component> par3List, TooltipFlag flag) {
		if (world == null) return;
		long worldTime = world.getGameTime();
		if (worldTime - lastDot > 10L) {
			lastDot = worldTime;
			dots = dots.length() > 5 ? "." : dots + ".";
		}
		if (type == 1) {
			par3List.add(Component.translatable("Detector.time").withStyle(ChatFormatting.AQUA));
			if (ClientHandler.nearestTimeLocation == null) {
				par3List.add(Component.translatable("Detector.scanning").append(dots));
			} else {
				par3List.add(Component.translatable("Detector.detected").withStyle(ChatFormatting.GREEN));
			}
		} else if (type == 0) {
			par3List.add(Component.translatable("Detector.proximity").withStyle(ChatFormatting.LIGHT_PURPLE));
			Vec3 pos = DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> net.meteor.client.ClientAccess::getPlayerPosition);
			if (pos == null || ClientHandler.getClosestIncomingMeteor(pos.x, pos.z) == null) {
				par3List.add(Component.translatable("Detector.scanning").append(dots));
			} else {
				par3List.add(Component.translatable("Detector.detected").withStyle(ChatFormatting.GREEN));
			}
		} else {
			par3List.add(Component.translatable("Detector.crash").withStyle(ChatFormatting.RED));
			if (ClientHandler.lastCrashLocation == null) {
				par3List.add(Component.translatable("CrashDetector.noActivity"));
				par3List.add(Component.translatable("Detector.scanning").append(dots));
			} else {
				if (ClientHandler.lastCrashLocation.inOrbit) {
					par3List.add(Component.translatable("Detector.orbitalEntrance").withStyle(ChatFormatting.GREEN));
				} else {
					par3List.add(Component.translatable("CrashDetector.zoneLocated").withStyle(ChatFormatting.GREEN));
				}
			}
		}
	}

}
