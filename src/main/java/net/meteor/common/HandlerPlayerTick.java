package net.meteor.common;

import java.util.List;

import net.meteor.MeteorsMod;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Armor and enchantment effects: Kreknorite fire immunity, Cold Touch and Magnetization. */
@Mod.EventBusSubscriber(modid = MeteorsMod.MOD_ID)
public class HandlerPlayerTick
{
	// Runs on both phases of the tick like the 1.7.10 handler did
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		Player player = event.player;
		Level world = player.level();

		if (isWearingFullKreknorite(player) && player.getRemainingFireTicks() > 0) {
			// 1.7.10 set Entity.isImmuneToFire through reflection; fire damage is cancelled in onLivingAttack
			player.clearFire();
		}

		int totalLevel = EnchantmentHelper.getItemEnchantmentLevel(MeteorEnchantments.COLD_TOUCH.get(), player.getItemBySlot(EquipmentSlot.LEGS))
				+ EnchantmentHelper.getItemEnchantmentLevel(MeteorEnchantments.COLD_TOUCH.get(), player.getItemBySlot(EquipmentSlot.FEET));
		if (totalLevel > 0 && !player.isInWater() && !world.isClientSide)
		{
			int j1 = Mth.floor(player.getY() - 1.0D);
			if (totalLevel > 1 && player.isSprinting())
			{
				int l = Mth.floor(player.getX());
				int k1 = Mth.floor(player.getZ());
				for (int x = l - 2; x < l + 2; x++) {
					for (int z = k1 - 2; z < k1 + 2; z++) {
						freezeWater(world, new BlockPos(x, j1, z));
					}
				}
			}
			else
			{
				for (int j = 0; j < 4; j++) {
					int l = Mth.floor(player.getX() + (j % 2 * 2 - 1) * 0.25F);
					int k1 = Mth.floor(player.getZ() + (j / 2 % 2 * 2 - 1) * 0.25F);
					freezeWater(world, new BlockPos(l, j1, k1));
				}
			}
		}

		int enchLevel = getMagnetizationLevel(player);
		if (enchLevel > 0) {
			double distance = 8 * enchLevel;
			List<ItemEntity> entities = world.getEntitiesOfClass(ItemEntity.class, new AABB(player.getX() - distance, player.getY() - distance, player.getZ() - distance, player.getX() + distance, player.getY() + distance, player.getZ() + distance));
			for (ItemEntity item : entities) {
				updateEntityItem(item, player, distance);
			}
		}
	}

	@SubscribeEvent
	public static void onLivingAttack(LivingAttackEvent event) {
		if (event.getEntity() instanceof Player player && event.getSource().is(DamageTypeTags.IS_FIRE) && isWearingFullKreknorite(player)) {
			event.setCanceled(true);
		}
	}

	private static void freezeWater(Level world, BlockPos pos) {
		if (world.getBlockState(pos).is(Blocks.WATER)) {
			world.setBlock(pos, Blocks.ICE.defaultBlockState(), 2);
		}
	}

	public static boolean isWearingFullKreknorite(Player player) {
		return isWearing(MeteorItems.KreknoriteHelmet.get(), player.getItemBySlot(EquipmentSlot.HEAD)) &&
				isWearing(MeteorItems.KreknoriteBody.get(), player.getItemBySlot(EquipmentSlot.CHEST)) &&
				isWearing(MeteorItems.KreknoriteLegs.get(), player.getItemBySlot(EquipmentSlot.LEGS)) &&
				isWearing(MeteorItems.KreknoriteBoots.get(), player.getItemBySlot(EquipmentSlot.FEET));
	}

	private static boolean isWearing(Item item, ItemStack itemStack) {
		return !itemStack.isEmpty() && itemStack.is(item);
	}

	/** Highest Magnetization level on the armor or held items. */
	public static int getMagnetizationLevel(Player player) {
		return EnchantmentHelper.getEnchantmentLevel(MeteorEnchantments.MAGNETIZATION.get(), player);
	}

	private static void updateEntityItem(ItemEntity en, Player player, double closeness) {
		double var3 = (player.getX() - en.getX()) / closeness;
		double var5 = (player.getY() + player.getEyeHeight() - en.getY()) / closeness;
		double var7 = (player.getZ() - en.getZ()) / closeness;
		double var9 = Math.sqrt(var3 * var3 + var5 * var5 + var7 * var7);
		double var11 = 1.0D - var9;
		if (var11 > 0.0D)
		{
			var11 *= var11;
			en.setDeltaMovement(en.getDeltaMovement().add(new Vec3(var3 / var9 * var11 * 0.1D, var5 / var9 * var11 * 0.1D, var7 / var9 * var11 * 0.1D)));
		}
	}

}
