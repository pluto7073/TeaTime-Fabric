package ml.pluto7073.teatime.recipe;

import ml.pluto7073.pdapi.recipes.WorkstationRecipeInput;
import ml.pluto7073.pdapi.tag.PDTags;
import ml.pluto7073.teatime.component.TTComponents;
import ml.pluto7073.teatime.item.TTItems;
import ml.pluto7073.teatime.recipe.special.SpecialAdditionRecipe;
import ml.pluto7073.teatime.teatypes.TeaType;
import ml.pluto7073.teatime.utils.TeaTimeUtils;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

@MethodsReturnNonnullByDefault
public class TeaInLatteAdditionRecipe extends SpecialAdditionRecipe {

    public TeaInLatteAdditionRecipe(ResourceLocation teaType) {
        super(Ingredient.of(PDTags.WORKSTATION_DRINKS),
                Ingredient.of(TTItems.TEA_BAG), teaType);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TTRecipes.TEA_IN_LATTE;
    }

    @Override
    public boolean matches(WorkstationRecipeInput container, Level level) {
        ItemStack input = container.getItem(0), addition = container.getItem(1);
        if (!input.is(PDTags.WORKSTATION_DRINKS)) return false;
        return testAddition(addition);
    }

    @Override
    public boolean testBase(ItemStack stack) {
        Item latte = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("plutoscoffee", "latte"));
        return stack.is(latte);
    }

    @Override
    public boolean testAddition(ItemStack stack) {
        return stack.is(TTItems.TEA_BAG) && stack.getOrDefault(TTComponents.TEA_TYPE, TeaType.EMPTY).unwrapKey()
                .map(key -> key.location().equals(result())).orElse(false);
    }

}
