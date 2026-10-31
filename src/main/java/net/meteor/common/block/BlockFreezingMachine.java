package net.meteor.common.block;

import net.meteor.common.MeteorBlockEntities;
import net.meteor.common.MeteorParticles;
import net.meteor.common.tileentity.TileEntityFreezingMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

/** The old metadata held the facing (2 - 5) plus 4 while freezing; FACING and LIT replace it. */
public class BlockFreezingMachine extends BaseEntityBlock {

	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty LIT = BlockStateProperties.LIT;

	public BlockFreezingMachine(Properties props) {
		super(props);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LIT, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, LIT);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileEntityFreezingMachine(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return world.isClientSide ? null : createTickerHelper(type, MeteorBlockEntities.FREEZER.get(), TileEntityFreezingMachine::tick);
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
	{
		if (!world.isClientSide && world.getBlockEntity(pos) instanceof TileEntityFreezingMachine freezer) {
			NetworkHooks.openScreen((ServerPlayer) player, freezer, pos);
		}
		return InteractionResult.sidedSuccess(world.isClientSide);
	}

	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			if (world.getBlockEntity(pos) instanceof TileEntityFreezingMachine freezer) {
				Containers.dropContents(world, pos, freezer);
				world.updateNeighbourForOutputSignal(pos, this);
			}
		}
		super.onRemove(state, world, pos, newState, isMoving);
	}

	@Override
	public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random)
	{
		if (state.getValue(LIT))
		{
			float f = (float)pos.getX() + 0.5F;
			float f1 = (float)pos.getY() + 0.0625F + random.nextFloat() * 14.0F / 16.0F;
			float f2 = (float)pos.getZ() + 0.5F;
			float f3 = 0.52F;
			float f4 = random.nextFloat() * 0.6F - 0.3F;
			switch (state.getValue(FACING)) {
			case WEST:
				world.addParticle(MeteorParticles.FREZA_DUST.get(), (double)(f - f3), (double)f1, (double)(f2 + f4), 0.0D, 0.0D, 0.0D);
				break;
			case EAST:
				world.addParticle(MeteorParticles.FREZA_DUST.get(), (double)(f + f3), (double)f1, (double)(f2 + f4), 0.0D, 0.0D, 0.0D);
				break;
			case NORTH:
				world.addParticle(MeteorParticles.FREZA_DUST.get(), (double)(f + f4), (double)f1, (double)(f2 - f3), 0.0D, 0.0D, 0.0D);
				break;
			case SOUTH:
				world.addParticle(MeteorParticles.FREZA_DUST.get(), (double)(f + f4), (double)f1, (double)(f2 + f3), 0.0D, 0.0D, 0.0D);
				break;
			default:
				break;
			}
		}
	}

}
