package ml.pluto7073.teatime.recipe;

import ml.pluto7073.teatime.component.TTComponents;
import ml.pluto7073.teatime.item.TTItems;
import ml.pluto7073.teatime.teatypes.TeaType;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TeaRecipe extends CustomRecipe {

    public TeaRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput container, Level world) {
        boolean hasWaterBottle = false;
        boolean hasTeaBag = false;

        for (int i = 0; i < container.size(); ++i) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) continue;
            if (stack.is(Items.POTION) && !hasWaterBottle) {
                if (stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)
                        .is(Potions.WATER)) {
                    hasWaterBottle = true;
                } else {
                    return false;
                }
            } else {
                if (!stack.is(TTItems.TEA_BAG) || hasTeaBag) {
                    return false;
                }
                hasTeaBag = true;
            }
        }
        return hasTeaBag && hasWaterBottle;
    }

    @Override
    public ItemStack assemble(CraftingInput container, HolderLookup.Provider manager) {
        ItemStack teaBag = ItemStack.EMPTY;
        for (int i = 0; i < container.size(); ++i) {
            ItemStack stack = container.getItem(i);
            if (stack.is(TTItems.TEA_BAG)) {
                teaBag = stack;
                break;
            }
        }
        Holder<TeaType> teaId = teaBag.getOrDefault(TTComponents.TEA_TYPE, TeaType.EMPTY);
        ItemStack stack = new ItemStack(TTItems.TEA, 1);
        stack.set(TTComponents.TEA_TYPE, teaId);
        return stack;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 1 && height >= 1;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TTRecipes.TEA_BREWING;
    }

}
