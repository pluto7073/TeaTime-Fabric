package ml.pluto7073.teatime.recipe;

import ml.pluto7073.teatime.TeaTime;
import ml.pluto7073.teatime.item.ModItems;
import ml.pluto7073.teatime.tags.ModItemTags;
import ml.pluto7073.teatime.teatypes.TeaType;
import ml.pluto7073.teatime.teatypes.TeaTypes;
import ml.pluto7073.teatime.utils.TeaTimeUtils;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class TeaBagRecipe extends SpecialCraftingRecipe {

    public TeaBagRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        List<Item> items = getItems(inventory);
        if (!items.contains(Items.STRING) || !items.contains(Items.PAPER)) return false;
        items.remove(Items.PAPER);
        items.remove(Items.STRING);
        return TeaTypes.getFromIngredients(items) != TeaTypes.EMPTY;
    }

    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager manager) {
        List<Item> ingredients = getItems(inventory);
        ingredients.remove(Items.PAPER);
        ingredients.remove(Items.STRING);
        ItemStack teaBag = new ItemStack(ModItems.TEA_BAG, 1);
        TeaType teaType = TeaTypes.getFromIngredients(ingredients);
        if (teaType == TeaTypes.EMPTY) {
            throw new IllegalStateException("There is no teaType for ingredients: " + ingredients.stream().map(Registries.ITEM::getId).toList());
        }
        return TeaTimeUtils.setTeaType(teaBag, teaType);
    }

    @Override
    public boolean fits(int width, int height) {
        return (width >= 1 && height >= 3) || (width >= 3 && height >= 1);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.TEA_BAG_MAKING;
    }

    public static List<Item> getItems(RecipeInputInventory inventory) {
        ArrayList<Item> list = new ArrayList<>();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isEmpty() || stack.isOf(Items.AIR)) continue;
            list.add(stack.getItem());
        }
        return list;
    }

}
