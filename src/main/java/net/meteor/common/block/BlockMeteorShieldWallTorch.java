package net.meteor.common.block;

import net.meteor.common.MeteorParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;

/** Wall-mounted Protected Land Tester (the old torch metadata 1 - 4). */
public class BlockMeteorShieldWallTorch extends WallTorchBlock
{
	public BlockMeteorShieldWallTorch(Properties props)
	{
		super(props, ParticleTypes.FLAME);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(BlockMeteorShieldTorch.ACTIVE, true));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(BlockMeteorShieldTorch.ACTIVE);
	}

	@Override
	public String getDescriptionId() {
		return this.asItem().getDescriptionId();
	}

	@Override
	public boolean isRandomlyTicking(BlockState state) {
		return true;
	}

	@Override
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random)
	{
		BlockMeteorShieldTorch.checkArea(world, pos, state);
	}

	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random)
	{
		BlockMeteorShieldTorch.checkArea(world, pos, state);
	}

	@Override
	public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean isMoving)
	{
		BlockMeteorShieldTorch.scheduleCheck(world, pos, state, oldState);
	}

	@Override
	public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random)
	{
		if (!state.getValue(BlockMeteorShieldTorch.ACTIVE)) {
			return;
		}
		Direction opposite = state.getValue(FACING).getOpposite();
		double d = pos.getX() + 0.5F + (random.nextFloat() - 0.5F) * 0.2D;
		double d1 = pos.getY() + 0.7F + (random.nextFloat() - 0.5F) * 0.2D;
		double d2 = pos.getZ() + 0.5F + (random.nextFloat() - 0.5F) * 0.2D;
		double d3 = 0.219999998807907D;
		double d4 = 0.2700000107288361D;
		world.addParticle(MeteorParticles.METEOR_DUST.get(), d + d4 * opposite.getStepX(), d1 + d3, d2 + d4 * opposite.getStepZ(), 0.0D, 0.0D, 0.0D);
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
	{
		return BlockMeteorShieldTorch.onActivated(state, world, pos, player);
	}

}
