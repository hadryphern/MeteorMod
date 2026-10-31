package net.meteor.client.effect;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

/**
 * Small coloured dust like redstone particles: dark grey for meteors ("meteordust"),
 * cyan for frezarite ("frezadust", the old EntityFrezaDustFX).
 */
public class EntityMeteordustFX extends TextureSheetParticle
{
	private final float reddustParticleScale;
	private final SpriteSet sprites;

	protected EntityMeteordustFX(ClientLevel world, double d, double d1, double d2, float red, float green, float blue, SpriteSet sprites)
	{
		super(world, d, d1, d2, 0.0D, 0.0D, 0.0D);
		this.sprites = sprites;
		xd *= 0.10000000149011612D;
		yd *= 0.10000000149011612D;
		zd *= 0.10000000149011612D;
		rCol = red;
		gCol = green;
		bCol = blue;
		quadSize *= 0.75F;
		reddustParticleScale = quadSize;
		lifetime = (int)(8D / (Math.random() * 0.80000000000000004D + 0.20000000000000001D));
		hasPhysics = true;
		this.setSpriteFromAge(sprites);
	}

	@Override
	public float getQuadSize(float f)
	{
		float f6 = (((float)age + f) / (float)lifetime) * 32F;
		if(f6 < 0.0F)
		{
			f6 = 0.0F;
		}
		if(f6 > 1.0F)
		{
			f6 = 1.0F;
		}
		return reddustParticleScale * f6;
	}

	@Override
	public void tick()
	{
		xo = x;
		yo = y;
		zo = z;
		if(age++ >= lifetime)
		{
			remove();
			return;
		}
		// The sprite list goes from the biggest dot to the smallest, like the old texture index 7 - age * 8 / maxAge
		this.setSpriteFromAge(sprites);
		move(xd, yd, zd);
		if(y == yo)
		{
			xd *= 1.1000000000000001D;
			zd *= 1.1000000000000001D;
		}
		xd *= 0.95999997854232788D;
		yd *= 0.95999997854232788D;
		zd *= 0.95999997854232788D;
		if(onGround)
		{
			xd *= 0.69999998807907104D;
			zd *= 0.69999998807907104D;
		}
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	public static class MeteorDustProvider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet sprites;

		public MeteorDustProvider(SpriteSet sprites) {
			this.sprites = sprites;
		}

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel world, double x, double y, double z, double xd, double yd, double zd) {
			return new EntityMeteordustFX(world, x, y, z, 0.09F, 0.09F, 0.09F, sprites);
		}
	}

	public static class FrezaDustProvider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet sprites;

		public FrezaDustProvider(SpriteSet sprites) {
			this.sprites = sprites;
		}

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel world, double x, double y, double z, double xd, double yd, double zd) {
			return new EntityMeteordustFX(world, x, y, z, 0.2F, 1.0F, 0.8F, sprites);
		}
	}
}
