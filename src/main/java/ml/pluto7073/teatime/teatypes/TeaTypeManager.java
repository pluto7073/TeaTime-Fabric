package ml.pluto7073.teatime.teatypes;

import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import ml.pluto7073.pdapi.util.DrinkUtil;
import ml.pluto7073.teatime.TeaTime;
import ml.pluto7073.teatime.item.ModItems;
import ml.pluto7073.teatime.networking.packets.s2c.SyncCustomTeaTypesRegistererS2CPacket;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class TeaTypeManager implements SimpleSynchronousResourceReloadListener {

    public static final ResourceLocation PHASE = TeaTime.asId("phase/tea_types");

    private static final Map<ResourceLocation, TeaType> REGISTRY = new HashMap<>();
    private static final Map<ResourceLocation, TeaType> DEFAULT_REGISTRY = new HashMap<>();

    public static final TeaType EMPTY;
    public static final TeaType HERBAL_TEA;
    public static final TeaType WHITE_TEA;
    public static final TeaType GREEN_TEA;
    public static final TeaType BLACK_TEA;

    public TeaTypeManager() {
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(PHASE, (player, joined) -> TeaTypeManager.send(player));
    }

    public static TeaType register(ResourceLocation id, TeaType teaType) {
        REGISTRY.put(id, teaType);
        DEFAULT_REGISTRY.put(id, teaType);
        return teaType;
    }

    private static TeaType register(String id, TeaType type) {
        return register(TeaTime.asId(id), type);
    }

    public static TeaType getFromIngredients(List<Item> stacks) {
        main: for (ResourceLocation i : REGISTRY.keySet()) {
            if (i.toString().equals("teatime:empty") || i.toString().equals("teatime:herbal_tea")) continue;
            TeaType type = REGISTRY.get(i);
            List<Item> wantedItems = new ArrayList<>(type.getIngredients());
            if (stacks.size() != wantedItems.size()) continue;
            for (Item item : stacks) {
                if (!wantedItems.remove(item)) continue main;
            }
            if (wantedItems.isEmpty()) return type;
        }
        return EMPTY;
    }

    public static TeaType get(ResourceLocation id) {
        if (!REGISTRY.containsKey(id)) {
            throw new IllegalArgumentException("No tea type with registered with id " + id);
        }
        return REGISTRY.get(id);
    }

    public static void send(ServerPlayer player) {

        Map<ResourceLocation, JsonObject> types = new HashMap<>();
        REGISTRY.forEach((identifier, type) -> types.put(identifier, type.toJson()));

        ServerPlayNetworking.send(player, new SyncCustomTeaTypesRegistererS2CPacket(types));

    }

    public static boolean containsId(ResourceLocation id) {
        return REGISTRY.containsKey(id);
    }

    public static ResourceLocation getId(TeaType teaType) {
        for (ResourceLocation i : REGISTRY.keySet()) {
            TeaType type = REGISTRY.get(i);
            if (type.equals(teaType)) {
                return i;
            }
        }
        return TeaTime.asId("empty");
    }

    public static Set<ResourceLocation> getIds() {
        return REGISTRY.keySet();
    }

    public static List<ResourceLocation> getOrderedIdListDisplayed() {
        Set<ResourceLocation> allIds = getIds();
        List<ResourceLocation> herbalTeas = new ArrayList<>();
        List<ResourceLocation> whiteTeas = new ArrayList<>();
        List<ResourceLocation> greenTeas = new ArrayList<>();
        List<ResourceLocation> blackTeas = new ArrayList<>();
        List<ResourceLocation> miscTeas = new ArrayList<>();
        for (ResourceLocation id : allIds) {
            TeaType type = get(id);
            if (type == EMPTY || type == HERBAL_TEA || type == WHITE_TEA
                    || type == GREEN_TEA || type == BLACK_TEA) continue;
            if (!(type instanceof CustomTeaType custom)) {
                miscTeas.add(id);
                continue;
            }
            if (custom.parent() == HERBAL_TEA) herbalTeas.add(id);
            else if (custom.parent() == WHITE_TEA) whiteTeas.add(id);
            else if (custom.parent() == GREEN_TEA) greenTeas.add(id);
            else if (custom.parent() == BLACK_TEA) blackTeas.add(id);
            else miscTeas.add(id);
        }
        Comparator<ResourceLocation> comparator = DrinkUtil.alphabetizer(ResourceLocation::toString);
        herbalTeas.sort(comparator);
        whiteTeas.sort(comparator);
        greenTeas.sort(comparator);
        blackTeas.sort(comparator);
        miscTeas.sort(comparator);
        whiteTeas.add(0, TeaTime.asId("white_tea"));
        greenTeas.add(0, TeaTime.asId("green_tea"));
        blackTeas.add(0, TeaTime.asId("black_tea"));
        return Streams.concat(herbalTeas.stream(), whiteTeas.stream(), greenTeas.stream(), blackTeas.stream())
                .toList();
    }

    public static void init() {}

    public static void resetRegistry() {
        REGISTRY.clear();
        REGISTRY.putAll(DEFAULT_REGISTRY);
    }

    static {
        EMPTY = register("empty", new TeaType(TeaTime.asId("empty"), 0xFFFFFF, List.of(), 0));
        HERBAL_TEA = register("herbal_tea", new TeaType(TeaTime.asId("herbal_tea"), 0xf7e48f, List.of(), 0));
        WHITE_TEA = register("white_tea", new TeaType(TeaTime.asId("white_tea"), 0xf7e48f, List.of(ModItems.WHITE_TEA_LEAVES), 10));
        GREEN_TEA = register("green_tea", new TeaType(TeaTime.asId("green_tea"), 0xd1b849, List.of(ModItems.DRIED_TEA_LEAVES), 30, new MobEffectInstance(MobEffects.DIG_SPEED, 20 * 60)));
        BLACK_TEA = register("black_tea", new TeaType(TeaTime.asId("black_tea"), 0x4d0705, List.of(ModItems.FERMENTED_TEA_LEAVES), 50, new MobEffectInstance(MobEffects.DIG_SPEED, 20 * 60),
                new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 60)));
    }

    @Override
    public ResourceLocation getFabricId() {
        return TeaTime.asId("custom_tea_types_registerer");
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        TeaTypeManager.resetRegistry();

        int i = 0;

        for (Map.Entry<ResourceLocation, Resource> entry : manager.listResources("custom_tea_types", id -> id.getPath().endsWith(".json")).entrySet()) {
            ResourceLocation id = new ResourceLocation(entry.getKey().getNamespace(),
                    entry.getKey().getPath()
                            .replace("custom_tea_types/", "")
                            .replace(".json", ""));

            try (InputStream stream = entry.getValue().open()) {
                JsonObject object = GsonHelper.parse(new InputStreamReader(stream));
                TeaTypeManager.register(id, loadFromJson(id, object));
                i++;
            } catch (Exception e) {
                TeaTime.logger.error("Could not load custom tea type {}", id, e);
            }
        }

        TeaTime.logger.info("Loaded {} custom tea types", i);
    }

    public static TeaType loadFromJson(ResourceLocation id, JsonObject object) {
        if (object.has("isParent")) {
            if (GsonHelper.getAsBoolean(object, "isParent")) return TeaTypeManager.get(new ResourceLocation(GsonHelper.getAsString(object, "parent")));
        }

        TeaType parent = TeaTypeManager.get(new ResourceLocation(GsonHelper.getAsString(object, "parent")));

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
