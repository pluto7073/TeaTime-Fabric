package ml.pluto7073.teatime.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import ml.pluto7073.teatime.recipe.TeaBagRecipe;
import ml.pluto7073.teatime.recipe.TeaRecipe;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftingMenu.class)
@Debug(export = true)
public class CraftingMenuMixin {

    @Inject(method = "slotChangedCraftingGrid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isItemEnabled(Lnet/minecraft/world/flag/FeatureFlagSet;)Z"))
    private static void teatime$AssembleTeaBag(AbstractContainerMenu menu, Level level, Player player, CraftingContainer container, ResultContainer result, CallbackInfo ci, @Local CraftingRecipe recipe, @Local(ordinal = 1) LocalRef<ItemStack> itemStackRef) {
        if (recipe instanceof TeaBagRecipe bagRecipe) {
            itemStackRef.set(bagRecipe.assemble(container, level));
        } else if (recipe instanceof TeaRecipe teaRecipe) {
            itemStackRef.set(teaRecipe.assemble(container, level));
        }
    }

}
