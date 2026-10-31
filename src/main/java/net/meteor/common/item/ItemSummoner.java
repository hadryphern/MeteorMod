package net.meteor.common.item;

import net.meteor.common.climate.HandlerMeteor;
import net.meteor.common.entity.EntitySummoner;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Throw it to summon a meteor where it lands. The six damage values of 1.7.10 are separate items now;
 * meteorID is -1 for the random summoner, otherwise the EnumMeteor id.
 */
public class ItemSummoner extends ItemMeteorsMod
{
	private final int meteorID;

	public ItemSummoner(int meteorID) {
		super(new Properties().stacksTo(16));
		this.meteorID = meteorID;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entityplayer, InteractionHand hand)
	{
		ItemStack itemstack = entityplayer.getItemInHand(hand);
		if (!entityplayer.getAbilities().instabuild)
		{
			itemstack.shrink(1);
		}
		world.playSound(null, entityplayer.getX(), entityplayer.getY(), entityplayer.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
		if (!world.isClientSide)
		{
			boolean random = meteorID < 0;
			EntitySummoner summoner = new EntitySummoner(world, entityplayer, random ? HandlerMeteor.getMeteorType().getID() : meteorID, random);
			summoner.shootFromRotation(entityplayer, entityplayer.getXRot(), entityplayer.getYRot(), 0.0F, 1.5F, 1.0F);
			world.addFreshEntity(summoner);
		}
		return InteractionResultHolder.sidedSuccess(itemstack, world.isClientSide());
	}

}
