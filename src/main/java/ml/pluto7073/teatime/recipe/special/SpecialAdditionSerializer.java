package ml.pluto7073.teatime.recipe.special;

import com.google.gson.JsonObject;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.RecipeSerializer;

@MethodsReturnNonnullByDefault
public class SpecialAdditionSerializer<T extends SpecialAdditionRecipe> implements RecipeSerializer<T> {

    private final Factory<T> factory;

    public SpecialAdditionSerializer(Factory<T> factory) {
        this.factory = factory;
    }

    @Override
    public T fromJson(ResourceLocation id, JsonObject json) {
        return this.factory.create(id, new ResourceLocation(GsonHelper.getAsString(json, "tea")));
    }

    @Override
    public T fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
        return this.factory.create(id, new ResourceLocation(buf.readUtf()));
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, T recipe) {
        buf.writeUtf(recipe.getResultId().toString());
    }

    @FunctionalInterface
    public interface Factory<T extends SpecialAdditionRecipe> {
        T create(ResourceLocation id, ResourceLocation type);
    }

}
