package net.meteor.common.block;

import net.meteor.common.MeteorBlockEntities;
import net.meteor.common.tileentity.TileEntityMeteorTimer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** The redstone output used to be the block metadata; it is the POWER property now. */
public class BlockMeteorTimer extends BaseEntityBlock {

	public static final IntegerProperty POWER = BlockStateProperties.POWER;
	private static final VoxelShape COLLISION = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
	// The selection box was drawn 0.125 higher than the block bounds
	private static final VoxelShape OUTLINE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D);

	public BlockMeteorTimer(Properties props) {
		super(props);
		this.registerDefaultState(this.stateDefinition.any().setValue(POWER, 0));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(POWER);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return OUTLINE;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return COLLISION;
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	public int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
		return state.getValue(POWER);
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
	{
		if (!world.isClientSide && world.getBlockEntity(pos) instanceof TileEntityMeteorTimer tEntity) {
			tEntity.quickMode = !tEntity.quickMode;
			if (tEntity.quickMode) {
				player.sendSystemMessage(Component.translatable("MeteorTimer.modeChange.two"));
			} else {
				player.sendSystemMessage(Component.translatable("MeteorTimer.modeChange.one"));
			}
			tEntity.setChanged();
			world.sendBlockUpdated(pos, state, state, 3);
		}
		return InteractionResult.sidedSuccess(world.isClientSide);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileEntityMeteorTimer(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return world.isClientSide ? null : createTickerHelper(type, MeteorBlockEntities.METEOR_TIMER.get(), TileEntityMeteorTimer::tick);
	}

}
