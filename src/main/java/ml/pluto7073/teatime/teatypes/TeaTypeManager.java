package ml.pluto7073.teatime.teatypes;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Streams;
import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import ml.pluto7073.pdapi.util.DrinkUtil;
import ml.pluto7073.teatime.TeaTime;
import ml.pluto7073.teatime.networking.packets.clientbound.ClientboundSyncCustomTeaTypesPacket;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class TeaTypeManager implements SimpleSynchronousResourceReloadListener {

    public static final ResourceLocation PHASE = TeaTime.asId("phase/tea_types");

    public static final ResourceKey<Registry<TeaType>> TEA_TYPE =
            ResourceKey.createRegistryKey(TeaTime.asId("tea_type"));

    public static final ResourceKey<TeaType> EMPTY = baseType("empty");
    public static final ResourceKey<TeaType> HERBAL_TEA = baseType("herbal_tea");
    public static final ResourceKey<TeaType> WHITE_TEA = baseType("white_tea");
    public static final ResourceKey<TeaType> GREEN_TEA = baseType("green_tea");
    public static final ResourceKey<TeaType> BLACK_TEA = baseType("black_tea");

    private static final Map<ResourceLocation, TeaType> REGISTRY = new HashMap<>();

    public static final TeaType EMPTY_TYPE = new TeaType(Optional.empty(), 0,
            List.of(), 0, List.of(), true, "");

    public TeaTypeManager() {
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(PHASE, (player, joined) -> TeaTypeManager.send(player));
    }

    private static ResourceKey<TeaType> baseType(String id) {
        return ResourceKey.create(TEA_TYPE, TeaTime.asId(id));
    }

    public static TeaType register(ResourceLocation id, TeaType teaType) {
        REGISTRY.put(id, teaType);
        return teaType;
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
        return EMPTY_TYPE;
    }

    public static TeaType get(ResourceKey<TeaType> key) {
        return get(key.location());
    }

    public static TeaType get(ResourceLocation id) {
        if (!REGISTRY.containsKey(id)) {
            return EMPTY_TYPE;
        }
        return REGISTRY.get(id);
    }

    public static void send(ServerPlayer player) {

        ServerPlayNetworking.send(player,
                new ClientboundSyncCustomTeaTypesPacket(ImmutableMap.copyOf(REGISTRY)));

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
        return new ResourceLocation("empty");
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
            if (type.internal()) continue;
            if (type.parent().isEmpty()) {
                miscTeas.add(id);
                continue;
            }
            if (type.parent().equals(Optional.of(HERBAL_TEA))) herbalTeas.add(id);
            else if (type.parent().equals(Optional.of(WHITE_TEA))) whiteTeas.add(id);
            else if (type.parent().equals(Optional.of(GREEN_TEA))) greenTeas.add(id);
            else if (type.parent().equals(Optional.of(BLACK_TEA))) blackTeas.add(id);
            else miscTeas.add(id);
        }
        Comparator<ResourceLocation> comparator = DrinkUtil.alphabetizer(ResourceLocation::toString);
        herbalTeas.sort(comparator);
        whiteTeas.sort(comparator);
        greenTeas.sort(comparator);
        blackTeas.sort(comparator);
        miscTeas.sort(comparator);
        whiteTeas.add(0, WHITE_TEA.location());
        greenTeas.add(0, GREEN_TEA.location());
        blackTeas.add(0, BLACK_TEA.location());
        return Streams.concat(herbalTeas.stream(), whiteTeas.stream(), greenTeas.stream(), blackTeas.stream())
                .toList();
    }

    public static List<TeaType> values() {
        return REGISTRY.values().stream().toList();
    }

    public static void init() {}

    public static void resetRegistry() {
        REGISTRY.clear();
    }

    @Override
    public ResourceLocation getFabricId() {
        return TeaTime.asId("tea_type_registerer");
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        TeaTypeManager.resetRegistry();

        int i = 0;

        for (Map.Entry<ResourceLocation, Resource> entry : manager.listResources("tea_types", id -> id.getPath().endsWith(".json")).entrySet()) {
            ResourceLocation id = new ResourceLocation(entry.getKey().getNamespace(),
                    entry.getKey().getPath()
                            .replace("tea_types/", "")
                            .replace(".json", ""));

            try (InputStream stream = entry.getValue().open()) {
                JsonObject object = GsonHelper.parse(new InputStreamReader(stream));

                if (object.has("fabric:load_conditions")) {
                    boolean b = ResourceConditions.conditionsMatch(
                            GsonHelper.getAsJsonArray(object, "fabric:load_conditions"),
                            true
                    );

                    if (!b) continue;
                }

                TeaTypeManager.register(id, TeaType.CODEC.parse(JsonOps.INSTANCE, object).getOrThrow(false, s -> {
                    throw new JsonSyntaxException(s);
                }));
                i++;
            } catch (Exception e) {
                TeaTime.logger.error("Could not load custom tea type {}", id, e);
            }
        }

        TeaTime.logger.info("Loaded {} custom tea types", i);
    }

}
