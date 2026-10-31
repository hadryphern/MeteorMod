package net.meteor.common.block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToIntFunction;

import net.meteor.common.MeteorEntities;
import net.meteor.common.MeteorItems;
import net.meteor.common.MeteorParticles;
import net.meteor.common.entity.EntityAlienCreeper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ForgeHooks;

/**
 * A fallen meteor. The old metadata (0 - 4) is now the HEAT property: 0 is a cooled meteor, anything above is hot.
 */
public class BlockMeteor extends Block
{
	public static final IntegerProperty HEAT = IntegerProperty.create("heat", 0, 4);

	// The 1.7.10 collision box was contracted by 0.006 so that entities touching a hot meteor catch fire.
	private static final VoxelShape COLLISION = Shapes.box(0.006D, 0.006D, 0.006D, 0.994D, 0.994D, 0.994D);

	public BlockMeteor(Properties props)
	{
		super(props.isRedstoneConductor(BlockMeteor::always).isSuffocating(BlockMeteor::always).isViewBlocking(BlockMeteor::always));
		this.registerDefaultState(this.stateDefinition.any().setValue(HEAT, 0));
	}

	private static boolean always(BlockState state, BlockGetter level, BlockPos pos) {
		return true;
	}

	public static ToIntFunction<BlockState> heatLight(int light) {
		return state -> state.getValue(HEAT) > 0 ? light : 0;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(HEAT);
	}

	protected Item getItemDropped()
	{
		return MeteorItems.itemMeteorChips.get();
	}

	protected int quantityDropped(int meta, int fortune, RandomSource random)
	{
		if ((meta == 0) && (random.nextInt(4) == 0)) {
			return 1 + random.nextInt(fortune + 1);
		}
		return random.nextInt(1 + meta + random.nextInt(fortune + 1));
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
		return computeDrops(this, state, builder, (meta, fortune, random) -> {
			List<ItemStack> drops = new ArrayList<ItemStack>();
			int count = quantityDropped(meta, fortune, random);
			if (count > 0) {
				drops.add(new ItemStack(getItemDropped(), count));
			}
			return drops;
		});
	}

	public interface DropFunction {
		List<ItemStack> getDrops(int meta, int fortune, RandomSource random);
	}

	/**
	 * Recreates the 1.7.10 drop rules in code since the quantity formulas depend on metadata: silk touch gives the
	 * cooled block, explosions drop each stack with a chance of 1 / explosion size.
	 */
	public static List<ItemStack> computeDrops(Block block, BlockState state, LootParams.Builder builder, DropFunction function) {
		ItemStack tool = builder.getOptionalParameter(LootContextParams.TOOL);
		RandomSource random = builder.getLevel().getRandom();
		if (tool != null && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, tool) > 0) {
			List<ItemStack> list = new ArrayList<ItemStack>();
			list.add(new ItemStack(block));
			return list;
		}
		int fortune = tool != null ? EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, tool) : 0;
		List<ItemStack> drops = function.getDrops(state.hasProperty(HEAT) ? state.getValue(HEAT) : 0, fortune, random);
		Float explosionRadius = builder.getOptionalParameter(LootContextParams.EXPLOSION_RADIUS);
		if (explosionRadius != null && explosionRadius > 0.0F) {
			float chance = 1.0F / explosionRadius;
			drops.removeIf(stack -> random.nextFloat() > chance);
		}
		return drops;
	}

	@Override
	public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random)
	{
		if (state.getValue(HEAT) > 0) {
			if (random.nextInt(32) == 0)
			{
				world.playLocalSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 1.0F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);
			}
			renderGlowParticles(world, pos, random, MeteorParticles.METEOR_DUST.get());
		}
	}

	public static void renderGlowParticles(Level world, BlockPos pos, RandomSource random, ParticleOptions particle)
	{
		int i = pos.getX();
		int j = pos.getY();
		int k = pos.getZ();
		double d = 0.0625D;
		for (int l = 0; l < 6; l++)
		{
			double d1 = i + random.nextFloat();
			double d2 = j + random.nextFloat();
			double d3 = k + random.nextFloat();
			if ((l == 0) && !world.getBlockState(pos.above()).isSolidRender(world, pos.above()))
			{
				d2 = j + 1 + d;
			}
			if ((l == 1) && !world.getBlockState(pos.below()).isSolidRender(world, pos.below()))
			{
				d2 = j + 0 - d;
			}
			if ((l == 2) && !world.getBlockState(pos.south()).isSolidRender(world, pos.south()))
			{
				d3 = k + 1 + d;
			}
			if ((l == 3) && !world.getBlockState(pos.north()).isSolidRender(world, pos.north()))
			{
				d3 = k + 0 - d;
			}
			if ((l == 4) && !world.getBlockState(pos.east()).isSolidRender(world, pos.east()))
			{
				d1 = i + 1 + d;
			}
			if ((l == 5) && !world.getBlockState(pos.west()).isSolidRender(world, pos.west()))
			{
				d1 = i + 0 - d;
			}
			if ((d1 < i) || (d1 > i + 1) || (d2 < 0.0D) || (d2 > j + 1) || (d3 < k) || (d3 > k + 1))
			{
				world.addParticle(particle, d1, d2, d3, 0.0D, 0.0D, 0.0D);
			}
		}
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context)
	{
		return COLLISION;
	}

	@Override
	public VoxelShape getBlockSupportShape(BlockState state, BlockGetter world, BlockPos pos) {
		return Shapes.block();
	}

	@Override
	public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
		// 1.7.10 isBurning(): touching a hot meteor works like touching fire
		if (state.getValue(HEAT) > 0) {
			if (!entity.fireImmune()) {
				entity.setRemainingFireTicks(entity.getRemainingFireTicks() + 1);
				if (entity.getRemainingFireTicks() == 0) {
					entity.setSecondsOnFire(8);
				}
			}
			entity.hurt(world.damageSources().inFire(), 1.0F);
		}
	}

	@Override
	public boolean isFireSource(BlockState state, LevelReader world, BlockPos pos, Direction side) {
		if ((side == Direction.UP) && (state.getValue(HEAT) > 0)) {
			return true;
		}
		return super.isFireSource(state, world, pos, side);
	}

	@Override
	public boolean isRandomlyTicking(BlockState state) {
		return state.getValue(HEAT) > 0;
	}

	@Override
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random)
	{
		int meta = state.getValue(HEAT);
		if (meta > 0) {
			if (isWaterAround(world, pos)) {
				world.setBlock(pos, state.setValue(HEAT, 0), 2);
				triggerLavaMixEffects(world, pos);
			} else {
				world.setBlock(pos, state.setValue(HEAT, --meta), 2);
				if (meta == 0) {
					triggerLavaMixEffects(world, pos);
				}
			}
		}
	}

	/** Fizz sound and large smoke, the same effect as lava touching water. */
	protected void triggerLavaMixEffects(LevelAccessor world, BlockPos pos)
	{
		world.levelEvent(1501, pos, 0);
	}

	private boolean isWaterAround(Level world, BlockPos pos) {
		for (BlockPos p : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
			if (world.getBlockState(p).is(Blocks.WATER)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canHarvestBlock(BlockState state, BlockGetter world, BlockPos pos, Player player) {
		// Cooled meteors only need a stone pickaxe (the block tags), hot ones need iron like 1.7.10's harvest level 2
		if (!ForgeHooks.isCorrectToolForDrops(state, player)) {
			return false;
		}
		return state.getValue(HEAT) == 0 || player.getMainHandItem().isCorrectToolForDrops(Blocks.DIAMOND_ORE.defaultBlockState());
	}

	@Override
	public void destroy(LevelAccessor world, BlockPos pos, BlockState state)
	{
		super.destroy(world, pos, state);
		if ((world instanceof ServerLevel level) && (level.random.nextInt(100) == 95)) {
			EntityAlienCreeper creeper = MeteorEntities.ALIEN_CREEPER.get().create(level);
			if (creeper != null) {
				creeper.moveTo(pos.getX(), pos.getY(), pos.getZ(), 0.0F, 0.0F);
				level.addFreshEntity(creeper);
				creeper.spawnAnim();
			}
		}
	}

	@Override
	public int getExpDrop(BlockState state, LevelReader world, RandomSource random, BlockPos pos, int fortune, int silkTouch) {
		return silkTouch > 0 ? 0 : Mth.nextInt(random, 2, 5);
	}

}
