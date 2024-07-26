package ml.pluto7073.teatime.mixin;

import ml.pluto7073.teatime.TeaTime;
import ml.pluto7073.teatime.recipe.DriedTeaLeaves;
import ml.pluto7073.teatime.recipe.DriedTeaLeavesBlasting;
import ml.pluto7073.teatime.utils.TeaTimeUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceEntityMixin {

    @Inject(at = @At("RETURN"), method = "canBurn", cancellable = true)
    private static void teatime_isRecipeTeaLeaf(RegistryAccess registryAccess, @Nullable Recipe<?> recipe, NonNullList<ItemStack> inventory, int maxStackSize, CallbackInfoReturnable<Boolean> cir) {
        if (inventory.get(0).isEmpty() || recipe == null) {
            cir.setReturnValue(false);
            return;
        }
        if (recipe instanceof DriedTeaLeaves dtlRecipe) {
            ItemStack input = inventory.get(0);
            if (!TeaTimeUtils.hasDryingResult(input)) {
                return;
            }
            ItemStack outSlot = inventory.get(2);

            ItemStack output = dtlRecipe.craft(inventory);
            if (outSlot.isEmpty()) {
                cir.setReturnValue(true);
            } else if (!output.is(outSlot.getItem())) {
                cir.setReturnValue(false);
            } else if (outSlot.getCount() < maxStackSize && outSlot.getCount() < outSlot.getMaxStackSize()) {
                cir.setReturnValue(true);
            } else {
                cir.setReturnValue(outSlot.getCount() < output.getMaxStackSize());
            }
        } else if (recipe instanceof DriedTeaLeavesBlasting dtlBlasting) {
            ItemStack input = inventory.get(0);
            if (!TeaTimeUtils.hasDryingResult(input)) {
                return;
            }
            ItemStack outSlot = inventory.get(2);

            ItemStack output = dtlBlasting.craft(inventory);
            if (outSlot.isEmpty()) {
                cir.setReturnValue(true);
            } else if (!output.is(outSlot.getItem())) {
                cir.setReturnValue(false);
            } else if (outSlot.getCount() < maxStackSize && outSlot.getCount() < outSlot.getMaxStackSize()) {
                cir.setReturnValue(true);
            } else {
                cir.setReturnValue(outSlot.getCount() < output.getMaxStackSize());
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "burn", cancellable = true)
    private static void teatime_craftTeaLeaves(RegistryAccess registryAccess, @Nullable Recipe<?> recipe, NonNullList<ItemStack> inventory, int maxStackSize, CallbackInfoReturnable<Boolean> cir) {
        if (recipe instanceof DriedTeaLeaves dtlRecipe) {
            ItemStack output = dtlRecipe.craft(inventory);
            ItemStack outputSlot = inventory.get(2);
            if (outputSlot.isEmpty()) {
                inventory.set(2, output.copy());
            } else if (outputSlot.is(output.getItem())) {
                outputSlot.grow(1);
            }
            inventory.get(0).shrink(1);
            cir.setReturnValue(true);
        } else if (recipe instanceof DriedTeaLeavesBlasting dtlBlasting) {
            ItemStack output = dtlBlasting.craft(inventory);
            ItemStack outputSlot = inventory.get(2);
            if (outputSlot.isEmpty()) {
                inventory.set(2, output.copy());
            } else if (outputSlot.is(output.getItem())) {
                outputSlot.grow(1);
            }
            inventory.get(0).shrink(1);
            cir.setReturnValue(true);
        }
    }

}
