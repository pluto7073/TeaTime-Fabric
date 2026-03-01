package ml.pluto7073.teatime.recipe;

import ml.pluto7073.teatime.recipe.special.SpecialBlastingRecipe;
import ml.pluto7073.teatime.utils.TeaTimeUtils;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

@MethodsReturnNonnullByDefault
public class DriedTeaLeavesBlasting extends SpecialBlastingRecipe {

    public DriedTeaLeavesBlasting(ResourceLocation id, CookingBookCategory category) {
        super(id, category);
    }

    @Override
    public int getCookingTime() {
        return 100;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TTRecipes.DRIED_TEA_LEAVES_BLASTING;
    }

    @Override
    public boolean matches(Container container, Level level) {
        ItemStack input = container.getItem(0);
        return TeaTimeUtils.hasDryingResult(input);
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess manager) {
        ItemStack input = container.getItem(0);
        return new ItemStack(TeaTimeUtils.getDryingResult(input), 1);
    }

    public ItemStack craft(NonNullList<ItemStack> slots) {
        ItemStack input = slots.get(0);
        return new ItemStack(TeaTimeUtils.getDryingResult(input), 1);
    }


}
