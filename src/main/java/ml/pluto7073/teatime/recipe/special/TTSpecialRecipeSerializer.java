package ml.pluto7073.teatime.recipe.special;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class TTSpecialRecipeSerializer <T extends AbstractCookingRecipe> implements RecipeSerializer<T> {

    private final Factory<T> factory;

    public TTSpecialRecipeSerializer(Factory<T> factory) {
        this.factory = factory;
    }

    public T fromJson(ResourceLocation identifier, JsonObject jsonObject) {
        CookingBookCategory craftingRecipeCategory = CookingBookCategory.CODEC.byName(GsonHelper.getAsString(jsonObject, "category", null), CookingBookCategory.MISC);
        return this.factory.create(identifier, craftingRecipeCategory);
    }

    public T fromNetwork(ResourceLocation identifier, FriendlyByteBuf packetByteBuf) {
        CookingBookCategory craftingRecipeCategory = packetByteBuf.readEnum(CookingBookCategory.class);
        return this.factory.create(identifier, craftingRecipeCategory);
    }

    public void toNetwork(FriendlyByteBuf packetByteBuf, T craftingRecipe) {
        packetByteBuf.writeEnum(craftingRecipe.category());
    }

    @FunctionalInterface
    public interface Factory<T extends AbstractCookingRecipe> {
        T create(ResourceLocation id, CookingBookCategory category);
    }

}
