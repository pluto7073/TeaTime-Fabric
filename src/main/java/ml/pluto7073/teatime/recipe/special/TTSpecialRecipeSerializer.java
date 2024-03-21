package ml.pluto7073.teatime.recipe.special;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class TTSpecialRecipeSerializer <T extends AbstractCookingRecipe> implements RecipeSerializer<T> {

    private final Factory<T> factory;

    public TTSpecialRecipeSerializer(Factory<T> factory) {
        this.factory = factory;
    }

    public T read(Identifier identifier, JsonObject jsonObject) {
        CookingRecipeCategory craftingRecipeCategory = CookingRecipeCategory.CODEC.byId(JsonHelper.getString(jsonObject, "category", null), CookingRecipeCategory.MISC);
        return this.factory.create(identifier, craftingRecipeCategory);
    }

    public T read(Identifier identifier, PacketByteBuf packetByteBuf) {
        CookingRecipeCategory craftingRecipeCategory = packetByteBuf.readEnumConstant(CookingRecipeCategory.class);
        return this.factory.create(identifier, craftingRecipeCategory);
    }

    public void write(PacketByteBuf packetByteBuf, T craftingRecipe) {
        packetByteBuf.writeEnumConstant(craftingRecipe.getCategory());
    }

    @FunctionalInterface
    public interface Factory<T extends AbstractCookingRecipe> {
        T create(Identifier id, CookingRecipeCategory category);
    }

}
