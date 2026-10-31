// Structure Builder API
// Coded by AlexDGr8r

package net.meteor.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The parts of the Structure Builder API the mod uses. Door, bed and slab metadata helpers are gone since
 * block states describe those now.
 * @author AlexDGr8r
 */
public class SBAPI {

	/** Place block at defined coordinates. */
	public static void placeBlock(Level world, int x, int y, int z, BlockState state) {
		world.setBlock(new BlockPos(x, y, z), state, 3);
	}

	public static void placeBlock(Level world, int x, int y, int z, Block block) {
		placeBlock(world, x, y, z, block.defaultBlockState());
	}

	public static void generateCuboid(Level world, int x1, int y1, int z1, int x2, int y2, int z2, Block block) {
		generateCuboid(world, x1, y1, z1, x2, y2, z2, block.defaultBlockState(), false);
	}

	public static void generateCuboid(Level world, int x1, int y1, int z1, int x2, int y2, int z2, BlockState state) {
		generateCuboid(world, x1, y1, z1, x2, y2, z2, state, false);
	}

	/**
	 * Generates a cuboid between both corners. When hollow, only the outside is placed: a ring for floors,
	 * a frame for walls (one coordinate equal) and a shell for anything else.
	 */
	public static void generateCuboid(Level world, int x1, int y1, int z1, int x2, int y2, int z2, BlockState state, boolean hollow) {
		int minX = Math.min(x1, x2), maxX = Math.max(x1, x2);
		int minY = Math.min(y1, y2), maxY = Math.max(y1, y2);
		int minZ = Math.min(z1, z2), maxZ = Math.max(z1, z2);
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					if (!hollow || onEdge(x, minX, maxX) || onEdge(y, minY, maxY) || onEdge(z, minZ, maxZ)) {
						placeBlock(world, x, y, z, state);
					}
				}
			}
		}
	}

	private static boolean onEdge(int v, int min, int max) {
		return min != max && (v == min || v == max);
	}

}
