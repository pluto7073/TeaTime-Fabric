package ml.pluto7073.teatime.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ml.pluto7073.teatime.block.ModBlocks;
import ml.pluto7073.teatime.block.entity.SteamerBlockEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

@MethodsReturnNonnullByDefault
public class SteamerRecipe implements Recipe<Container> {

    protected final ResourceLocation id;
    protected final String group;
    protected final Ingredient input;
    public final ItemStack output;
    protected final int steamTime;

    public SteamerRecipe(ResourceLocation id, String group, Ingredient input, ItemStack output, int steamTime) {
        this.id = id;
        this.group = group;
        this.input = input;
        this.output = output;
        this.steamTime = steamTime;
    }

    @Override
    public boolean matches(Container container, Level level) {
        return this.input.test(container.getItem(SteamerBlockEntity.INPUT_SLOT_INDEX));
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess manager) {
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
    public ItemStack getResultItem(RegistryAccess manager) {
        return this.output;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.STEAMING_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.STEAMING;
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
        return new ItemStack(ModBlocks.STEAMER);
    }

    public static class Serializer implements RecipeSerializer<SteamerRecipe> {

        private final int steamTime;

        public Serializer(int steamTime) {
            this.steamTime = steamTime;
        }

        @Override
        public SteamerRecipe fromJson(ResourceLocation id, JsonObject json) {
            String group = GsonHelper.getAsString(json, "group", "");
            JsonElement ingredientJson = GsonHelper.isArrayNode(json, "ingredient") ?
                    GsonHelper.getAsJsonArray(json, "ingredient") : GsonHelper.getAsJsonObject(json, "ingredient");
            Ingredient input = Ingredient.fromJson(ingredientJson);
            ResourceLocation resultId = new ResourceLocation(GsonHelper.getAsString(json, "result"));
            ItemStack result = new ItemStack(BuiltInRegistries.ITEM.getOptional(resultId).orElseThrow(() ->
                    new IllegalStateException("Item: " + resultId + " does not exist")));
            int time = GsonHelper.getAsInt(json, "steamtime", this.steamTime);
            return new SteamerRecipe(id, group, input, result, time);
        }

        @Override
        public SteamerRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            String group = buf.readUtf();
            Ingredient input = Ingredient.fromNetwork(buf);
            ItemStack result = buf.readItem();
            int time = buf.readVarInt();
            return new SteamerRecipe(id, group, input, result, time);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, SteamerRecipe recipe) {
            buf.writeUtf(recipe.group);
            recipe.input.toNetwork(buf);
            buf.writeItem(recipe.output);
            buf.writeVarInt(recipe.steamTime);
        }
    }

}
