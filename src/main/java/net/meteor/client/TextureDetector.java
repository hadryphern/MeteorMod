package net.meteor.client;

import net.meteor.common.ClientHandler;
import net.meteor.common.climate.CrashLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * The detector needles. 1.7.10 animated the texture like a compass; now the frame is an item model override,
 * picked with the "meteors:angle" property. One needle per detector type.
 */
public class TextureDetector {

	private static final int FRAMES = 32;
	private static final TextureDetector[] DETECTORS = { new TextureDetector(0), new TextureDetector(1), new TextureDetector(2) };

	public double currentAngle;
	public double angleDelta;
	public final int detectorType;		// 0 = Proximity, 1 = Time, 2 = Crash
	private int frameCounter;

	private TextureDetector(int type) {
		this.detectorType = type;
	}

	/** @return the frame to show, as a value between 0 and 1 */
	public static float getAngle(int detectorType) {
		return (DETECTORS[detectorType].frameCounter + 0.5F) / FRAMES;
	}

	/** Called every client tick, when 1.7.10 updated texture animations. */
	public static void updateAnimations() {
		for (TextureDetector detector : DETECTORS) {
			detector.updateAnimation();
		}
	}

	public void updateAnimation()
	{
		Minecraft minecraft = Minecraft.getInstance();

		if (minecraft.level != null && minecraft.player != null)
		{
			this.updateCompass(minecraft.level, minecraft.player.getX(), minecraft.player.getZ(), (double)minecraft.player.getYRot(), false, false);
		}
		else
		{
			this.updateCompass((Level)null, 0.0D, 0.0D, 0.0D, true, false);
		}
	}

	public void updateCompass(Level par1World, double par2, double par4, double par6, boolean par8, boolean par9)
	{
		double d3 = 0.0D;

		if (par1World != null && !par8)
		{
			BlockPos chunkcoordinates;
			if (this.detectorType == 0) {
				chunkcoordinates = ClientHandler.getClosestIncomingMeteor(par2, par4);
			} else if (this.detectorType == 1) {
				chunkcoordinates = ClientHandler.nearestTimeLocation;
			} else {
				CrashLocation cl = ClientHandler.lastCrashLocation;
				if (cl != null) {
					chunkcoordinates = new BlockPos(cl.x, cl.y, cl.z);
				} else {
					chunkcoordinates = null;
				}
			}
			if (chunkcoordinates != null) {
				double d4 = (double)chunkcoordinates.getX() - par2;
				double d5 = (double)chunkcoordinates.getZ() - par4;
				par6 %= 360.0D;
				d3 = -((par6 - 90.0D) * Math.PI / 180.0D - Math.atan2(d5, d4));
			} else {
				d3 = Math.random() * Math.PI * 2.0D;
			}

			if (!par1World.dimensionType().natural())
			{
				d3 = Math.random() * Math.PI * 2.0D;
			}
		}

		if (par9)
		{
			this.currentAngle = d3;
		}
		else
		{
			double d6;

			for (d6 = d3 - this.currentAngle; d6 < -Math.PI; d6 += (Math.PI * 2D))
			{
				;
			}

			while (d6 >= Math.PI)
			{
				d6 -= (Math.PI * 2D);
			}

			if (d6 < -1.0D)
			{
				d6 = -1.0D;
			}

			if (d6 > 1.0D)
			{
				d6 = 1.0D;
			}

			this.angleDelta += d6 * 0.1D;
			this.angleDelta *= 0.8D;
			this.currentAngle += this.angleDelta;
		}

		int i;

		for (i = (int)((this.currentAngle / (Math.PI * 2D) + 1.0D) * (double)FRAMES) % FRAMES; i < 0; i = (i + FRAMES) % FRAMES)
		{
			;
		}

		this.frameCounter = i;
	}
}
