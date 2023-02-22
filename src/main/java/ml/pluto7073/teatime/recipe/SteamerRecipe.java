package ml.pluto7073.teatime.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ml.pluto7073.teatime.block.ModBlocks;
import ml.pluto7073.teatime.block.entity.SteamerBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class SteamerRecipe implements Recipe<Inventory> {

    protected final Identifier id;
    protected final String group;
    protected final Ingredient input;
    protected final ItemStack output;
    protected final int steamTime;

    public SteamerRecipe(Identifier id, String group, Ingredient input, ItemStack output, int steamTime) {
        this.id = id;
        this.group = group;
        this.input = input;
        this.output = output;
        this.steamTime = steamTime;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        return this.input.test(inventory.getStack(SteamerBlockEntity.INPUT_SLOT_INDEX));
    }

    @Override
    public ItemStack craft(Inventory inventory) {
        return this.output.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> ingredients = DefaultedList.of();
        ingredients.add(this.input);
        return ingredients;
    }

    @Override
    public ItemStack getOutput() {
        return this.output;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
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
    public ItemStack createIcon() {
        return new ItemStack(ModBlocks.STEAMER);
    }

    public static class Serializer implements RecipeSerializer<SteamerRecipe> {

        private final int steamTime;

        public Serializer(int steamTime) {
            this.steamTime = steamTime;
        }

        @Override
        public SteamerRecipe read(Identifier id, JsonObject json) {
            String group = JsonHelper.getString(json, "group", "");
            JsonElement ingredientJson = JsonHelper.hasArray(json, "ingredient") ?
                    JsonHelper.getArray(json, "ingredient") : JsonHelper.getObject(json, "ingredient");
            Ingredient input = Ingredient.fromJson(ingredientJson);
            Identifier resultId = new Identifier(JsonHelper.getString(json, "result"));
            ItemStack result = new ItemStack(Registry.ITEM.getOrEmpty(resultId).orElseThrow(() ->
                    new IllegalStateException("Item: " + resultId + " does not exist")));
            int time = JsonHelper.getInt(json, "steamtime", this.steamTime);
            return new SteamerRecipe(id, group, input, result, time);
        }

        @Override
        public SteamerRecipe read(Identifier id, PacketByteBuf buf) {
            String group = buf.readString();
            Ingredient input = Ingredient.fromPacket(buf);
            ItemStack result = buf.readItemStack();
            int time = buf.readVarInt();
            return new SteamerRecipe(id, group, input, result, time);
        }

        @Override
        public void write(PacketByteBuf buf, SteamerRecipe recipe) {
            buf.writeString(recipe.group);
            recipe.input.write(buf);
            buf.writeItemStack(recipe.output);
            buf.writeVarInt(recipe.steamTime);
        }
    }

}
