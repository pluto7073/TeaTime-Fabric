package ml.pluto7073.teatime.recipe;

import ml.pluto7073.pdapi.DrinkUtil;
import ml.pluto7073.pdapi.addition.DrinkAdditions;
import ml.pluto7073.pdapi.item.AbstractCustomizableDrinkItem;
import ml.pluto7073.teatime.TeaTime;
import ml.pluto7073.teatime.item.ModItems;
import ml.pluto7073.teatime.recipe.special.SpecialAdditionRecipe;
import ml.pluto7073.teatime.teatypes.TeaType;
import ml.pluto7073.teatime.teatypes.TeaTypes;
import ml.pluto7073.teatime.utils.TeaTimeUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class TeaInLatteAdditionRecipe extends SpecialAdditionRecipe {

    public TeaInLatteAdditionRecipe(ResourceLocation id) {
        super(id);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.TEA_IN_LATTE;
    }

    @Override
    public boolean matches(Container container, Level level) {
        Item latteItem = BuiltInRegistries.ITEM.get(new ResourceLocation("plutoscoffee:latte"));
        if (latteItem == null || Items.AIR.equals(latteItem)) return false;
        ItemStack input = container.getItem(0), addition = container.getItem(1);
        if (!input.is(latteItem)) return false;
        if (!addition.is(ModItems.TEA_BAG)) return false;
        return TeaTimeUtils.getTeaType(addition) != TeaTypes.EMPTY;
    }

    @Override
    public ItemStack craft(Container container) {
        ItemStack stack = container.getItem(0).copy();
        ItemStack teaBag = container.getItem(1);
        ListTag resAdds = stack.getOrCreateTagElement(AbstractCustomizableDrinkItem.DRINK_DATA_NBT_KEY)
                .getList(DrinkAdditions.ADDITIONS_NBT_KEY, Tag.TAG_STRING);
        resAdds.add(DrinkUtil.stringAsNbt(TeaTimeUtils.getTeaTypeStr(teaBag)));
        stack.getOrCreateTagElement(AbstractCustomizableDrinkItem.DRINK_DATA_NBT_KEY).put(DrinkAdditions.ADDITIONS_NBT_KEY, resAdds);

        return stack;
    }

    @Override
    public boolean testBase(ItemStack stack) {
        Item latte = BuiltInRegistries.ITEM.get(new ResourceLocation("plutoscoffee:latte"));
        return stack.is(latte);
    }

    @Override
    public boolean testAddition(ItemStack stack) {
        return stack.is(ModItems.TEA_BAG);
    }

}
