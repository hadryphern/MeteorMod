package net.meteor.common;

import net.meteor.MeteorsMod;
import net.meteor.common.item.ItemBlockSlippery;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/** Port of CreativeTabMeteor. */
public class MeteorTabs {

	public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MeteorsMod.MOD_ID);

	public static final RegistryObject<CreativeModeTab> METEOR_TAB = TABS.register("meteors", () -> CreativeModeTab.builder()
			.title(Component.translatable("itemGroup.meteors"))
			.icon(() -> new ItemStack(MeteorItems.METEOR.get()))
			.displayItems((params, out) -> {
				// Same order as the 1.7.10 block registration, followed by the items
				out.accept(MeteorItems.METEOR_ORE.get());
				out.accept(MeteorItems.METEOR.get());
				out.accept(MeteorItems.METEOR_SHIELD.get());
				out.accept(MeteorItems.PROTECTED_LAND_TESTER.get());
				out.accept(MeteorItems.FREZARITE.get());
				out.accept(MeteorItems.KREKNORITE.get());
				out.accept(MeteorItems.METEOR_TIMER.get());
				out.accept(MeteorItems.RED_METEOR_GEM_BLOCK.get());
				out.accept(MeteorItems.METEORITE_BLOCK.get());
				out.accept(MeteorItems.FROZEN_IRON_BLOCK.get());
				out.accept(MeteorItems.KREKNORITE_BLOCK.get());
				out.accept(MeteorItems.FREEZER.get());
				out.accept(ItemBlockSlippery.withFacade(MeteorItems.SLIPPERY_BLOCK.get(), Blocks.STONE));
				out.accept(ItemBlockSlippery.withFacade(MeteorItems.SLIPPERY_BLOCK_TWO.get(), Blocks.STONE));
				out.accept(ItemBlockSlippery.withFacade(MeteorItems.SLIPPERY_BLOCK_THREE.get(), Blocks.STONE));
				out.accept(ItemBlockSlippery.withFacade(MeteorItems.SLIPPERY_BLOCK_FOUR.get(), Blocks.STONE));
				out.accept(ItemBlockSlippery.withFacade(MeteorItems.SLIPPERY_STAIRS.get(), Blocks.OAK_STAIRS));
				out.accept(ItemBlockSlippery.withFacade(MeteorItems.SLIPPERY_STAIRS_TWO.get(), Blocks.OAK_STAIRS));
				out.accept(ItemBlockSlippery.withFacade(MeteorItems.SLIPPERY_STAIRS_THREE.get(), Blocks.OAK_STAIRS));
				out.accept(ItemBlockSlippery.withFacade(MeteorItems.SLIPPERY_STAIRS_FOUR.get(), Blocks.OAK_STAIRS));
				out.accept(MeteorItems.FREZARITE_ORE.get());

				out.accept(MeteorItems.itemMeteorChips.get());
				out.accept(MeteorItems.itemRedMeteorGem.get());
				out.accept(MeteorItems.itemMeteorSummoner.get());
				out.accept(MeteorItems.itemMeteoriteSummoner.get());
				out.accept(MeteorItems.itemFrezariteSummoner.get());
				out.accept(MeteorItems.itemKreknoriteSummoner.get());
				out.accept(MeteorItems.itemUnknownSummoner.get());
				out.accept(MeteorItems.itemKittySummoner.get());
				out.accept(MeteorItems.itemFrezaCrystal.get());
				out.accept(MeteorItems.itemKreknoChip.get());
				out.accept(MeteorItems.itemVanillaIceCream.get());
				out.accept(MeteorItems.itemChocolateIceCream.get());
				out.accept(MeteorItems.itemMeteorCrashDetector.get());
				out.accept(MeteorItems.itemMeteorProximityDetector.get());
				out.accept(MeteorItems.itemMeteorTimeDetector.get());
				out.accept(MeteorItems.FrezariteHelmet.get().getDefaultInstance());
				out.accept(MeteorItems.FrezariteBody.get().getDefaultInstance());
				out.accept(MeteorItems.FrezariteLegs.get().getDefaultInstance());
				out.accept(MeteorItems.FrezariteBoots.get().getDefaultInstance());
				out.accept(MeteorItems.FrezaritePickaxe.get());
				out.accept(MeteorItems.FrezariteSpade.get());
				out.accept(MeteorItems.KreknoriteHelmet.get().getDefaultInstance());
				out.accept(MeteorItems.KreknoriteBody.get().getDefaultInstance());
				out.accept(MeteorItems.KreknoriteLegs.get().getDefaultInstance());
				out.accept(MeteorItems.KreknoriteBoots.get().getDefaultInstance());
				out.accept(MeteorItems.KreknoriteSword.get().getDefaultInstance());
				out.accept(MeteorItems.MeteoriteAxe.get().getDefaultInstance());
				out.accept(MeteorItems.MeteoriteBody.get().getDefaultInstance());
				out.accept(MeteorItems.MeteoriteBoots.get().getDefaultInstance());
				out.accept(MeteorItems.MeteoriteHelmet.get().getDefaultInstance());
				out.accept(MeteorItems.MeteoriteHoe.get().getDefaultInstance());
				out.accept(MeteorItems.MeteoriteLegs.get().getDefaultInstance());
				out.accept(MeteorItems.MeteoritePickaxe.get().getDefaultInstance());
				out.accept(MeteorItems.MeteoriteSpade.get().getDefaultInstance());
				out.accept(MeteorItems.MeteoriteSword.get().getDefaultInstance());
				out.accept(MeteorItems.FrezariteSword.get());
				out.accept(MeteorItems.FrezariteAxe.get());
				out.accept(MeteorItems.FrezariteHoe.get());
				out.accept(MeteorItems.MeteoriteIngot.get());
				out.accept(MeteorItems.FrozenIron.get());
				out.accept(MeteorItems.KreknoriteIngot.get());
				out.accept(MeteorItems.ALIEN_CREEPER_SPAWN_EGG.get());
				out.accept(MeteorItems.COMET_KITTY_SPAWN_EGG.get());
				out.accept(maxBook(MeteorEnchantments.COLD_TOUCH.get()));
				out.accept(maxBook(MeteorEnchantments.MAGNETIZATION.get()));
			})
			.build());

	private static ItemStack maxBook(Enchantment ench) {
		return EnchantedBookItem.createForEnchantment(new EnchantmentInstance(ench, ench.getMaxLevel()));
	}

}
