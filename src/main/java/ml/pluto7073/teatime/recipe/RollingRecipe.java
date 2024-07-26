package ml.pluto7073.teatime.recipe;

import com.google.gson.JsonObject;
import ml.pluto7073.teatime.event.RollTeaLeaves;
import net.minecraft.MethodsReturnNonnullByDefault;
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

import java.util.Random;

@MethodsReturnNonnullByDefault
public class RollingRecipe implements Recipe<Container> {

    protected final ResourceLocation id;
    protected final Ingredient starting;
    protected final ItemStack ending;
    protected final double chance;

    private final Random random;

    public RollingRecipe(ResourceLocation id, Ingredient starting, ItemStack ending, double chance) {
        this.id = id;
        this.starting = starting;
        this.ending = ending;
        this.chance = chance;
        this.random = RollTeaLeaves.RANDOM;
    }

    @Override
    public boolean matches(Container container, Level level) {
        ItemStack stack = container.getItem(0);
        return starting.test(stack);
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess manager) {
        return craft(container);
    }

    public ItemStack craft(Container inventory) {
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
    public ItemStack getResultItem(RegistryAccess manager) {
        return ending;
    }

    @Override
    public ResourceLocation getId() {
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
        public RollingRecipe fromJson(ResourceLocation id, JsonObject json) {
            Ingredient starting = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "starting"));
            JsonObject resultData = GsonHelper.getAsJsonObject(json, "ending");
            String endingItemId = GsonHelper.getAsString(resultData, "item");
            String mod = GsonHelper.getAsString(resultData, "mod", "teatime:default");
            ItemStack ending = new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation(endingItemId)), 1);
            ending.getOrCreateTagElement("TeaData").putString("mod", mod);
            double chance = GsonHelper.getAsDouble(json, "chance", this.chance);

            return new RollingRecipe(id, starting, ending, chance);
        }

        @Override
        public RollingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            Ingredient starting = Ingredient.fromNetwork(buf);
            ItemStack ending = buf.readItem();
            double chance = buf.readDouble();
            return new RollingRecipe(id, starting, ending, chance);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, RollingRecipe recipe) {
            recipe.starting.toNetwork(buf);
            buf.writeItem(recipe.ending);
            buf.writeDouble(recipe.chance);
        }

    }

}
