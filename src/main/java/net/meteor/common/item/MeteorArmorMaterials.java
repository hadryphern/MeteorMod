package net.meteor.common.item;

import java.util.function.Supplier;

import net.meteor.MeteorsMod;
import net.meteor.common.MeteorItems;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

/** The 1.7.10 EnumHelper armor materials: durability factor, reduction amounts {helmet, chest, legs, boots} and enchantability. */
public enum MeteorArmorMaterials implements ArmorMaterial {

	METEORITE("meteorite", 36, new int[] { 2, 7, 5, 2 }, 15, () -> Ingredient.of(MeteorItems.MeteoriteIngot.get())),
	FREZARITE("frezarite", 7, new int[] { 2, 5, 3, 1 }, 20, () -> Ingredient.of(MeteorItems.FrozenIron.get())),
	KREKNORITE("kreknorite", 40, new int[] { 3, 8, 6, 3 }, 10, () -> Ingredient.of(MeteorItems.KreknoriteIngot.get()));

	// Same per-slot base durability as 1.7.10's maxDamageArray {11, 16, 15, 13}
	private static final int[] HEALTH_PER_SLOT = new int[] { 11, 16, 15, 13 };

	private final String name;
	private final int durabilityFactor;
	private final int[] reductionAmounts;
	private final int enchantability;
	private final Supplier<Ingredient> repair;

	private MeteorArmorMaterials(String name, int durabilityFactor, int[] reductionAmounts, int enchantability, Supplier<Ingredient> repair) {
		this.name = name;
		this.durabilityFactor = durabilityFactor;
		this.reductionAmounts = reductionAmounts;
		this.enchantability = enchantability;
		this.repair = repair;
	}

	private static int slot(ArmorItem.Type type) {
		switch (type) {
		case HELMET: return 0;
		case CHESTPLATE: return 1;
		case LEGGINGS: return 2;
		default: return 3;
		}
	}

	@Override
	public int getDurabilityForType(ArmorItem.Type type) {
		return HEALTH_PER_SLOT[slot(type)] * this.durabilityFactor;
	}

	@Override
	public int getDefenseForType(ArmorItem.Type type) {
		return this.reductionAmounts[slot(type)];
	}

	@Override
	public int getEnchantmentValue() {
		return this.enchantability;
	}

	@Override
	public SoundEvent getEquipSound() {
		return SoundEvents.ARMOR_EQUIP_GENERIC;
	}

	@Override
	public Ingredient getRepairIngredient() {
		return this.repair.get();
	}

	/** Textures are at assets/meteors/textures/models/armor/[name]_layer_1.png and _layer_2.png */
	@Override
	public String getName() {
		return MeteorsMod.MOD_ID + ":" + this.name;
	}

	@Override
	public float getToughness() {
		return 0.0F;
	}

	@Override
	public float getKnockbackResistance() {
		return 0.0F;
	}

}
