package ml.pluto7073.teatime.recipe;

import ml.pluto7073.teatime.item.TTItems;
import ml.pluto7073.teatime.teatypes.TeaType;
import ml.pluto7073.teatime.teatypes.TeaTypeManager;
import ml.pluto7073.teatime.utils.TeaTimeUtils;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

@MethodsReturnNonnullByDefault
public class TeaBagRecipe extends CustomRecipe {

    public TeaBagRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        List<Item> items = getItems(container);
        if (!items.contains(Items.STRING) || !items.contains(Items.PAPER)) return false;
        items.remove(Items.PAPER);
        items.remove(Items.STRING);
        return TeaTypeManager.getFromIngredients(items) != TeaTypeManager.EMPTY_TYPE;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess manager) {
        List<Item> ingredients = getItems(container);
        ingredients.remove(Items.PAPER);
        ingredients.remove(Items.STRING);
        ItemStack teaBag = new ItemStack(TTItems.TEA_BAG, 1);
        TeaType teaType = TeaTypeManager.getFromIngredients(ingredients);
        if (teaType == TeaTypeManager.EMPTY_TYPE) {
            throw new IllegalStateException("There is no teaType for ingredients: " + ingredients.stream().map(BuiltInRegistries.ITEM::getId).toList());
        }
        return TeaTimeUtils.setTeaType(teaBag, teaType);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return (width >= 1 && height >= 3) || (width >= 3 && height >= 1);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TTRecipes.TEA_BAG_MAKING;
    }

    public static List<Item> getItems(CraftingContainer container) {
        ArrayList<Item> list = new ArrayList<>();
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty() || stack.is(Items.AIR)) continue;
            list.add(stack.getItem());
        }
        return list;
    }

}
