package net.meteor.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

/** Client-only lookups used from common code through DistExecutor. */
public class ClientAccess {

	public static Vec3 getPlayerPosition() {
		Minecraft mc = Minecraft.getInstance();
		return mc.player == null ? null : mc.player.position();
	}

}
