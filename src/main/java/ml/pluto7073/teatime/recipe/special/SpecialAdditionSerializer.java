package ml.pluto7073.teatime.recipe.special;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.function.Function;

@MethodsReturnNonnullByDefault
public class SpecialAdditionSerializer<T extends SpecialAdditionRecipe> implements RecipeSerializer<T> {

    private final MapCodec<T> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public SpecialAdditionSerializer(Function<ResourceLocation, T> factory) {
        this.codec = RecordCodecBuilder.mapCodec(instance -> instance.group(ResourceLocation.CODEC.fieldOf("tea").forGetter(SpecialAdditionRecipe::result))
                .apply(instance, factory));
        this.streamCodec = ResourceLocation.STREAM_CODEC.map(factory, SpecialAdditionRecipe::result).cast();
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
