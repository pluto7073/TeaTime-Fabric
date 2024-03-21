package ml.pluto7073.teatime.recipe.special;

import ml.pluto7073.pdapi.recipes.DrinkWorkstationRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

public abstract class SpecialAdditionRecipe extends DrinkWorkstationRecipe {

    public SpecialAdditionRecipe(Identifier id) {
        super(id, Ingredient.EMPTY, Ingredient.EMPTY, "pdapi:empty");
    }

}
