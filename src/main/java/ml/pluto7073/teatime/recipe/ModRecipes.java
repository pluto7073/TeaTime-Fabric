package ml.pluto7073.teatime.recipe;

import ml.pluto7073.teatime.TeaTime;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModRecipes {

    //Recipe Types
    public static final RecipeType<SteamerRecipe> STEAMING;
    public static final RecipeType<RollingRecipe> ROLLING;
    //Serializers
    public static final RecipeSerializer<SteamerRecipe> STEAMING_SERIALIZER;
    public static final RecipeSerializer<RollingRecipe> ROLLING_SERIALIZER;
    public static final SpecialRecipeSerializer<TeaBagRecipe> TEA_BAG_MAKING;
    public static final SpecialRecipeSerializer<TeaRecipe> TEA_BREWING;
    public static final SpecialRecipeSerializer<DriedTeaLeaves> DRIED_TEA_LEAVES;
    public static final SpecialRecipeSerializer<DriedTeaLeavesBlasting> DRIED_TEA_LEAVES_BLASTING;

    private static <T extends Recipe<?>> RecipeType<T> register(final String id) {
        return Registry.register(Registry.RECIPE_TYPE, new Identifier(TeaTime.MOD_ID, id), new RecipeType<T>() {
            @Override
            public String toString() {
                return new Identifier(TeaTime.MOD_ID, id).toString();
            }
        });
    }

    private static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(String id, S serializer) {
        return Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(TeaTime.MOD_ID, id), serializer);
    }

    static {
        //Recipe Types
        STEAMING = register("steaming");
        ROLLING = register("rolling");
        //Serializers
        STEAMING_SERIALIZER = register("steaming", new SteamerRecipe.Serializer(1200));
        ROLLING_SERIALIZER = register("rolling", new RollingRecipe.Serializer(1.0));
        TEA_BAG_MAKING = register("crafting_special_teabagmaking", new SpecialRecipeSerializer<>(TeaBagRecipe::new));
        TEA_BREWING = register("crafting_special_teabrewing", new SpecialRecipeSerializer<>(TeaRecipe::new));
        DRIED_TEA_LEAVES = register("smelting_special_driedtealeaves", new SpecialRecipeSerializer<>(DriedTeaLeaves::new));
        DRIED_TEA_LEAVES_BLASTING = register("blasting_special_driedtealeaves", new SpecialRecipeSerializer<>(DriedTeaLeavesBlasting::new));
    }

    public static void init() {}

}
