package ml.pluto7073.teatime.event;

import ml.pluto7073.teatime.recipe.TTRecipes;
import ml.pluto7073.teatime.recipe.RollingRecipe;
import ml.pluto7073.teatime.tags.TTBlockTags;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public class RollTeaLeaves {

    public static final Random RANDOM = new Random();

    public static void rollLeavesEvent() {
        RecipeManager.CachedCheck<Container, ? extends RollingRecipe> matchGetter = RecipeManager.createCheck(TTRecipes.ROLLING);
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            if (player == null ) {
                return InteractionResult.PASS;
            }
            ItemStack stack = player.getItemInHand(hand);
            BlockPos pos = hitResult.getBlockPos();
            BlockState state = level.getBlockState(pos);

            if (state == null) {
                return InteractionResult.PASS;
            }

            if (!state.is(TTBlockTags.WORKSTATIONS)) {
                return InteractionResult.PASS;
            }

            SimpleContainer container = new SimpleContainer(1);
            container.setItem(0, stack);
            RollingRecipe recipe = matchGetter.getRecipeFor(container, level).orElse(null);

            if (recipe == null) {
                return InteractionResult.PASS;
            }

            ItemStack result = recipe.craft(container);
            level.playSound(null, pos, SoundEvents.BONE_MEAL_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
            if (!result.isEmpty()) {
                if (!level.isClientSide) {
                    ItemEntity entity = new ItemEntity(
                            player.level(),
                            hitResult.getLocation().x,
                            hitResult.getLocation().y,
                            hitResult.getLocation().z,
                            result.copy());
                    player.level().addFreshEntity(entity);
                    stack.shrink(1);
                    player.setItemInHand(hand, stack);
                }
            }
            return InteractionResult.SUCCESS;
        });

    }

}
