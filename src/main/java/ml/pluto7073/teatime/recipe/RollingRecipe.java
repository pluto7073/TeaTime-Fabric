package ml.pluto7073.teatime.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ml.pluto7073.teatime.event.RollTeaLeaves;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class RollingRecipe implements Recipe<SingleRecipeInput> {

    protected final Ingredient starting;
    protected final ItemStack ending;
    protected final double chance;

    private final Random random;

    public RollingRecipe(Ingredient starting, ItemStack ending, double chance) {
        this.starting = starting;
        this.ending = ending;
        this.chance = chance;
        this.random = RollTeaLeaves.RANDOM;
    }

    @Override
    public boolean matches(SingleRecipeInput container, Level level) {
        ItemStack stack = container.getItem(0);
        return starting.test(stack);
    }

    @Override
    public ItemStack assemble(SingleRecipeInput container, HolderLookup.Provider manager) {
        double r = random.nextDouble();
        if (r <= chance) {
            return ending;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider manager) {
        return ending;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TTRecipes.ROLLING_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return TTRecipes.ROLLING;
    }

    public static class Serializer implements RecipeSerializer<RollingRecipe> {

        public static final MapCodec<RollingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(Ingredient.CODEC.fieldOf("starting").forGetter(r -> r.starting),
                                ItemStack.CODEC.fieldOf("ending").forGetter(r -> r.ending),
                                Codec.DOUBLE.fieldOf("chance").orElse(1.0).forGetter(r -> r.chance))
                        .apply(instance, RollingRecipe::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, RollingRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, r -> r.starting,
                ItemStack.STREAM_CODEC, r -> r.ending,
                ByteBufCodecs.DOUBLE, r -> r.chance,
                RollingRecipe::new
        );

        public Serializer() {
        }

        @Override
        public MapCodec<RollingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, RollingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

}
