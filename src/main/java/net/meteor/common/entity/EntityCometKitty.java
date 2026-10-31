package net.meteor.common.entity;

import net.meteor.common.MeteorEntities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.level.Level;

/**
 * A fireproof feline that falls from the sky. It was a tameable ocelot in 1.7.10; tameable ocelots are cats now.
 * Taming one grants the "Friendly Feline" advancement through the vanilla tame_animal trigger.
 */
public class EntityCometKitty extends Cat
{
	public EntityCometKitty(EntityType<? extends Cat> type, Level world)
	{
		super(type, world);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Cat.createAttributes().add(Attributes.MAX_HEALTH, 16.0D);
	}

	@Override
	public Cat getBreedOffspring(ServerLevel world, AgeableMob mate)
	{
		Cat var2;
		if (world.random.nextBoolean()) {
			var2 = EntityType.CAT.create(world);
		} else {
			var2 = MeteorEntities.COMET_KITTY.get().create(world);
		}

		if (var2 != null && this.isTame())
		{
			var2.setOwnerUUID(this.getOwnerUUID());
			var2.setTame(true);
			var2.setVariant(this.getVariant());
		}

		return var2;
	}

}
