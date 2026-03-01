package ml.pluto7073.teatime.recipe;

import ml.pluto7073.pdapi.tag.PDTags;
import ml.pluto7073.teatime.item.TTItems;
import ml.pluto7073.teatime.recipe.special.SpecialAdditionRecipe;
import ml.pluto7073.teatime.utils.TeaTimeUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class TeaInLatteAdditionRecipe extends SpecialAdditionRecipe {

    public TeaInLatteAdditionRecipe(ResourceLocation id, ResourceLocation teaType) {
        super(id, Ingredient.of(PDTags.WORKSTATION_DRINKS),
                Ingredient.of(TeaTimeUtils.setTeaType(new ItemStack(TTItems.TEA_BAG), teaType)), teaType.toString());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TTRecipes.TEA_IN_LATTE;
    }

    @Override
    public boolean matches(Container container, Level level) {
        ItemStack input = container.getItem(0), addition = container.getItem(1);
        if (!input.is(PDTags.WORKSTATION_DRINKS)) return false;
        if (!addition.is(TTItems.TEA_BAG)) return false;
        return TeaTimeUtils.getTeaTypeId(addition).equals(getResultId());
    }

    @Override
    public boolean testBase(ItemStack stack) {
        Item latte = BuiltInRegistries.ITEM.get(new ResourceLocation("plutoscoffee:latte"));
        return stack.is(latte);
    }

    @Override
    public boolean testAddition(ItemStack stack) {
        return stack.is(TTItems.TEA_BAG) && (TeaTimeUtils.getTeaTypeId(stack).equals(getResultId()));
    }

}
