package ml.pluto7073.teatime.event;

import com.google.gson.*;
import ml.pluto7073.teatime.TeaTime;
import ml.pluto7073.teatime.teatypes.CustomTeaType;
import ml.pluto7073.teatime.teatypes.TeaType;
import ml.pluto7073.teatime.teatypes.TeaTypes;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomTeaTypesRegisterer implements SimpleSynchronousResourceReloadListener {

    public static final ResourceLocation PHASE = TeaTime.asId("phase/tea_types");

    public CustomTeaTypesRegisterer() {
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(PHASE, (player, joined) -> TeaTypes.send(player));
    }

    @Override
    public ResourceLocation getFabricId() {
        return TeaTime.asId("custom_tea_types_registerer");
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        TeaTypes.resetRegistry();

        int i = 0;

        for (Map.Entry<ResourceLocation, Resource> entry : manager.listResources("custom_tea_types", id -> id.getPath().endsWith(".json")).entrySet()) {
            ResourceLocation id = new ResourceLocation(entry.getKey().getNamespace(),
                    entry.getKey().getPath()
                            .replace("custom_tea_types/", "")
                            .replace(".json", ""));

            try (InputStream stream = entry.getValue().open()) {
                JsonObject object = GsonHelper.parse(new InputStreamReader(stream));
                TeaTypes.register(id, loadFromJson(id, object));
                i++;
            } catch (Exception e) {
                TeaTime.logger.error("Could not load custom tea type {}", id, e);
            }
        }

        TeaTime.logger.info("Loaded {} custom tea types", i);
    }

    public static TeaType loadFromJson(ResourceLocation id, JsonObject object) {
        if (object.has("isParent")) {
            if (GsonHelper.getAsBoolean(object, "isParent")) return TeaTypes.get(new ResourceLocation(GsonHelper.getAsString(object, "parent")));
        }

        TeaType parent = TeaTypes.get(new ResourceLocation(GsonHelper.getAsString(object, "parent")));

        List<Item> ingredients = new ArrayList<>();

        if (object.has("ingredients")) {
            JsonArray ingArr = GsonHelper.getAsJsonArray(object, "ingredients");

            for (JsonElement e : ingArr) {
                if (!e.isJsonPrimitive()) {
                    TeaTime.logger.warn("Non String value in ingredients list");
                    continue;
                }
                JsonPrimitive prim = e.getAsJsonPrimitive();
                String s = prim.getAsString();
                Item item = BuiltInRegistries.ITEM.get(new ResourceLocation(s));
                if (item == null) throw new IllegalArgumentException(s + " is not a valid item id");
                ingredients.add(item);
            }
        }

        List<MobEffectInstance> effects = new ArrayList<>();

        if (object.has("effects")) {
            JsonArray ingArr = GsonHelper.getAsJsonArray(object, "effects");

            for (JsonElement e : ingArr) {
                if (!e.isJsonPrimitive()) {
                    TeaTime.logger.warn("Non String value in effects list");
                    continue;
                }
                JsonPrimitive prim = e.getAsJsonPrimitive();
                String s = prim.getAsString();
                MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(new ResourceLocation(s));
                if (effect == null) throw new IllegalArgumentException(s + " is not a valid status effect");
                if (effect.isInstantenous()) {
                    effects.add(new MobEffectInstance(effect, 1, 0));
                } else effects.add(new MobEffectInstance(effect, 60 * 20, 0));
            }
        }

        int color = parent.getColour();

        if (object.has("color")) {
            color = GsonHelper.getAsInt(object, "color");
        }

        return new CustomTeaType(id, parent, color, ingredients, effects, object);
    }

}
