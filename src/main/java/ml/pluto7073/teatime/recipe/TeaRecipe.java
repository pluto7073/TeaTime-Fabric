package ml.pluto7073.teatime.recipe;

import ml.pluto7073.teatime.TeaTime;
import ml.pluto7073.teatime.item.ModItems;
import ml.pluto7073.teatime.teatypes.TeaType;
import ml.pluto7073.teatime.utils.TeaTimeUtils;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class TeaRecipe extends SpecialCraftingRecipe {

    public TeaRecipe(Identifier id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        boolean hasWaterBottle = false;
        boolean hasTeaBag = false;

        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isEmpty()) continue;
            if (stack.isOf(Items.POTION) && !hasWaterBottle) {
                if (PotionUtil.getPotion(stack).equals(Potions.WATER)) {
                    hasWaterBottle = true;
                } else {
                    return false;
                }
            } else {
                if (!stack.isOf(ModItems.TEA_BAG) || hasTeaBag) {
                    return false;
                }
                hasTeaBag = true;
            }
        }
        return hasTeaBag && hasWaterBottle;
    }

    @Override
    public ItemStack craft(CraftingInventory inventory) {
        ItemStack teaBag = ItemStack.EMPTY;
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isOf(ModItems.TEA_BAG)) {
                teaBag = stack;
                break;
            }
        }
        TeaType type = TeaTimeUtils.getTeaType(teaBag);
        return TeaTimeUtils.setTeaType(new ItemStack(ModItems.TEA, 1), type);
    }

    @Override
    public boolean fits(int width, int height) {
        return width >= 1 && height >= 1;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.TEA_BREWING;
    }

}
