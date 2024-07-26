package ml.pluto7073.teatime.recipe.special;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

@MethodsReturnNonnullByDefault
public abstract class SpecialBlastingRecipe extends BlastingRecipe {
    public SpecialBlastingRecipe(ResourceLocation id, CookingBookCategory category) {
        super(id, "", category, Ingredient.EMPTY, ItemStack.EMPTY, 0, 200);
    }

    @Override
    public abstract int getCookingTime();

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public abstract RecipeSerializer<?> getSerializer();

    @Override
    public abstract boolean matches(Container container, Level level);

    @Override
    public ItemStack getResultItem(RegistryAccess registryManager) {
        return ItemStack.EMPTY;
    }

    @Override
    public abstract ItemStack assemble(Container container, RegistryAccess registryManager);
}
