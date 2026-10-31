package net.meteor.common.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;

/**
 * A charged, fireproof creeper that comes with meteors. Drops come from its loot table
 * (Red Meteor Gems or Meteor Chips). Creepers already avoid ocelots, and so Comet Kitties.
 */
public class EntityAlienCreeper extends Creeper
{
	public EntityAlienCreeper(EntityType<? extends Creeper> type, Level world)
	{
		super(type, world);
	}

	@Override
	public boolean isPowered()
	{
		return true;
	}
}
