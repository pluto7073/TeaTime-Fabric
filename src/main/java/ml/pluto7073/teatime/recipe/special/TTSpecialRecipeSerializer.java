package ml.pluto7073.teatime.recipe.special;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.Objects;
import java.util.function.Function;

@MethodsReturnNonnullByDefault
public class TTSpecialRecipeSerializer <T extends AbstractCookingRecipe> implements RecipeSerializer<T> {

    private final MapCodec<T> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public TTSpecialRecipeSerializer(Function<CookingBookCategory, T> factory) {
        codec = RecordCodecBuilder.mapCodec(instance -> instance
                .group(CookingBookCategory.CODEC.fieldOf("category").orElse(null).forGetter(AbstractCookingRecipe::category))
                .apply(instance, factory));
        streamCodec = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8.map(name -> Objects.equals(name, "") ? null : CookingBookCategory.valueOf(name),
                        category -> category == null ? "" : category.name()),
                AbstractCookingRecipe::category,
                factory
        );
    }

    @Override
    public MapCodec<T> codec() {
        return codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
        return streamCodec;
    }

}
