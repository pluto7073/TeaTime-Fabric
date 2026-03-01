package ml.pluto7073.teatime.recipe;

import ml.pluto7073.teatime.TeaTime;
import ml.pluto7073.teatime.recipe.special.SpecialAdditionSerializer;
import ml.pluto7073.teatime.recipe.special.TTSpecialRecipeSerializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;

public class TTRecipes {

    //Recipe Types
    public static final RecipeType<SteamerRecipe> STEAMING;
    public static final RecipeType<RollingRecipe> ROLLING;
    //Serializers
    public static final RecipeSerializer<SteamerRecipe> STEAMING_SERIALIZER;
    public static final RecipeSerializer<RollingRecipe> ROLLING_SERIALIZER;
    public static final SimpleCraftingRecipeSerializer<TeaBagRecipe> TEA_BAG_MAKING;
    public static final SimpleCraftingRecipeSerializer<TeaRecipe> TEA_BREWING;
    public static final TTSpecialRecipeSerializer<DriedTeaLeaves> DRIED_TEA_LEAVES;
    public static final TTSpecialRecipeSerializer<DriedTeaLeavesBlasting> DRIED_TEA_LEAVES_BLASTING;
    public static final SpecialAdditionSerializer<TeaInLatteAdditionRecipe> TEA_IN_LATTE;

    private static <T extends Recipe<?>> RecipeType<T> register(final String id) {
        return Registry.register(BuiltInRegistries.RECIPE_TYPE, new ResourceLocation(TeaTime.MOD_ID, id), new RecipeType<T>() {
            @Override
            public String toString() {
                return new ResourceLocation(TeaTime.MOD_ID, id).toString();
            }
        });
    }

    private static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(String id, S serializer) {
        return Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, new ResourceLocation(TeaTime.MOD_ID, id), serializer);
    }

    static {
        //Recipe Types
        STEAMING = register("steaming");
        ROLLING = register("rolling");
        //Serializers
        STEAMING_SERIALIZER = register("steaming", new SteamerRecipe.Serializer(1200));
        ROLLING_SERIALIZER = register("rolling", new RollingRecipe.Serializer(1.0));
        TEA_BAG_MAKING = register("crafting_special_teabagmaking", new SimpleCraftingRecipeSerializer<>(TeaBagRecipe::new));
        TEA_BREWING = register("crafting_special_teabrewing", new SimpleCraftingRecipeSerializer<>(TeaRecipe::new));
        DRIED_TEA_LEAVES = register("smelting_special_driedtealeaves", new TTSpecialRecipeSerializer<>(DriedTeaLeaves::new));
        DRIED_TEA_LEAVES_BLASTING = register("blasting_special_driedtealeaves", new TTSpecialRecipeSerializer<>(DriedTeaLeavesBlasting::new));
        TEA_IN_LATTE = register("drink_workstation_special_teainlatte", new SpecialAdditionSerializer<>(TeaInLatteAdditionRecipe::new));
    }

    public static void init() {}

}
