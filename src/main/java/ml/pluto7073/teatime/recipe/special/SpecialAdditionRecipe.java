package ml.pluto7073.teatime.recipe.special;

import ml.pluto7073.pdapi.recipes.DrinkWorkstationRecipe;
import ml.pluto7073.pdapi.recipes.WorkstationRecipeInput;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

@MethodsReturnNonnullByDefault
public abstract class SpecialAdditionRecipe extends DrinkWorkstationRecipe {

    public SpecialAdditionRecipe(Ingredient base, Ingredient add, ResourceLocation addin) {
        super(base, add, addin);
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public abstract RecipeSerializer<?> getSerializer();

    @Override
    public abstract boolean matches(WorkstationRecipeInput container, Level level);

    @Override
    public abstract boolean testBase(ItemStack stack);

    @Override
    public abstract boolean testAddition(ItemStack stack);

}
