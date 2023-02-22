package ml.pluto7073.teatime.mixin;

import ml.pluto7073.teatime.TeaTime;
import ml.pluto7073.teatime.recipe.DriedTeaLeaves;
import ml.pluto7073.teatime.recipe.DriedTeaLeavesBlasting;
import ml.pluto7073.teatime.utils.TeaTimeUtils;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceEntityMixin {

    @Inject(at = @At("RETURN"), method = "canAcceptRecipeOutput", cancellable = true)
    private static void teatime_isRecipeTeaLeaf(Recipe<?> recipe, DefaultedList<ItemStack> slots, int count, CallbackInfoReturnable<Boolean> cir) {
        if (slots.get(0).isEmpty() || recipe == null) {
            cir.setReturnValue(false);
            return;
        }
        if (recipe instanceof DriedTeaLeaves dtlRecipe) {
            ItemStack input = slots.get(0);
            if (!TeaTimeUtils.hasDryingResult(input)) {
                return;
            }
            ItemStack outSlot = slots.get(2);

            ItemStack output = dtlRecipe.craft(slots);
            if (outSlot.isEmpty()) {
                cir.setReturnValue(true);
            } else if (!output.isItemEqualIgnoreDamage(outSlot)) {
                cir.setReturnValue(false);
            } else if (outSlot.getCount() < count && outSlot.getCount() < outSlot.getMaxCount()) {
                cir.setReturnValue(true);
            } else {
                cir.setReturnValue(outSlot.getCount() < output.getMaxCount());
            }
        } else if (recipe instanceof DriedTeaLeavesBlasting dtlBlasting) {
            ItemStack input = slots.get(0);
            if (!TeaTimeUtils.hasDryingResult(input)) {
                return;
            }
            ItemStack outSlot = slots.get(2);

            ItemStack output = dtlBlasting.craft(slots);
            if (outSlot.isEmpty()) {
                cir.setReturnValue(true);
            } else if (!output.isItemEqualIgnoreDamage(outSlot)) {
                cir.setReturnValue(false);
            } else if (outSlot.getCount() < count && outSlot.getCount() < outSlot.getMaxCount()) {
                cir.setReturnValue(true);
            } else {
                cir.setReturnValue(outSlot.getCount() < output.getMaxCount());
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "craftRecipe", cancellable = true)
    private static void teatime_craftTeaLeaves(Recipe<?> recipe, DefaultedList<ItemStack> slots, int count, CallbackInfoReturnable<Boolean> cir) {
        if (recipe instanceof DriedTeaLeaves dtlRecipe) {
            ItemStack output = dtlRecipe.craft(slots);
            ItemStack outputSlot = slots.get(2);
            if (outputSlot.isEmpty()) {
                slots.set(2, output.copy());
            } else if (outputSlot.isOf(output.getItem())) {
                outputSlot.increment(1);
            }
            slots.get(0).decrement(1);
            cir.setReturnValue(true);
        } else if (recipe instanceof DriedTeaLeavesBlasting dtlBlasting) {
            ItemStack output = dtlBlasting.craft(slots);
            ItemStack outputSlot = slots.get(2);
            if (outputSlot.isEmpty()) {
                slots.set(2, output.copy());
            } else if (outputSlot.isOf(output.getItem())) {
                outputSlot.increment(1);
            }
            slots.get(0).decrement(1);
            cir.setReturnValue(true);
        }
    }

}
