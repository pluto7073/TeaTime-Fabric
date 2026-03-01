package ml.pluto7073.teatime.block;

import ml.pluto7073.teatime.block.entity.ModBlockEntityTypes;
import ml.pluto7073.teatime.block.entity.SteamerBlockEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
@MethodsReturnNonnullByDefault
public class SteamerBlock extends BaseEntityBlock {

    protected static final VoxelShape SHAPE;

    public SteamerBlock(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            this.openScreen(level, pos, player);
            return InteractionResult.CONSUME;
        }
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (stack.hasCustomHoverName()) {
            BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof SteamerBlockEntity steamerEntity) {
                steamerEntity.setCustomName(stack.getHoverName());
            }
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof SteamerBlockEntity steamerEntity) {
                if (level instanceof ServerLevel) {
                    Containers.dropContents(level, pos, steamerEntity);
                }

                level.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        double x = pos.getX() + 0.4 + (double) random.nextFloat() * 0.2;
        double y = pos.getY() + 0.3 + (double) random.nextFloat() * 0.3;
        double z = pos.getZ() + 0.4 + (double) random.nextFloat() * 0.2;
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof SteamerBlockEntity steamerBlockEntity) {
            if (steamerBlockEntity.isBoiling(pos, level)) {
                level.addParticle(ParticleTypes.SPLASH, x, y, z, 0.0, 0.0, 0.0);
                if (random.nextDouble() < 0.1) {
                    level.playLocalSound(x, y, z, SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundSource.BLOCKS, 1.0F, 1.0F, false);
                }
            }
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SteamerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, ModBlockEntityTypes.STEAMER, SteamerBlockEntity::tick);
    }

    protected void openScreen(Level level, BlockPos pos, Player player) {
        if (level.getBlockEntity(pos) instanceof SteamerBlockEntity blockEntity) {
            player.openMenu(blockEntity);
            //TODO Stats.INTERACT_WITH_STEAMER
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState floor = level.getBlockState(pos.below());
        return floor.is(BlockTags.CAMPFIRES);
    }

    static {
        SHAPE = Shapes.or(
                /*Middle Bar*/ Block.box(1, 7, 7, 15, 9, 9),
                /*West Leg*/Block.box(0, -16, 7, 1, 10, 9),
                /*East Leg*/Block.box(15, -16, 7, 16, 10, 9),
                /*Steamer*/Block.box(2, -3, 2, 14, 3, 14));
    }

}
