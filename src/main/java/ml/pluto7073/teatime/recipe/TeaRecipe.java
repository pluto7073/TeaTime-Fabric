package ml.pluto7073.teatime.recipe;

import ml.pluto7073.teatime.item.TTItems;
import ml.pluto7073.teatime.teatypes.TeaType;
import ml.pluto7073.teatime.utils.TeaTimeUtils;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

@MethodsReturnNonnullByDefault
public class TeaRecipe extends CustomRecipe {

    public TeaRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level world) {
        boolean hasWaterBottle = false;
        boolean hasTeaBag = false;

        for (int i = 0; i < container.getContainerSize(); ++i) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) continue;
            if (stack.is(Items.POTION) && !hasWaterBottle) {
                if (PotionUtils.getPotion(stack).equals(Potions.WATER)) {
                    hasWaterBottle = true;
                } else {
                    return false;
                }
            } else {
                if (!stack.is(TTItems.TEA_BAG) || hasTeaBag) {
                    return false;
                }
                hasTeaBag = true;
            }
        }
        return hasTeaBag && hasWaterBottle;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess manager) {
        return new ItemStack(TTItems.TEA);
    }

    public ItemStack assemble(CraftingContainer container, Level level) {
        ItemStack teaBag = ItemStack.EMPTY;
        for (int i = 0; i < container.getContainerSize(); ++i) {
            ItemStack stack = container.getItem(i);
            if (stack.is(TTItems.TEA_BAG)) {
                teaBag = stack;
                break;
            }
        }
        TeaType type = TeaTimeUtils.getTeaType(teaBag, level);
        return TeaTimeUtils.setTeaType(new ItemStack(TTItems.TEA, 1), type, level);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 1 && height >= 1;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TTRecipes.TEA_BREWING;
    }

}
