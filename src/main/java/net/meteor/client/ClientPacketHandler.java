package net.meteor.client;

import net.meteor.common.network.PacketBlockedMeteor;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ClientPacketHandler {

	/** A burst of the meteor's block particles over the shield, like landing after a long fall. */
	public static void handleBlockedMeteor(PacketBlockedMeteor message) {
		Level world = Minecraft.getInstance().level;
		if (world == null) return;
		BlockState block = message.type.getRepresentingBlock().defaultBlockState();
		double d3 = (double)Math.min(0.2F + (float)10 / 15.0F, 10.0F);
		if (d3 > 2.5D)
		{
			d3 = 2.5D;
		}
		int l1 = (int)(150.0D * d3);
		for (int i2 = 0; i2 < l1; ++i2)
		{
			float f3 = Mth.randomBetween(world.random, 0.0F, ((float)Math.PI * 2F));
			double d5 = (double)Mth.randomBetween(world.random, 0.75F, 1.0F);
			double d6 = 0.20000000298023224D + d3 / 100.0D;
			double d7 = (double)(Mth.cos(f3) * 0.2F) * d5 * d5 * (d3 + 0.2D);
			double d8 = (double)(Mth.sin(f3) * 0.2F) * d5 * d5 * (d3 + 0.2D);
			world.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, block), (double)((float)message.x + 0.5F), (double)((float)message.y + 1.0F), (double)((float)message.z + 0.5F), d7, d6, d8);
		}
	}

}
