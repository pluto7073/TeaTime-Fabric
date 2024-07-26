package ml.pluto7073.teatime.teatypes;

import com.google.gson.JsonObject;
import ml.pluto7073.teatime.TeaTime;
import ml.pluto7073.teatime.item.ModItems;
import ml.pluto7073.teatime.networking.packets.s2c.SyncCustomTeaTypesRegistererS2CPacket;
import ml.pluto7073.teatime.networking.packets.s2c.SyncRollableRecipesRegistryS2CPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;

import java.util.*;

public class TeaTypes {

    private static final Map<ResourceLocation, TeaType> REGISTRY = new HashMap<>();
    private static final Map<ResourceLocation, TeaType> DEFAULT_REGISTRY = new HashMap<>();

    public static final TeaType EMPTY;
    public static final TeaType HERBAL_TEA;
    public static final TeaType WHITE_TEA;
    public static final TeaType GREEN_TEA;
    public static final TeaType BLACK_TEA;

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
        return REGISTRY.get(id);
    }

    public static void send(ServerPlayer player) {

        Map<ResourceLocation, JsonObject> types = new HashMap<>();
        REGISTRY.forEach((identifier, type) -> types.put(identifier, type.getAsJson()));

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

}
