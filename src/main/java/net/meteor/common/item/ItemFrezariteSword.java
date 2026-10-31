package net.meteor.common.item;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class ItemFrezariteSword extends SwordItem
{
	public ItemFrezariteSword(Tier toolMaterial)
	{
		super(toolMaterial, 3, -2.4F, new Properties());
	}

	@Override
	public boolean hurtEnemy(ItemStack par1ItemStack, LivingEntity par2EntityLiving, LivingEntity par3EntityLiving)
	{
		par2EntityLiving.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 5));
		return super.hurtEnemy(par1ItemStack, par2EntityLiving, par3EntityLiving);
	}

	@Override
	public void appendHoverText(ItemStack par1ItemStack, Level world, List<Component> par3List, TooltipFlag flag)
	{
		ItemFrezaritePickaxe.addFrezariteInfo(par3List, "enchantment.frezSword");
	}

	@Override
	public boolean isFoil(ItemStack par1ItemStack)
	{
		return true;
	}

	@Override
	public int getEnchantmentValue()
	{
		return 0;
	}
}
