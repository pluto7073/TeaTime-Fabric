package ml.pluto7073.teatime.utils;

import com.google.gson.JsonObject;
import ml.pluto7073.teatime.networking.packets.s2c.SyncRollableRecipesRegistryS2CPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class RollableTeaLeavesUtil {

    private static final Map<Identifier, Recipe> REGISTRY = new HashMap<>();

    public static Recipe register(Identifier id, Recipe.Builder recipe) {
        Recipe r = recipe.build();
        REGISTRY.put(id, r);
        return r;
    }

    public static boolean contains(Identifier id) {
        return REGISTRY.containsKey(id);
    }

    public static Recipe getFromStarting(ItemStack stack) {
        for (Identifier key : REGISTRY.keySet()) {
            Recipe recipe = REGISTRY.get(key);
            if (recipe.matches(stack)) {
                return recipe;
            }
        }
        throw new IllegalArgumentException("No such recipe with starting item: " + Registries.ITEM.getId(stack.getItem()));
    }

    public static void resetRegistry() {
        REGISTRY.clear();
    }

    public static void send(ServerPlayerEntity player) {

        Map<Identifier, JsonObject> recipes = new HashMap<>();
        REGISTRY.forEach((identifier, recipe) -> recipes.put(identifier, recipe.getAsJson()));

        ServerPlayNetworking.send(player, new SyncRollableRecipesRegistryS2CPacket(recipes));

    }

    public static class Recipe {

        private final Ingredient starting;
        private final ItemStack ending;
        private final double chance;
        private final JsonObject baseData;

        private Recipe(Ingredient starting, ItemStack ending, double chance, JsonObject baseData) {
            this.starting = starting;
            this.ending = ending;
            this.chance = chance;
            this.baseData = baseData;
        }

        public boolean matches(ItemStack stack) {
            return starting.test(stack);
        }

        public Optional<ItemStack> getWithChance(Random random) {
            double d = random.nextDouble();
            if (d <= chance) {
                return Optional.of(ending.copy());
            }
            return Optional.empty();
        }

        public JsonObject getAsJson() {
            return baseData;
        }

        public static class Builder {
            private Ingredient starting = Ingredient.EMPTY;
            private ItemStack ending = ItemStack.EMPTY;
            private double chance = 1.0;
            private JsonObject baseData = new JsonObject();

            public Builder setStarting(Ingredient ingredient) {
                this.starting = ingredient;
                return this;
            }

            public Builder setEnding(ItemStack stack) {
                this.ending = stack;
                return this;
            }

            public Builder setChance(double d) {
                this.chance = d;
                return this;
            }

            public Builder setBaseData(JsonObject data) {
                this.baseData = data;
                return this;
            }

            public Recipe build() {
                return new Recipe(starting, ending, chance, baseData);
            }

        }

    }

}
