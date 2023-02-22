package ml.pluto7073.teatime.event;

import ml.pluto7073.teatime.recipe.ModRecipes;
import ml.pluto7073.teatime.recipe.RollingRecipe;
import ml.pluto7073.teatime.tags.ModBlockTags;
import ml.pluto7073.teatime.utils.RollableTeaLeavesUtil;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;
import java.util.Random;

public class RollTeaLeaves {

    public static final Random RANDOM = new Random();

    public static void rollLeavesEvent() {
        RecipeManager.MatchGetter<Inventory, ? extends RollingRecipe> matchGetter = RecipeManager.createCachedMatchGetter(ModRecipes.ROLLING);
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (player == null ) {
                return ActionResult.PASS;
            }
            ItemStack stack = player.getStackInHand(hand);
            BlockPos pos = hitResult.getBlockPos();
            BlockState state = world.getBlockState(pos);

            if (state == null) {
                return ActionResult.PASS;
            }

            if (!state.isIn(ModBlockTags.WORKSTATIONS)) {
                return ActionResult.PASS;
            }

            SimpleInventory inventory = new SimpleInventory(1);
            inventory.setStack(0, stack);
            RollingRecipe recipe = matchGetter.getFirstMatch(inventory, world).orElse(null);

            if (recipe == null) {
                return ActionResult.PASS;
            }

            ItemStack result = recipe.craft(inventory);
            world.playSound(null, pos, SoundEvents.ITEM_BONE_MEAL_USE, SoundCategory.PLAYERS, 1.0F, 1.0F);
            if (!result.isEmpty()) {
                if (!world.isClient) {
                    ItemEntity entity = new ItemEntity(
                            player.world,
                            hitResult.getPos().x,
                            hitResult.getPos().y,
                            hitResult.getPos().z,
                            result.copy());
                    player.world.spawnEntity(entity);
                    stack.decrement(1);
                    player.setStackInHand(hand, stack);
                }
            }
            return ActionResult.SUCCESS;
        });

    }

}
