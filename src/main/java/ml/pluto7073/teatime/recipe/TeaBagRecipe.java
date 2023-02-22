package ml.pluto7073.teatime.recipe;

import ml.pluto7073.teatime.item.ModItems;
import ml.pluto7073.teatime.tags.ModItemTags;
import ml.pluto7073.teatime.teatypes.TeaType;
import ml.pluto7073.teatime.utils.TeaTimeUtils;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class TeaBagRecipe extends SpecialCraftingRecipe {

    public TeaBagRecipe(Identifier id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        boolean hasPaper = false;
        boolean hasString = false;
        boolean hasTeaLeaves = false;
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isEmpty()) continue;
            if (stack.isOf(Items.PAPER) && !hasPaper) {
                hasPaper = true;
            } else if (stack.isIn(ModItemTags.BREWABLE_TEA_LEAVES) && !hasTeaLeaves) {
                hasTeaLeaves = true;
            } else {
                if (!stack.isOf(Items.STRING) || hasString) {
                    return false;
                }

                hasString = true;
            }
        }
        return hasString && hasPaper && hasTeaLeaves;
    }

    @Override
    public ItemStack craft(CraftingInventory inventory) {
        ItemStack teaLeaves = ItemStack.EMPTY;
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isIn(ModItemTags.BREWABLE_TEA_LEAVES)) {
                teaLeaves = stack;
                break;
            }
        }

        ItemStack teaBag = new ItemStack(ModItems.TEA_BAG, 1);
        TeaType teaType = TeaType.getFromLeaves(teaLeaves);
        if (teaType == TeaType.EMPTY) {
            throw new IllegalStateException("There is no teaType for Item: " + Registry.ITEM.getId(teaLeaves.getItem()));
        }
        return TeaTimeUtils.setTeaType(teaBag, teaType);
    }

    @Override
    public boolean fits(int width, int height) {
        return width >= 2 && height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.TEA_BAG_MAKING;
    }

}
