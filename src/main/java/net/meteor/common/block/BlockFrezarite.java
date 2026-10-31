package net.meteor.common.block;

import java.util.ArrayList;
import java.util.List;

import net.meteor.common.MeteorItems;
import net.meteor.common.MeteorParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.storage.loot.LootParams;

/** Uses the same HEAT property as the other meteor blocks; for Frezarite it is how much cold energy is left. */
public class BlockFrezarite extends Block
{
	public BlockFrezarite(Properties props)
	{
		super(props);
		this.registerDefaultState(this.stateDefinition.any().setValue(BlockMeteor.HEAT, 0));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(BlockMeteor.HEAT);
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
		return BlockMeteor.computeDrops(this, state, builder, (meta, fortune, random) -> {
			List<ItemStack> drops = new ArrayList<ItemStack>();
			int count = random.nextInt(1 + meta + random.nextInt(fortune + 1));
			if (count > 0) {
				drops.add(new ItemStack(MeteorItems.itemFrezaCrystal.get(), count));
			}
			return drops;
		});
	}

	@Override
	public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random)
	{
		if (state.getValue(BlockMeteor.HEAT) > 0) {
			BlockMeteor.renderGlowParticles(world, pos, random, MeteorParticles.FREZA_DUST.get());
		}
	}

	@Override
	public boolean isRandomlyTicking(BlockState state) {
		return state.getValue(BlockMeteor.HEAT) > 0;
	}

	@Override
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random)
	{
		int meta = state.getValue(BlockMeteor.HEAT);
		float temp = world.getBiome(pos).value().getBaseTemperature();
		if ((meta > 0) && (temp > 0.15F)) {
			world.setBlock(pos, state.setValue(BlockMeteor.HEAT, --meta), 2);
			if (meta <= 0) {
				world.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
				world.playSound(null, pos, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1.0F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F);
			}
		}
	}

	@Override
	public int getExpDrop(BlockState state, LevelReader world, RandomSource random, BlockPos pos, int fortune, int silkTouch) {
		if (silkTouch > 0) {
			return 0;
		}
		if (state.getValue(BlockMeteor.HEAT) > 0) {
			return Mth.nextInt(random, 2, 5);
		}
		return Mth.nextInt(random, 0, 2);
	}

}
