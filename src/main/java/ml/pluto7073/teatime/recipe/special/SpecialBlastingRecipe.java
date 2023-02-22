package ml.pluto7073.teatime.recipe.special;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.BlastingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public abstract class SpecialBlastingRecipe extends BlastingRecipe {
    public SpecialBlastingRecipe(Identifier id) {
        super(id, "", Ingredient.EMPTY, ItemStack.EMPTY, 0, 200);
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
