package ml.pluto7073.teatime.recipe;

import ml.pluto7073.teatime.block.entity.SteamerBlockEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

@MethodsReturnNonnullByDefault
public record SteamerRecipeInput(ItemStack input) implements RecipeInput {

    @Override
    public ItemStack getItem(int index) {
        if (index == SteamerBlockEntity.INPUT_SLOT_INDEX) {
            return input;
        }
         throw new IllegalArgumentException("No slot with index " + index);
    }

    @Override
    public int size() {
        return 1;
    }
}
