package net.meteor.client.effect;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

/**
 * Enchanting table style particles that fly into a charging Meteor Shield, using the mod's particle sheet.
 * Sprites 0 - 29 are the five rows of meteor coloured glyphs (6 each), 30 - 37 the extra glyphs of the first row.
 */
public class EntityMeteorShieldParticleFX extends TextureSheetParticle
{
	private final double field_70568_aq;
	private final double field_70567_ar;
	private final double field_70566_as;

	protected EntityMeteorShieldParticleFX(ClientLevel par1World, double par2, double par4, double par6, double par8, double par10, double par12, SpriteSet sprites)
	{
		super(par1World, par2, par4, par6);
		this.xd = par8;
		this.yd = par10;
		this.zd = par12;
		this.field_70568_aq = par2;
		this.field_70567_ar = par4;
		this.field_70566_as = par6;
		this.xo = par2 + par8;
		this.yo = par4 + par10;
		this.zo = par6 + par12;
		this.x = this.xo;
		this.y = this.yo;
		this.z = this.zo;
		this.quadSize = 0.1F * (this.random.nextFloat() * 0.5F + 0.2F);
		float f = this.random.nextFloat() * 0.6F + 0.4F;
		this.rCol = 0.9F * f;
		this.gCol = 0.9F * f;
		this.bCol = f;
		this.hasPhysics = false;
		this.lifetime = (int)(Math.random() * 10.0D) + 30;
		int index;
		if (this.random.nextInt(8) != 0)
			index = (int)(Math.random() * 6.0D) + (int)(Math.random() * 5.0D) * 6;
		else
			index = 30 + (int)(Math.random() * 8.0D);
		this.setSprite(sprites.get(index, 37));
	}

	@Override
	public void move(double x, double y, double z) {
		this.setBoundingBox(this.getBoundingBox().move(x, y, z));
		this.setLocationFromBoundingbox();
	}

	@Override
	public int getLightColor(float partialTick) {
		int i = super.getLightColor(partialTick);
		float f = (float)this.age / (float)this.lifetime;
		f *= f;
		f *= f;
		int j = i & 255;
		int k = i >> 16 & 255;
		k += (int)(f * 15.0F * 16.0F);
		if (k > 240) {
			k = 240;
		}
		return j | k << 16;
	}

	@Override
	public void tick()
	{
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		float f = (float)this.age / (float)this.lifetime;
		f = 1.0F - f;
		float f1 = 1.0F - f;
		f1 *= f1;
		f1 *= f1;
		this.x = this.field_70568_aq + this.xd * (double)f;
		this.y = this.field_70567_ar + this.yd * (double)f - (double)(f1 * 1.2F);
		this.z = this.field_70566_as + this.zd * (double)f;
		if (this.age++ >= this.lifetime)
		{
			this.remove();
		}
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	public static class Provider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet sprites;

		public Provider(SpriteSet sprites) {
			this.sprites = sprites;
		}

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel world, double x, double y, double z, double xd, double yd, double zd) {
			return new EntityMeteorShieldParticleFX(world, x, y, z, xd, yd, zd, sprites);
		}
	}
}
