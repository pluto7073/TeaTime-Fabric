package ml.pluto7073.teatime;

import com.google.gson.JsonObject;
import ml.pluto7073.teatime.utils.RollableTeaLeavesUtil;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.NoSuchElementException;

public class ModServerResourceManager implements SimpleSynchronousResourceReloadListener {

    @Override
    public Identifier getFabricId() {
        return new Identifier(TeaTime.MOD_ID, "custom_resources");
    }

    @Override
    public void reload(ResourceManager manager) {
        int i = 0;
        for (Identifier id : manager.findResources("rollable_tea_leaf_recipes", path -> path.toString().endsWith(".json")).keySet()) {
            try (InputStream stream = manager.getResource(id).get().getInputStream()) {
                JsonObject data = JsonHelper.deserialize(new InputStreamReader(stream));
                String starting = JsonHelper.getString(data, "starting");
                Ingredient startingI = Ingredient.ofItems(Registry.ITEM.get(new Identifier(starting)));
                JsonObject endingData = JsonHelper.getObject(data, "ending");
                Item endingItem = Registry.ITEM.get(new Identifier(JsonHelper.getString(endingData, "item")));
                ItemStack endingStack = new ItemStack(endingItem, 1);
                String mod = JsonHelper.getString(endingData, "mod", "teatime:default");
                endingStack.getOrCreateSubNbt("TeaData").putString("mod", mod);
                double chance = JsonHelper.getDouble(data, "chance", 1.0);
                RollableTeaLeavesUtil.register(id, new RollableTeaLeavesUtil.Recipe.Builder().setStarting(startingI).setEnding(endingStack).setChance(chance));
                ++i;
            } catch (IOException | NoSuchElementException e) {
                TeaTime.logger.error("Error occurred while loading resource json " + id.toString(), e);
            }
        }
        TeaTime.logger.info("Loaded " + i + " resources");
    }

}
