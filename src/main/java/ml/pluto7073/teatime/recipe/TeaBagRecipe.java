package ml.pluto7073.teatime.recipe;

import ml.pluto7073.teatime.component.TTComponents;
import ml.pluto7073.teatime.item.TTItems;
import ml.pluto7073.teatime.teatypes.TeaType;
import ml.pluto7073.teatime.utils.TeaTimeUtils;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TeaBagRecipe extends CustomRecipe {

    public TeaBagRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput container, Level level) {
        List<Item> items = getItems(container);
        if (!items.contains(Items.STRING) || !items.contains(Items.PAPER)) return false;
        items.remove(Items.PAPER);
        items.remove(Items.STRING);
        return TeaType.getFromIngredients(container.items(), level.registryAccess()).isPresent();
    }

    @Override
    public ItemStack assemble(CraftingInput container, HolderLookup.Provider provider) {
        List<Item> ingredients = getItems(container);
        ingredients.remove(Items.PAPER);
        ingredients.remove(Items.STRING);
        ItemStack teaBag = new ItemStack(TTItems.TEA_BAG, 1);
        Optional<Holder.Reference<TeaType>> teaType = TeaType.getFromIngredients(container.items(), provider);
        if (teaType.isEmpty()) {
            throw new IllegalStateException("There is no teaType for ingredients: " + ingredients.stream().map(BuiltInRegistries.ITEM::getId).toList());
        }
        teaBag.set(TTComponents.TEA_TYPE, teaType.get());
        return teaBag;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return (width >= 1 && height >= 3) || (width >= 3 && height >= 1);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TTRecipes.TEA_BAG_MAKING;
    }

    public static List<Item> getItems(CraftingInput container) {
        ArrayList<Item> list = new ArrayList<>();
        for (int i = 0; i < container.size(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty() || stack.is(Items.AIR)) continue;
            list.add(stack.getItem());
        }
        return list;
    }

}
