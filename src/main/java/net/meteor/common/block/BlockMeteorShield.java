package net.meteor.common.block;

import net.meteor.common.ClientHandler;
import net.meteor.common.MeteorBlockEntities;
import net.meteor.common.MeteorConfig;
import net.meteor.common.MeteorSounds;
import net.meteor.common.climate.HandlerMeteor;
import net.meteor.common.climate.HandlerWorld;
import net.meteor.common.tileentity.TileEntityMeteorShield;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
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
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

public class BlockMeteorShield extends BaseEntityBlock
{
	/** The direction the placing player was looking at; used by the renderer like the old metadata. */
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	private static final VoxelShape SHAPE = Block.box(1.0D, 6.0D, 1.0D, 15.0D, 16.0D, 15.0D);

	public BlockMeteorShield(Properties props)
	{
		super(props);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemstack)
	{
		if (placer instanceof Player player) {
			if (!world.isClientSide) {
				player.sendSystemMessage(ClientHandler.createMessage(Component.translatable("MeteorShield.charging"), ChatFormatting.YELLOW));
				world.playSound(null, pos, MeteorSounds.SHIELD_HUMM.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
			}
			if (world.getBlockEntity(pos) instanceof TileEntityMeteorShield shield) {
				shield.owner = player.getGameProfile().getName();
				shield.setChanged();
			}
		}
		super.setPlacedBy(world, pos, state, placer, itemstack);
	}

	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving)
	{
		if (!state.is(newState.getBlock())) {
			BlockEntity tile = world.getBlockEntity(pos);
			if (tile instanceof TileEntityMeteorShield shield) {
				if (!world.isClientSide) {
					HandlerMeteor metHandler = HandlerWorld.get(world);
					if (metHandler != null) {
						metHandler.getShieldManager().removeShield(shield);
					}
					world.playSound(null, pos, MeteorSounds.SHIELD_POWERDOWN.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
				}
				Containers.dropContents(world, pos, shield);
			}
		}
		super.onRemove(state, world, pos, newState, isMoving);
	}

	@Override
	public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random)
	{
		if (MeteorConfig.safeGet(MeteorConfig.meteorShieldSound, true) && random.nextInt(256) == 100) {
			world.playLocalSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, MeteorSounds.SHIELD_HUMM.get(), SoundSource.BLOCKS, 0.6F, 1.0F, false);
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
	{
		if (!world.isClientSide && world.getBlockEntity(pos) instanceof TileEntityMeteorShield shield) {
			NetworkHooks.openScreen((ServerPlayer) player, shield, pos);
		}
		return InteractionResult.sidedSuccess(world.isClientSide);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileEntityMeteorShield(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, MeteorBlockEntities.METEOR_SHIELD.get(), TileEntityMeteorShield::tick);
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

}
