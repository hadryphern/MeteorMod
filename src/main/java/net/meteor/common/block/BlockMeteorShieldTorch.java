package net.meteor.common.block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToIntFunction;

import net.meteor.common.IMeteorShield;
import net.meteor.common.MeteorParticles;
import net.meteor.common.climate.HandlerMeteor;
import net.meteor.common.climate.HandlerWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

/**
 * The Protected Land Tester. The idle and active torches used to be two blocks; it's the ACTIVE property now,
 * and like every 1.20.1 torch there is a separate wall variant ({@link BlockMeteorShieldWallTorch}).
 */
public class BlockMeteorShieldTorch extends TorchBlock
{
	public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

	public BlockMeteorShieldTorch(Properties props)
	{
		super(props, ParticleTypes.FLAME);
		this.registerDefaultState(this.stateDefinition.any().setValue(ACTIVE, true));
	}

	public static ToIntFunction<BlockState> activeLight(int light) {
		return state -> state.getValue(ACTIVE) ? light : 0;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(ACTIVE);
	}

	@Override
	public boolean isRandomlyTicking(BlockState state) {
		return true;
	}

	@Override
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random)
	{
		checkArea(world, pos, state);
	}

	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random)
	{
		checkArea(world, pos, state);
	}

	@Override
	public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean isMoving)
	{
		scheduleCheck(world, pos, state, oldState);
	}

	@Override
	public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random)
	{
		if (!state.getValue(ACTIVE)) {
			return;
		}
		double d = pos.getX() + 0.5F + (random.nextFloat() - 0.5F) * 0.2D;
		double d1 = pos.getY() + 0.7F + (random.nextFloat() - 0.5F) * 0.2D;
		double d2 = pos.getZ() + 0.5F + (random.nextFloat() - 0.5F) * 0.2D;
		world.addParticle(MeteorParticles.METEOR_DUST.get(), d, d1, d2, 0.0D, 0.0D, 0.0D);
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
	{
		return onActivated(state, world, pos, player);
	}

	// Shared with the wall torch

	static void scheduleCheck(Level world, BlockPos pos, BlockState state, BlockState oldState) {
		if (!world.isClientSide && !oldState.is(state.getBlock())) {
			// tickRate() was 2 in 1.7.10
			world.scheduleTick(pos, state.getBlock(), 2);
		}
	}

	static void checkArea(Level world, BlockPos pos, BlockState state) {
		HandlerMeteor metHandler = HandlerWorld.get(world);
		if (metHandler == null) return;
		boolean isSafeChunk = metHandler.getShieldManager().getClosestShieldInRange(pos.getX(), pos.getZ()) != null;
		if (state.getValue(ACTIVE) != isSafeChunk) {
			world.setBlock(pos, state.setValue(ACTIVE, isSafeChunk), 3);
		}
	}

	static InteractionResult onActivated(BlockState state, Level world, BlockPos pos, Player player) {
		if (!world.isClientSide) {
			checkArea(world, pos, state);
			HandlerMeteor meteorHandler = HandlerWorld.get(world);
			if (meteorHandler != null) {
				List<IMeteorShield> shields = meteorHandler.getShieldManager().getShieldsInRange(pos.getX(), pos.getZ());
				if (!shields.isEmpty()) {
					player.sendSystemMessage(Component.translatable("ProtectionTorch.landOwnership"));
					List<String> owners = new ArrayList<String>();
					for (IMeteorShield oPair : shields) {
						if (!owners.contains(oPair.getOwner())) {
							owners.add(oPair.getOwner());
						}
					}
					for (String owner : owners) {
						player.sendSystemMessage(Component.literal("    - " + owner));
					}
				}
			}
		}
		return InteractionResult.sidedSuccess(world.isClientSide);
	}

}
