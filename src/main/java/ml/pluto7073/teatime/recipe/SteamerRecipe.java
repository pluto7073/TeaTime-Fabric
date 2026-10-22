package ml.pluto7073.teatime.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ml.pluto7073.teatime.block.TTBlocks;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SteamerRecipe implements Recipe<SteamerRecipeInput> {

    protected final String group;
    protected final Ingredient input;
    public final ItemStack output;
    protected final int steamTime;

    public SteamerRecipe(String group, Ingredient input, ItemStack output, int steamTime) {
        this.group = group;
        this.input = input;
        this.output = output;
        this.steamTime = steamTime;
    }

    @Override
    public boolean matches(SteamerRecipeInput container, Level level) {
        return this.input.test(container.input());
    }

    @Override
    public ItemStack assemble(SteamerRecipeInput container, HolderLookup.Provider manager) {
        return this.output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(this.input);
        return ingredients;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider manager) {
        return this.output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TTRecipes.STEAMING_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return TTRecipes.STEAMING;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    public int getSteamTime() {
        return this.steamTime;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(TTBlocks.STEAMER);
    }

    public static class Serializer implements RecipeSerializer<SteamerRecipe> {

        public static final MapCodec<SteamerRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(Codec.STRING.fieldOf("group").orElse("").forGetter(recipe -> recipe.group),
                        Ingredient.CODEC.fieldOf("ingredient").forGetter(recipe -> recipe.input),
                        ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.output),
                        Codec.INT.fieldOf("steamtime").orElse(200).forGetter(recipe -> recipe.steamTime))
                        .apply(instance, SteamerRecipe::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, SteamerRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, SteamerRecipe::getGroup,
                Ingredient.CONTENTS_STREAM_CODEC, recipe -> recipe.input,
                ItemStack.STREAM_CODEC, recipe -> recipe.output,
                ByteBufCodecs.INT, recipe -> recipe.steamTime,
                SteamerRecipe::new
        );

        private final int steamTime;

        public Serializer(int steamTime) {
            this.steamTime = steamTime;
        }

        @Override
        public MapCodec<SteamerRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SteamerRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

}
