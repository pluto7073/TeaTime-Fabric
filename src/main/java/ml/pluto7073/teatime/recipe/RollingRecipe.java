package ml.pluto7073.teatime.recipe;

import com.google.gson.JsonObject;
import ml.pluto7073.teatime.event.RollTeaLeaves;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.Random;

public class RollingRecipe implements Recipe<Inventory> {

    protected final Identifier id;
    protected final Ingredient starting;
    protected final ItemStack ending;
    protected final double chance;

    private final Random random;

    public RollingRecipe(Identifier id, Ingredient starting, ItemStack ending, double chance) {
        this.id = id;
        this.starting = starting;
        this.ending = ending;
        this.chance = chance;
        this.random = RollTeaLeaves.RANDOM;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        ItemStack stack = inventory.getStack(0);
        return starting.test(stack);
    }

    @Override
    public ItemStack craft(Inventory inventory) {
        double r = random.nextDouble();
        if (r <= chance) {
            return ending;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput() {
        return ending;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.ROLLING_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.ROLLING;
    }

    public static class Serializer implements RecipeSerializer<RollingRecipe> {

        private final double chance;

        public Serializer(double chance) {
            this.chance = chance;
        }

        @Override
        public RollingRecipe read(Identifier id, JsonObject json) {
            Ingredient starting = Ingredient.fromJson(JsonHelper.getObject(json, "starting"));
            JsonObject resultData = JsonHelper.getObject(json, "ending");
            String endingItemId = JsonHelper.getString(resultData, "item");
            String mod = JsonHelper.getString(resultData, "mod", "teatime:default");
            ItemStack ending = new ItemStack(Registry.ITEM.get(new Identifier(endingItemId)), 1);
            ending.getOrCreateSubNbt("TeaData").putString("mod", mod);
            double chance = JsonHelper.getDouble(json, "chance", this.chance);

            return new RollingRecipe(id, starting, ending, chance);
        }

        @Override
        public RollingRecipe read(Identifier id, PacketByteBuf buf) {
            Ingredient starting = Ingredient.fromPacket(buf);
            ItemStack ending = buf.readItemStack();
            double chance = buf.readDouble();
            return new RollingRecipe(id, starting, ending, chance);
        }

        @Override
        public void write(PacketByteBuf buf, RollingRecipe recipe) {
            recipe.starting.write(buf);
            buf.writeItemStack(recipe.ending);
            buf.writeDouble(recipe.chance);
        }

    }

}
