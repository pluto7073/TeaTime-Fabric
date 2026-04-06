package ml.pluto7073.teatime.block;

import ml.pluto7073.teatime.block.entity.TTBlockEntityTypes;
import ml.pluto7073.teatime.block.entity.TeaKettleBlockEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("deprecation")
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TeaKettleBlock extends BaseEntityBlock {

    public static final BooleanProperty CAMPFIRE = BooleanProperty.create("campfire");
    public static final VoxelShape SHAPE = Shapes.or(Block.box(4, 0, 4, 12, 6, 12),
            Block.box(3, 1, 3, 13, 5, 13), Block.box(7, 6, 7, 9, 7, 9),
            Block.box(7.5, 3, 1, 8.5, 6, 2), Block.box(7.5, 5.5, 0.5, 8.5, 6.5, 1.5),
            Block.box(7.5, 2, 1.5, 8.5, 3.5, 3), Block.box(7.5, 4, 13, 8.5, 5, 15),
            Block.box(7.5, 3, 14, 8.5, 4, 15), Block.box(7.5, 2, 13, 8.5, 3, 15));
    public static final VoxelShape SHAPE_CAMPFIRE = Shapes.or(SHAPE, Block.box(0, -2, 0, 16, 0, 16),
            Block.box(0, -16, 0, 2, -2, 2), Block.box(0, -16, 14, 2, -2, 16),
            Block.box(14, -16, 14, 16, -2, 16), Block.box(14, -16, 0, 16, -2, 2));

    public TeaKettleBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(CAMPFIRE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CAMPFIRE);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(CAMPFIRE) ? SHAPE_CAMPFIRE : SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TeaKettleBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (world.getBlockEntity(pos) instanceof TeaKettleBlockEntity kettle) {
            kettle.load(itemStack.getOrCreateTag());
        }
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(CAMPFIRE, context.getLevel().getBlockState(context.getClickedPos().below()).is(BlockTags.CAMPFIRES));
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return true;//world.getBlockState(pos).isFaceSturdy(world, pos, Direction.UP) || state.hasProperty(BlockStateProperties.LIT);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof TeaKettleBlockEntity kettle && level instanceof ServerLevel) {
                ItemStack stack = kettle.createDroppedStack();
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
            }
            level.updateNeighbourForOutputSignal(pos, this);
        }
        super.onRemove(state, level, pos, newState, moved);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, TTBlockEntityTypes.TEA_KETTLE, TeaKettleBlockEntity::tick);
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

    protected void openScreen(Level level, BlockPos pos, Player player) {
        if (level.getBlockEntity(pos) instanceof TeaKettleBlockEntity blockEntity) {
            player.openMenu(blockEntity);
        }
    }

}
