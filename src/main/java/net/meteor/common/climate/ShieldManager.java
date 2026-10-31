package net.meteor.common.climate;

import java.util.ArrayList;
import java.util.List;

import net.meteor.common.IMeteorShield;
import net.meteor.common.MeteorConfig;
import net.meteor.common.MeteorItems;
import net.meteor.common.crash.CrashUnknown;
import net.meteor.common.network.MeteorNetwork;
import net.meteor.common.network.PacketBlockedMeteor;
import net.meteor.common.tileentity.TileEntityMeteorShield;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class ShieldManager {

	private final ServerLevel theWorld;

	private static final RandomSource random = RandomSource.create();

	public final List<IMeteorShield> meteorShields;

	public ShieldManager(ServerLevel w, List<IMeteorShield> shields) {
		this.theWorld = w;
		this.meteorShields = shields;
	}

	/** Adds the shield, replacing any other shield (loaded or not) at the same position. */
	public void addShield(IMeteorShield shield) {
		removeShield(shield);
		meteorShields.add(shield);
	}

	public boolean removeShield(IMeteorShield shield) {
		return meteorShields.removeIf(s -> IMeteorShield.samePosition(s, shield));
	}

	public IMeteorShield getClosestShield(int x, int z) {
		IMeteorShield closest = null;
		double distance = -1;
		for (IMeteorShield shield : this.meteorShields) {
			if (closest == null) {
				closest = shield;
				distance = getDistance(x, z, shield.getX(), shield.getZ());
			} else {
				double d = getDistance(x, z, shield.getX(), shield.getZ());
				if (d < distance) {
					distance = d;
					closest = shield;
				}
			}
		}
		return closest;
	}

	public IMeteorShield getClosestShieldInRange(int x, int z) {
		IMeteorShield shield = getClosestShield(x, z);
		if (shield != null) {
			double distance = getDistance(x, z, shield.getX(), shield.getZ());
			return distance <= shield.getRange() ? shield : null;
		}
		return null;
	}

	public List<IMeteorShield> getShieldsInRange(int x, int z) {
		List<IMeteorShield> shields = new ArrayList<IMeteorShield>();
		for (IMeteorShield shield : this.meteorShields) {
			double d = getDistance(x, z, shield.getX(), shield.getZ());
			if (d <= shield.getRange()) {
				shields.add(shield);
			}
		}
		return shields;
	}

	public void sendMeteorMaterialsToShield(IMeteorShield shield, GhostMeteor gMeteor) {
		BlockPos pos = new BlockPos(shield.getX(), shield.getY(), shield.getZ());
		// Loads the chunk if needed, like getTileEntity did in 1.7.10
		if (theWorld.getBlockEntity(pos) instanceof TileEntityMeteorShield tShield) {

			MeteorNetwork.sendToAllAround(new PacketBlockedMeteor(shield.getX(), shield.getY(), shield.getZ(), gMeteor.type), theWorld, shield.getX(), shield.getY(), shield.getZ(), 64);

			List<ItemStack> items = new ArrayList<ItemStack>();
			int r = random.nextInt(100);
			switch (gMeteor.type.getID()) {
				case 0:
					items.add(new ItemStack(MeteorItems.itemMeteorChips.get(), random.nextInt(4 * gMeteor.size) + 1));
					if (r < 15) {
						items.add(new ItemStack(MeteorItems.itemRedMeteorGem.get(), random.nextInt(2 * gMeteor.size) + 1));
					}
					break;
				case 1:
					items.add(new ItemStack(MeteorItems.itemFrezaCrystal.get(), random.nextInt(4 * gMeteor.size) + 1));
					if (r < 20) {
						items.add(new ItemStack(Blocks.ICE, random.nextInt(3 * gMeteor.size) + 1));
					}
					break;
				case 2:
					items.add(new ItemStack(MeteorItems.itemKreknoChip.get(), random.nextInt(3 * gMeteor.size) + 1));
					if (gMeteor.size >= MeteorConfig.getMinMeteorSizeForPortal()) {
						if (r < 20) {
							items.add(new ItemStack(Items.NETHER_BRICK, random.nextInt(4 * gMeteor.size) + 1));
						}
						if (r < 10) {
							items.add(new ItemStack(Items.GLOWSTONE_DUST, random.nextInt(3 * gMeteor.size) + 1));
						}
					}
					break;
				case 3:
					if (r < 50) {
						items.add(new ItemStack(Items.GLOWSTONE_DUST, random.nextInt(5 * gMeteor.size) + 1));
					}
					if (r < 75) {
						int r2 = random.nextInt(3);
						if (r2 == 0) {
							items.add(new ItemStack(MeteorItems.itemMeteorChips.get(), random.nextInt(4) + 1));
						} else if (r2 == 1) {
							items.add(new ItemStack(MeteorItems.itemFrezaCrystal.get(), random.nextInt(4) + 1));
						} else {
							items.add(new ItemStack(MeteorItems.itemKreknoChip.get(), random.nextInt(4) + 1));
						}
					}
					if (r < 20) {
						ItemStack stack = CrashUnknown.getRandomLoot(random);
						if (!stack.isEmpty()) {
							items.add(stack);
						}
					}
					if (r < 5) {
						ItemStack stack = CrashUnknown.getRandomLoot(random);
						if (!stack.isEmpty()) {
							items.add(stack);
						}
					}
					break;
				case 4:
					items.add(new ItemStack(Items.COOKED_COD, random.nextInt(2 * gMeteor.size) + 1));
					if (r < 5) {
						items.add(new ItemStack(MeteorItems.COMET_KITTY_SPAWN_EGG.get(), 1));
					}
					break;
			}
			tShield.addMeteorMaterials(items);
		}
	}

	private double getDistance(int x1, int z1, int x2, int z2) {
		int x = x1 - x2;
		int z = z1 - z2;
		return Mth.sqrt(x * x + z * z);
	}

}
