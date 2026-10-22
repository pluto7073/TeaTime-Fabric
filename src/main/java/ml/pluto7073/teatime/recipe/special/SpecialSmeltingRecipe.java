package ml.pluto7073.teatime.recipe.special;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class SpecialSmeltingRecipe extends SmeltingRecipe {

    public SpecialSmeltingRecipe(CookingBookCategory category) {
        super("", category, Ingredient.EMPTY, ItemStack.EMPTY, 0, 200);
    }

    @Override
    public abstract int getCookingTime();

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public abstract RecipeSerializer<?> getSerializer();

    @Override
    public abstract boolean matches(SingleRecipeInput container, Level world);

    @Override
    public abstract ItemStack assemble(SingleRecipeInput container, HolderLookup.Provider provider);
}
