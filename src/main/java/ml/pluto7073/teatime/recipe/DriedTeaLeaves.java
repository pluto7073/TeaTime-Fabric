package ml.pluto7073.teatime.recipe;

import ml.pluto7073.teatime.recipe.special.SpecialSmeltingRecipe;
import ml.pluto7073.teatime.utils.TeaTimeUtils;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class DriedTeaLeaves extends SpecialSmeltingRecipe {

    public DriedTeaLeaves(Identifier id) {
        super(id);
    }

    @Override
    public int getCookTime() {
        return 3000;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.DRIED_TEA_LEAVES;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        ItemStack input = inventory.getStack(0);
        return TeaTimeUtils.hasDryingResult(input);
    }

    @Override
    public ItemStack craft(Inventory inventory) {
        ItemStack input = inventory.getStack(0);
        return new ItemStack(TeaTimeUtils.getDryingResult(input), 1);
    }

    public ItemStack craft(DefaultedList<ItemStack> slots) {
        ItemStack input = slots.get(0);
        return new ItemStack(TeaTimeUtils.getDryingResult(input), 1);
    }

}
