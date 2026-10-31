package net.meteor.common.crash;

import net.meteor.common.EnumMeteor;
import net.meteor.common.MeteorBlocks;
import net.meteor.common.MeteorItems;
import net.meteor.common.SBAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

/** A ball of meteorite around a chest full of loot, lit up by glowstone. */
public class CrashUnknown extends CrashMeteorite
{
	public CrashUnknown(int Size, Explosion expl, EnumMeteor metType)
	{
		super(Size, expl, metType);
	}

	@Override
	public boolean generate(Level world, RandomSource random, int i, int j, int k)
	{
		int j1 = getFirstUncoveredBlock(world, i, k, j - this.crashSize * 4, j + this.crashSize * 4) + 1;

		SBAPI.generateCuboid(world, i + 1, j1 + 1, k + 1, i - 1, j1 - 1, k - 1, EnumMeteor.withHeat(MeteorBlocks.METEOR.get(), random.nextInt(4) + 1));
		int end = random.nextInt(5) + 4;
		for (int r = 0; r < end; r++) {
			Block id = random.nextBoolean() ? MeteorBlocks.FREZARITE.get() : MeteorBlocks.KREKNORITE.get();
			SBAPI.placeBlock(world, i + random.nextInt(3) - 1, j1 + random.nextInt(3) - 1, k + random.nextInt(3) - 1, EnumMeteor.withHeat(id, random.nextInt(4) + 1));
		}
		SBAPI.placeBlock(world, i, j1 + 2, k, Blocks.GLOWSTONE);
		SBAPI.placeBlock(world, i, j1 - 2, k, Blocks.GLOWSTONE);
		SBAPI.placeBlock(world, i + 2, j1, k, Blocks.GLOWSTONE);
		SBAPI.placeBlock(world, i - 2, j1, k, Blocks.GLOWSTONE);
		SBAPI.placeBlock(world, i, j1, k + 2, Blocks.GLOWSTONE);
		SBAPI.placeBlock(world, i, j1, k - 2, Blocks.GLOWSTONE);
		BlockPos chestPos = new BlockPos(i, j1, k);
		world.setBlock(chestPos, Blocks.CHEST.defaultBlockState(), 3);
		if (world.getBlockEntity(chestPos) instanceof ChestBlockEntity chest) {
			for (int i1 = 0; i1 < 8; i1++) {
				ItemStack item = getRandomLoot(random);
				if (!item.isEmpty()) {
					chest.setItem(random.nextInt(chest.getContainerSize()), item);
				}
			}
		}

		return true;
	}

	private int getFirstUncoveredBlock(Level world, int x, int z, int yStart, int yEnd)
	{
		int var3;

		for (var3 = yStart; !world.isEmptyBlock(new BlockPos(x, var3 + 1, z)) && var3 <= yEnd; ++var3) {}

		return var3;
	}

	public static ItemStack getRandomLoot(RandomSource random) {
		int r = random.nextInt(60);
		switch (r) {
		case 0:
		case 1:
		case 2:
			return new ItemStack(MeteorItems.itemMeteorChips.get(), random.nextInt(8) + 1);
		case 3:
		case 4:
		case 5:
			return new ItemStack(MeteorItems.itemFrezaCrystal.get(), random.nextInt(8) + 1);
		case 6:
		case 7:
		case 8:
			return new ItemStack(MeteorItems.itemKreknoChip.get(), random.nextInt(8) + 1);
		case 9:
		case 10:
		case 11:
			return new ItemStack(MeteorItems.MeteoriteBody.get(), 1);
		case 12:
		case 13:
		case 14:
			return ItemStack.EMPTY;
		case 15:
		case 16:
			return new ItemStack(MeteorItems.KreknoriteSword.get(), 1);
		case 17:
		case 18:
			return ItemStack.EMPTY;
		case 19:
		case 20:
		case 21:
		case 22:
			return new ItemStack(MeteorItems.itemRedMeteorGem.get(), random.nextInt(16) + 1);
		case 23:
		case 24:
		case 25:
			return new ItemStack(MeteorItems.KreknoriteBody.get(), 1);
		case 26:
		case 27:
		case 28:
		case 29:
			return new ItemStack(Items.DIAMOND, random.nextInt(2) + 1);
		case 30:
		case 31:
			return new ItemStack(Items.GOLD_INGOT, random.nextInt(4) + 1);
		case 32:
		case 33:
		case 34:
			return new ItemStack(MeteorItems.FrezaritePickaxe.get(), 1);
		case 35:
		case 36:
		case 37:
			return new ItemStack(MeteorItems.itemChocolateIceCream.get(), random.nextInt(6) + 1);
		case 38:
			return new ItemStack(MeteorItems.FrezariteSpade.get(), 1);
		case 39:
		case 40:
			return new ItemStack(MeteorItems.KreknoriteHelmet.get(), 1);
		case 41:
		case 42:
			return new ItemStack(MeteorItems.FrezariteHelmet.get(), 1);
		case 43:
		case 44:
			return new ItemStack(Items.REDSTONE, random.nextInt(16) + 1);
		case 45:
		case 46:
		case 47:
			return ItemStack.EMPTY;
		case 48:
		case 49:
			return new ItemStack(Blocks.IRON_BLOCK, random.nextInt(5) + 1);
		case 50:
		case 51:
		case 52:
			Item[] l = { MeteorItems.itemMeteorProximityDetector.get(), MeteorItems.itemMeteorTimeDetector.get(), MeteorItems.itemMeteorCrashDetector.get() };
			return new ItemStack(l[random.nextInt(3)], 1);
		}
		return ItemStack.EMPTY;
	}
}
