package net.meteor.common;

import java.util.List;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Port of the old ModConfig. Every value lives in the per-world SERVER config, which Forge
 * syncs to clients automatically (this replaces PacketSettings).
 */
public class MeteorConfig {

	public static final ForgeConfigSpec SPEC;

	public static final ForgeConfigSpec.IntValue meteorFallDistance;
	public static final ForgeConfigSpec.IntValue kittyAttackChance;
	public static final ForgeConfigSpec.BooleanValue textNotifyCrash;
	public static final ForgeConfigSpec.BooleanValue meteoriteEnabled;
	public static final ForgeConfigSpec.BooleanValue frezariteEnabled;
	public static final ForgeConfigSpec.BooleanValue kreknoriteEnabled;
	public static final ForgeConfigSpec.BooleanValue unknownEnabled;
	public static final ForgeConfigSpec.BooleanValue meteorShieldSound;
	public static final ForgeConfigSpec.IntValue cometFallChance;
	public static final ForgeConfigSpec.ConfigValue<List<? extends String>> whitelistedDimensions;
	public static final ForgeConfigSpec.IntValue meteorFallDeterrence;
	public static final ForgeConfigSpec.BooleanValue meteorsFallOnlyAtNight;
	public static final ForgeConfigSpec.BooleanValue allowSummonedMeteorGrief;
	public static final ForgeConfigSpec.IntValue shieldRadiusMultiplier;
	public static final ForgeConfigSpec.IntValue minMeteorSize;
	public static final ForgeConfigSpec.IntValue maxMeteorSize;
	public static final ForgeConfigSpec.IntValue minMeteorSizeForPortal;
	public static final ForgeConfigSpec.DoubleValue impactExplosionMultiplier;
	public static final ForgeConfigSpec.IntValue impactSpread;
	public static final ForgeConfigSpec.BooleanValue slipperyBlocksEnabled;

	static {
		ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
		b.push("general");
		meteorFallDistance		= b.comment("When determining where a meteor falls, it chooses within this radius (blocks) of a random player.").defineInRange("meteorFallRadius", 350, 4, 10000);
		kittyAttackChance		= b.comment("Ranges from 0 to 100").defineInRange("kittyAttackChance", 1, 0, 100);
		textNotifyCrash			= b.comment("Pops up a chat message when a meteor falls.").define("textCrashNotification", false);
		meteoriteEnabled		= b.comment("Allow Meteorite Meteors to Fall?").define("meteoriteMeteorEnabled", true);
		frezariteEnabled		= b.comment("Allow Frezarite Meteors to Fall?").define("frezariteMeteorEnabled", true);
		kreknoriteEnabled		= b.comment("Allow Kreknorite Meteors to Fall?").define("kreknoriteMeteorEnabled", true);
		unknownEnabled			= b.comment("Allow Unknown Meteors to Fall?").define("unknownMeteorEnabled", true);
		meteorShieldSound 		= b.comment("Allows a humming sound for the Meteor Shield too happen on occasion.").define("meteorShieldHummingNoiseEnabled", true);
		cometFallChance			= b.comment("Ranges from 0 to 100").defineInRange("cometFallChance", 20, 0, 100);
		whitelistedDimensions 	= b.comment("Dimensions that meteors are allowed to naturally fall in.").defineListAllowEmpty("whitelistedDimensions",
				List.of("minecraft:overworld", "minecraft:the_end", "twilightforest:twilight_forest"), o -> o instanceof String);
		meteorFallDeterrence	= b.comment("The higher this is, the longer it takes for meteors to appear and fall. For more info, refer here: http://fallingmeteorsmod.wikia.com/wiki/Falling_Meteors#Configuration").defineInRange("meteorFallDeterrence", 25, 1, 100000);
		meteorsFallOnlyAtNight	= b.comment("The amount of time left until a meteor falls will only tick down at night.").define("meteorsOnlyFallAtNight", true);
		allowSummonedMeteorGrief = b.comment("If false, players can't summon meteors on land protected by other players with a Meteor Shield").define("allowSummonedMeteorGrief", false);
		shieldRadiusMultiplier	= b.comment("The range of the meteor shield is determined by this times the shield's power level.").defineInRange("shieldRadiusInBlocks", 64, 0, 10000);
		minMeteorSize			= b.comment("Minimum Size of a falling meteor. Ranges from 1 to 3.").defineInRange("minimumMeteorSize", 1, 1, 3);
		maxMeteorSize			= b.comment("Maximum Size of a falling meteor. Ranges from 1 to 3.").defineInRange("maximumMeteorSize", 3, 1, 3);
		minMeteorSizeForPortal	= b.comment("When a Kreknorite Meteor falls, if the meteor is this size or bigger, it will generate a nether portal.").defineInRange("minimumMeteorSizeToSpawnNetherPortal", 2, 1, 4);
		impactExplosionMultiplier = b.comment("This times the meteor's size is how big the explosion will be.").defineInRange("meteorImpactExplosionMultiplier", 5.0D, 0.0D, 20.0D);
		impactSpread			= b.comment("This times the meteor size determines how big of an impact the meteor's crater will have to spread ore.").defineInRange("meteorImpactSpread", 4, 0, 64);
		slipperyBlocksEnabled	= b.comment("Setting to false will disallow the creation of Slippery Blocks with the Freezer.").define("slipperyBlocksEnabled", true);
		b.pop();
		SPEC = b.build();
	}

	// ------------------------------------------------------------------------
	// Derived values, matching the math of the original MeteorsMod class
	// ------------------------------------------------------------------------

	private static int configTicks() {
		return meteorFallDeterrence.get() * 100;
	}

	public static int minTicksUntilMeteorSpawn() {
		int mSpawn = (int)(configTicks() * 0.25D);
		return (int)(mSpawn * 0.25D);
	}

	public static int randTicksUntilMeteorSpawn() {
		int mSpawn = (int)(configTicks() * 0.25D);
		return (int)(mSpawn * 0.75D);
	}

	public static int minTicksUntilMeteorCrashes() {
		int mCrash = (int)(configTicks() * 0.75D);
		return (int)(mCrash * 0.5D);
	}

	public static int randTicksUntilMeteorCrashes() {
		int mCrash = (int)(configTicks() * 0.75D);
		return (int)(mCrash * 0.5D);
	}

	public static int getMinMeteorSize() {
		int min = Mth.clamp(minMeteorSize.get(), 1, 3);
		int max = Mth.clamp(maxMeteorSize.get(), 1, 3);
		return Math.min(min, max);
	}

	public static int getMaxMeteorSize() {
		return Mth.clamp(maxMeteorSize.get(), 1, 3);
	}

	public static int getMinMeteorSizeForPortal() {
		return Math.max(minMeteorSizeForPortal.get(), getMinMeteorSize());
	}

	public static boolean isDimensionWhitelisted(ResourceKey<Level> dim) {
		String name = dim.location().toString();
		for (String s : whitelistedDimensions.get()) {
			if (s.equals(name)) {
				return true;
			}
		}
		return false;
	}

	public static EnumMeteor getDefaultType() {
		if (meteoriteEnabled.get()) return EnumMeteor.METEORITE;
		if (frezariteEnabled.get()) return EnumMeteor.FREZARITE;
		if (kreknoriteEnabled.get()) return EnumMeteor.KREKNORITE;
		if (unknownEnabled.get()) return EnumMeteor.UNKNOWN;
		return EnumMeteor.METEORITE;
	}

	/** Values read on the client, where the config may not be loaded yet (e.g. tooltips in menus). */
	public static <T> T safeGet(ForgeConfigSpec.ConfigValue<T> value, T def) {
		try {
			return SPEC.isLoaded() ? value.get() : def;
		} catch (IllegalStateException e) {
			return def;
		}
	}

}
