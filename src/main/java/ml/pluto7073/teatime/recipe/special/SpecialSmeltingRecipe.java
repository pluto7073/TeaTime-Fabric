package ml.pluto7073.teatime.recipe.special;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public abstract class SpecialSmeltingRecipe extends SmeltingRecipe {

    public SpecialSmeltingRecipe(Identifier id) {
        super(id, "", Ingredient.empty(), ItemStack.EMPTY, 0, 200);
    }

    @Override
    public abstract int getCookTime();

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }

    @Override
    public ItemStack getOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public abstract RecipeSerializer<?> getSerializer();

    @Override
    public abstract boolean matches(Inventory inventory, World world);

    @Override
    public abstract ItemStack craft(Inventory inventory);
}
