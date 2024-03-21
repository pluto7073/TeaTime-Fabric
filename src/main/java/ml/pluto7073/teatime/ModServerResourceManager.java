package ml.pluto7073.teatime;

import com.google.gson.JsonObject;
import ml.pluto7073.teatime.utils.RollableTeaLeavesUtil;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.NoSuchElementException;

public class ModServerResourceManager implements SimpleSynchronousResourceReloadListener {

    public static final Identifier PHASE = TeaTime.asId("phase/rollable_tea_leaf_recipes");

    public ModServerResourceManager() {
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(PHASE, ((player, joined) -> RollableTeaLeavesUtil.send(player)));
    }

    @Override
    public Identifier getFabricId() {
        return new Identifier(TeaTime.MOD_ID, "custom_resources");
    }

    @Override
    public void reload(ResourceManager manager) {
        RollableTeaLeavesUtil.resetRegistry();

        int i = 0;
        for (Map.Entry<Identifier, Resource> entry : manager.findResources("rollable_tea_leaf_recipes", path -> path.toString().endsWith(".json")).entrySet()) {
            Identifier id = new Identifier(entry.getKey().getNamespace(),
                    entry.getKey().getPath()
                            .replace("rollable_tea_leaf_recipes/", "")
                            .replace(".json", ""));
            try (InputStream stream = entry.getValue().getInputStream()) {
                JsonObject data = JsonHelper.deserialize(new InputStreamReader(stream));

                RollableTeaLeavesUtil.register(id, loadRecipeFromJson(data));
                ++i;
            } catch (IOException | NoSuchElementException e) {
                TeaTime.logger.error("Error occurred while loading resource json " + id, e);
            }
        }
        TeaTime.logger.info("Loaded " + i + " roll-able recipes");
    }

    public static RollableTeaLeavesUtil.Recipe.Builder loadRecipeFromJson(JsonObject data) {
        String starting = JsonHelper.getString(data, "starting");
        Ingredient startingI = Ingredient.ofItems(Registries.ITEM.get(new Identifier(starting)));
        JsonObject endingData = JsonHelper.getObject(data, "ending");
        Item endingItem = Registries.ITEM.get(new Identifier(JsonHelper.getString(endingData, "item")));
        ItemStack endingStack = new ItemStack(endingItem, 1);
        String mod = JsonHelper.getString(endingData, "mod", "teatime:default");
        endingStack.getOrCreateSubNbt("TeaData").putString("mod", mod);
        double chance = JsonHelper.getDouble(data, "chance", 1.0);
        return new RollableTeaLeavesUtil.Recipe.Builder().setStarting(startingI).setEnding(endingStack).setChance(chance).setBaseData(data);
    }

}
