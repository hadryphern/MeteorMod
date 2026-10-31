package net.meteor.common;

import java.util.function.Supplier;

import net.meteor.MeteorsMod;
import net.meteor.common.item.ItemBlockMeteorsMod;
import net.meteor.common.item.ItemBlockRendered;
import net.meteor.common.item.ItemBlockSlippery;
import net.meteor.common.item.ItemDetector;
import net.meteor.common.item.ItemEnchArmor;
import net.meteor.common.item.ItemEnchAxe;
import net.meteor.common.item.ItemEnchHoe;
import net.meteor.common.item.ItemEnchPickaxe;
import net.meteor.common.item.ItemEnchSpade;
import net.meteor.common.item.ItemEnchSword;
import net.meteor.common.item.ItemFrezariteAxe;
import net.meteor.common.item.ItemFrezariteHoe;
import net.meteor.common.item.ItemFrezaritePickaxe;
import net.meteor.common.item.ItemFrezariteSpade;
import net.meteor.common.item.ItemFrezariteSword;
import net.meteor.common.item.ItemKreknoChip;
import net.meteor.common.item.ItemKreknoSword;
import net.meteor.common.item.ItemMeteorsMod;
import net.meteor.common.item.ItemProtectionTorch;
import net.meteor.common.item.ItemSummoner;
import net.meteor.common.item.MeteorArmorMaterials;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MeteorItems {

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MeteorsMod.MOD_ID);

	// Same numbers as the 1.7.10 EnumHelper tool materials (harvest level, uses, speed, damage, enchantability)
	public static final Tier MeteoriteTool = new ForgeTier(3, 900, 10.0F, 2, 15, BlockTags.NEEDS_DIAMOND_TOOL, () -> Ingredient.of(MeteorItems.MeteoriteIngot.get()));
	public static final Tier FrezariteTool = new ForgeTier(2, 225, 7.0F, 2, 20, BlockTags.NEEDS_IRON_TOOL, () -> Ingredient.of(MeteorItems.FrozenIron.get()));

	public static final RegistryObject<Item> itemMeteorChips 			= ITEMS.register("meteor_chips", () -> new ItemMeteorsMod(new Item.Properties()));
	public static final RegistryObject<Item> itemRedMeteorGem 			= ITEMS.register("red_meteor_gem", () -> new ItemMeteorsMod(new Item.Properties()));
	public static final RegistryObject<Item> itemMeteorSummoner 		= ITEMS.register("meteor_summoner", () -> new ItemSummoner(-1));
	public static final RegistryObject<Item> itemMeteoriteSummoner 		= ITEMS.register("meteorite_meteor_summoner", () -> new ItemSummoner(0));
	public static final RegistryObject<Item> itemFrezariteSummoner 		= ITEMS.register("frezarite_meteor_summoner", () -> new ItemSummoner(1));
	public static final RegistryObject<Item> itemKreknoriteSummoner 	= ITEMS.register("kreknorite_meteor_summoner", () -> new ItemSummoner(2));
	public static final RegistryObject<Item> itemUnknownSummoner 		= ITEMS.register("unknown_meteor_summoner", () -> new ItemSummoner(3));
	public static final RegistryObject<Item> itemKittySummoner 			= ITEMS.register("kitty_comet_summoner", () -> new ItemSummoner(4));
	public static final RegistryObject<Item> itemFrezaCrystal 			= ITEMS.register("frezarite_crystal", () -> new ItemMeteorsMod(new Item.Properties()));
	public static final RegistryObject<Item> itemKreknoChip 			= ITEMS.register("kreknorite_chip", () -> new ItemKreknoChip(new Item.Properties()));
	public static final RegistryObject<Item> itemVanillaIceCream 		= ITEMS.register("vanilla_ice_cream", () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.6F).build())));
	public static final RegistryObject<Item> itemChocolateIceCream 		= ITEMS.register("chocolate_ice_cream", () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.6F).build())));
	public static final RegistryObject<Item> itemMeteorProximityDetector= ITEMS.register("meteor_proximity_detector", () -> new ItemDetector(0));
	public static final RegistryObject<Item> itemMeteorTimeDetector 	= ITEMS.register("meteor_time_detector", () -> new ItemDetector(1));
	public static final RegistryObject<Item> itemMeteorCrashDetector 	= ITEMS.register("meteor_crash_detector", () -> new ItemDetector(2));

	public static final RegistryObject<Item> MeteoriteHelmet 	= armor("meteorite_helmet", MeteorArmorMaterials.METEORITE, ArmorItem.Type.HELMET, () -> MeteorEnchantments.MAGNETIZATION.get(), 1);
	public static final RegistryObject<Item> MeteoriteBody 		= armor("meteorite_chestplate", MeteorArmorMaterials.METEORITE, ArmorItem.Type.CHESTPLATE, () -> MeteorEnchantments.MAGNETIZATION.get(), 1);
	public static final RegistryObject<Item> MeteoriteLegs 		= armor("meteorite_leggings", MeteorArmorMaterials.METEORITE, ArmorItem.Type.LEGGINGS, () -> MeteorEnchantments.MAGNETIZATION.get(), 1);
	public static final RegistryObject<Item> MeteoriteBoots 	= armor("meteorite_boots", MeteorArmorMaterials.METEORITE, ArmorItem.Type.BOOTS, () -> MeteorEnchantments.MAGNETIZATION.get(), 1);
	public static final RegistryObject<Item> MeteoriteAxe 		= ITEMS.register("meteorite_axe", () -> new ItemEnchAxe(MeteoriteTool, () -> MeteorEnchantments.MAGNETIZATION.get(), 1));
	public static final RegistryObject<Item> MeteoriteSpade 	= ITEMS.register("meteorite_spade", () -> new ItemEnchSpade(MeteoriteTool, () -> MeteorEnchantments.MAGNETIZATION.get(), 1));
	public static final RegistryObject<Item> MeteoriteSword 	= ITEMS.register("meteorite_sword", () -> new ItemEnchSword(MeteoriteTool, () -> MeteorEnchantments.MAGNETIZATION.get(), 1));
	public static final RegistryObject<Item> MeteoritePickaxe 	= ITEMS.register("meteorite_pickaxe", () -> new ItemEnchPickaxe(MeteoriteTool, () -> MeteorEnchantments.MAGNETIZATION.get(), 1));
	public static final RegistryObject<Item> MeteoriteHoe 		= ITEMS.register("meteorite_hoe", () -> new ItemEnchHoe(MeteoriteTool, () -> MeteorEnchantments.MAGNETIZATION.get(), 1));
	public static final RegistryObject<Item> FrezariteHelmet 	= armor("frezarite_helmet", MeteorArmorMaterials.FREZARITE, ArmorItem.Type.HELMET, () -> Enchantments.RESPIRATION, 3);
	public static final RegistryObject<Item> FrezariteBody 		= armor("frezarite_chestplate", MeteorArmorMaterials.FREZARITE, ArmorItem.Type.CHESTPLATE, () -> Enchantments.AQUA_AFFINITY, 1);
	public static final RegistryObject<Item> FrezariteLegs 		= armor("frezarite_leggings", MeteorArmorMaterials.FREZARITE, ArmorItem.Type.LEGGINGS, () -> MeteorEnchantments.COLD_TOUCH.get(), 1);
	public static final RegistryObject<Item> FrezariteBoots 	= armor("frezarite_boots", MeteorArmorMaterials.FREZARITE, ArmorItem.Type.BOOTS, () -> MeteorEnchantments.COLD_TOUCH.get(), 1);
	public static final RegistryObject<Item> FrezaritePickaxe 	= ITEMS.register("frezarite_pickaxe", () -> new ItemFrezaritePickaxe(FrezariteTool));
	public static final RegistryObject<Item> FrezariteSpade 	= ITEMS.register("frezarite_spade", () -> new ItemFrezariteSpade(FrezariteTool));
	public static final RegistryObject<Item> FrezariteSword 	= ITEMS.register("frezarite_sword", () -> new ItemFrezariteSword(FrezariteTool));
	public static final RegistryObject<Item> FrezariteAxe 		= ITEMS.register("frezarite_axe", () -> new ItemFrezariteAxe(FrezariteTool));
	public static final RegistryObject<Item> FrezariteHoe 		= ITEMS.register("frezarite_hoe", () -> new ItemFrezariteHoe(FrezariteTool));
	public static final RegistryObject<Item> KreknoriteHelmet 	= armor("kreknorite_helmet", MeteorArmorMaterials.KREKNORITE, ArmorItem.Type.HELMET, () -> Enchantments.FIRE_PROTECTION, 4);
	public static final RegistryObject<Item> KreknoriteBody 	= armor("kreknorite_chestplate", MeteorArmorMaterials.KREKNORITE, ArmorItem.Type.CHESTPLATE, () -> Enchantments.FIRE_PROTECTION, 4);
	public static final RegistryObject<Item> KreknoriteLegs 	= armor("kreknorite_leggings", MeteorArmorMaterials.KREKNORITE, ArmorItem.Type.LEGGINGS, () -> Enchantments.FIRE_PROTECTION, 4);
	public static final RegistryObject<Item> KreknoriteBoots 	= armor("kreknorite_boots", MeteorArmorMaterials.KREKNORITE, ArmorItem.Type.BOOTS, () -> Enchantments.FIRE_PROTECTION, 4);
	public static final RegistryObject<Item> KreknoriteSword 	= ITEMS.register("kreknorite_sword", () -> new ItemKreknoSword(MeteoriteTool));

	// Begin industrialization! :D
	public static final RegistryObject<Item> MeteoriteIngot		= ITEMS.register("meteorite_ingot", () -> new ItemMeteorsMod(new Item.Properties()));
	public static final RegistryObject<Item> FrozenIron			= ITEMS.register("frozen_iron", () -> new ItemMeteorsMod(new Item.Properties()));
	public static final RegistryObject<Item> KreknoriteIngot	= ITEMS.register("kreknorite_ingot", () -> new ItemMeteorsMod(new Item.Properties()));

	// Block items
	public static final RegistryObject<Item> METEOR_ORE 			= block(MeteorBlocks.METEOR_ORE);
	public static final RegistryObject<Item> METEOR 				= block(MeteorBlocks.METEOR);
	public static final RegistryObject<Item> RARE_METEOR 			= block(MeteorBlocks.RARE_METEOR);
	public static final RegistryObject<Item> METEOR_SHIELD 			= ITEMS.register("meteor_shield", () -> new ItemBlockRendered(MeteorBlocks.METEOR_SHIELD.get(), new Item.Properties()));
	public static final RegistryObject<Item> PROTECTED_LAND_TESTER 	= ITEMS.register("protected_land_tester", () -> new ItemProtectionTorch(MeteorBlocks.PROTECTED_LAND_TESTER.get(), MeteorBlocks.PROTECTED_LAND_TESTER_WALL.get(), new Item.Properties(), Direction.DOWN));
	public static final RegistryObject<Item> FREZARITE 				= block(MeteorBlocks.FREZARITE);
	public static final RegistryObject<Item> KREKNORITE 			= block(MeteorBlocks.KREKNORITE);
	public static final RegistryObject<Item> METEOR_TIMER 			= ITEMS.register("meteor_timer", () -> new ItemBlockRendered(MeteorBlocks.METEOR_TIMER.get(), new Item.Properties()));
	public static final RegistryObject<Item> RED_METEOR_GEM_BLOCK 	= block(MeteorBlocks.RED_METEOR_GEM_BLOCK);
	public static final RegistryObject<Item> METEORITE_BLOCK 		= block(MeteorBlocks.METEORITE_BLOCK);
	public static final RegistryObject<Item> FROZEN_IRON_BLOCK 		= block(MeteorBlocks.FROZEN_IRON_BLOCK);
	public static final RegistryObject<Item> KREKNORITE_BLOCK 		= block(MeteorBlocks.KREKNORITE_BLOCK);
	public static final RegistryObject<Item> FREEZER 				= block(MeteorBlocks.FREEZER);
	public static final RegistryObject<Item> SLIPPERY_BLOCK 		= slippery(MeteorBlocks.SLIPPERY_BLOCK);
	public static final RegistryObject<Item> SLIPPERY_BLOCK_TWO 	= slippery(MeteorBlocks.SLIPPERY_BLOCK_TWO);
	public static final RegistryObject<Item> SLIPPERY_BLOCK_THREE 	= slippery(MeteorBlocks.SLIPPERY_BLOCK_THREE);
	public static final RegistryObject<Item> SLIPPERY_BLOCK_FOUR 	= slippery(MeteorBlocks.SLIPPERY_BLOCK_FOUR);
	public static final RegistryObject<Item> SLIPPERY_STAIRS 		= slippery(MeteorBlocks.SLIPPERY_STAIRS);
	public static final RegistryObject<Item> SLIPPERY_STAIRS_TWO 	= slippery(MeteorBlocks.SLIPPERY_STAIRS_TWO);
	public static final RegistryObject<Item> SLIPPERY_STAIRS_THREE 	= slippery(MeteorBlocks.SLIPPERY_STAIRS_THREE);
	public static final RegistryObject<Item> SLIPPERY_STAIRS_FOUR 	= slippery(MeteorBlocks.SLIPPERY_STAIRS_FOUR);
	public static final RegistryObject<Item> FREZARITE_ORE 			= block(MeteorBlocks.FREZARITE_ORE);

	// Spawn eggs (replaces the 1.7.10 global entity id eggs)
	public static final RegistryObject<Item> ALIEN_CREEPER_SPAWN_EGG = ITEMS.register("alien_creeper_spawn_egg", () -> new ForgeSpawnEggItem(MeteorEntities.ALIEN_CREEPER, 7864485, 16732697, new Item.Properties()));
	public static final RegistryObject<Item> COMET_KITTY_SPAWN_EGG 	 = ITEMS.register("comet_kitty_spawn_egg", () -> new ForgeSpawnEggItem(MeteorEntities.COMET_KITTY, 2239283, 884535, new Item.Properties()));

	private static RegistryObject<Item> armor(String name, MeteorArmorMaterials mat, ArmorItem.Type type, Supplier<Enchantment> ench, int level) {
		return ITEMS.register(name, () -> new ItemEnchArmor(mat, type, ench, level));
	}

	private static RegistryObject<Item> block(RegistryObject<Block> block) {
		return ITEMS.register(block.getId().getPath(), () -> new ItemBlockMeteorsMod(block.get(), new Item.Properties()));
	}

	private static RegistryObject<Item> slippery(RegistryObject<Block> block) {
		return ITEMS.register(block.getId().getPath(), () -> new ItemBlockSlippery(block.get(), new Item.Properties()));
	}

}
