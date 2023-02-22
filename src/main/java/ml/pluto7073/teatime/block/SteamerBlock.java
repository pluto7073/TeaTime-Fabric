package ml.pluto7073.teatime.block;

import ml.pluto7073.teatime.block.entity.ModBlockEntityTypes;
import ml.pluto7073.teatime.block.entity.SteamerBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class SteamerBlock extends BlockWithEntity {

    protected static final VoxelShape SHAPE;

    public SteamerBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            this.openScreen(world, pos, player);
            return ActionResult.CONSUME;
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (itemStack.hasCustomName()) {
            BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof SteamerBlockEntity steamerEntity) {
                steamerEntity.setCustomName(itemStack.getName());
            }
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof SteamerBlockEntity steamerEntity) {
                if (world instanceof ServerWorld) {
                    ItemScatterer.spawn(world, pos, steamerEntity);
                }

                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        double x = pos.getX() + 0.4 + (double) random.nextFloat() * 0.2;
        double y = pos.getY() + 0.3 + (double) random.nextFloat() * 0.3;
        double z = pos.getZ() + 0.4 + (double) random.nextFloat() * 0.2;
        BlockEntity entity = world.getBlockEntity(pos);
        if (entity instanceof SteamerBlockEntity steamerBlockEntity) {
            if (steamerBlockEntity.isBoiling(pos, world)) {
                world.addParticle(ParticleTypes.SPLASH, x, y, z, 0.0, 0.0, 0.0);
                if (random.nextDouble() < 0.1) {
                    world.playSound(x, y, z, SoundEvents.BLOCK_BUBBLE_COLUMN_BUBBLE_POP, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
                }
            }
        }
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SteamerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : checkType(type, ModBlockEntityTypes.STEAMER_TYPE, SteamerBlockEntity::tick);
    }

    protected void openScreen(World world, BlockPos pos, PlayerEntity player) {
        if (world.getBlockEntity(pos) instanceof SteamerBlockEntity blockEntity) {
            player.openHandledScreen(blockEntity);
            //TODO Stats.INTERACT_WITH_STEAMER
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockState floor = world.getBlockState(pos.down());
        return floor.isIn(BlockTags.CAMPFIRES);
    }

    static {
        SHAPE = VoxelShapes.union(
                /*Middle Bar*/ Block.createCuboidShape(1, 7, 7, 15, 9, 9),
                /*West Leg*/Block.createCuboidShape(0, -16, 7, 1, 10, 9),
                /*East Leg*/Block.createCuboidShape(15, -16, 7, 16, 10, 9),
                /*Steamer*/Block.createCuboidShape(2, -3, 2, 14, 3, 14));
    }

}
