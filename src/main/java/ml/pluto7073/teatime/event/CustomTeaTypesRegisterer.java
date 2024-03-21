package ml.pluto7073.teatime.event;

import com.google.gson.*;
import ml.pluto7073.teatime.TeaTime;
import ml.pluto7073.teatime.teatypes.CustomTeaType;
import ml.pluto7073.teatime.teatypes.TeaType;
import ml.pluto7073.teatime.teatypes.TeaTypes;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.spongepowered.asm.mixin.injection.struct.InjectorGroupInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomTeaTypesRegisterer implements SimpleSynchronousResourceReloadListener {

    public static final Identifier PHASE = TeaTime.asId("phase/tea_types");

    public CustomTeaTypesRegisterer() {
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(PHASE, (player, joined) -> TeaTypes.send(player));
    }

    @Override
    public Identifier getFabricId() {
        return TeaTime.asId("custom_tea_types_registerer");
    }

    @Override
    public void reload(ResourceManager manager) {
        TeaTypes.resetRegistry();

        int i = 0;

        for (Map.Entry<Identifier, Resource> entry : manager.findResources("custom_tea_types", id -> id.getPath().endsWith(".json")).entrySet()) {
            Identifier id = new Identifier(entry.getKey().getNamespace(),
                    entry.getKey().getPath()
                            .replace("custom_tea_types/", "")
                            .replace(".json", ""));

            try (InputStream stream = entry.getValue().getInputStream()) {
                JsonObject object = JsonHelper.deserialize(new InputStreamReader(stream));
                TeaTypes.register(id, loadFromJson(object));
                i++;
            } catch (Exception e) {
                TeaTime.logger.error("Could not load custom tea type " + id, e);
            }
        }

        TeaTime.logger.info("Loaded {} custom tea types", i);
    }

    public static TeaType loadFromJson(JsonObject object) {
        if (object.has("isParent")) {
            if (JsonHelper.getBoolean(object, "isParent")) return TeaTypes.get(new Identifier(JsonHelper.getString(object, "parent")));
        }

        TeaType parent = TeaTypes.get(new Identifier(JsonHelper.getString(object, "parent")));

        List<Item> ingredients = new ArrayList<>();

        if (object.has("ingredients")) {
            JsonArray ingArr = JsonHelper.getArray(object, "ingredients");

            for (JsonElement e : ingArr) {
                if (!e.isJsonPrimitive()) {
                    TeaTime.logger.warn("Non String value in ingredients list");
                    continue;
                }
                JsonPrimitive prim = e.getAsJsonPrimitive();
                String s = prim.getAsString();
                Item item = Registries.ITEM.get(new Identifier(s));
                if (item == null) throw new IllegalArgumentException(s + " is not a valid item id");
                ingredients.add(item);
            }
        }

        List<StatusEffectInstance> effects = new ArrayList<>();

        if (object.has("effects")) {
            JsonArray ingArr = JsonHelper.getArray(object, "effects");

            for (JsonElement e : ingArr) {
                if (!e.isJsonPrimitive()) {
                    TeaTime.logger.warn("Non String value in effects list");
                    continue;
                }
                JsonPrimitive prim = e.getAsJsonPrimitive();
                String s = prim.getAsString();
                StatusEffect effect = Registries.STATUS_EFFECT.get(new Identifier(s));
                if (effect == null) throw new IllegalArgumentException(s + " is not a valid status effect");
                effects.add(new StatusEffectInstance(effect, 60 * 20, 0));
            }
        }

        int color = parent.getColour();

        if (object.has("color")) {
            color = JsonHelper.getInt(object, "color");
        }

        return new CustomTeaType(parent, color, ingredients, effects, object);
    }

}
