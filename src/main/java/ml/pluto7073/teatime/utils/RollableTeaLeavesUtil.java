package ml.pluto7073.teatime.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class RollableTeaLeavesUtil {

    public static final Map<Identifier, Recipe> REGISTRY = new HashMap<>();

    public static Recipe register(Identifier id, Recipe.Builder recipe) {
        Recipe r = recipe.build();
        REGISTRY.put(id, r);
        return r;
    }

    public static Recipe getFromStarting(ItemStack stack) {
        for (Identifier key : REGISTRY.keySet()) {
            Recipe recipe = REGISTRY.get(key);
            if (recipe.matches(stack)) {
                return recipe;
            }
        }
        throw new IllegalArgumentException("No such recipe with starting item: " + Registry.ITEM.getId(stack.getItem()));
    }

    public static class Recipe {

        private final Ingredient starting;
        private final ItemStack ending;
        private final double chance;

        private Recipe(Ingredient starting, ItemStack ending, double chance) {
            this.starting = starting;
            this.ending = ending;
            this.chance = chance;
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

        public static class Builder {
            private Ingredient starting = Ingredient.EMPTY;
            private ItemStack ending = ItemStack.EMPTY;
            private double chance = 1.0;

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

            public Recipe build() {
                return new Recipe(starting, ending, chance);
            }

        }

    }

}
