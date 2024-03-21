package ml.pluto7073.teatime.teatypes;

import com.google.gson.JsonObject;
import ml.pluto7073.teatime.TeaTime;
import ml.pluto7073.teatime.item.ModItems;
import ml.pluto7073.teatime.networking.packets.s2c.SyncCustomTeaTypesRegistererS2CPacket;
import ml.pluto7073.teatime.networking.packets.s2c.SyncRollableRecipesRegistryS2CPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeaTypes {

    private static final Map<Identifier, TeaType> REGISTRY = new HashMap<>();
    private static final Map<Identifier, TeaType> DEFAULT_REGISTRY = new HashMap<>();

    public static final TeaType EMPTY;
    public static final TeaType HERBAL_TEA;
    public static final TeaType WHITE_TEA;
    public static final TeaType GREEN_TEA;
    public static final TeaType BLACK_TEA;

    public static TeaType register(Identifier id, TeaType teaType) {
        REGISTRY.put(id, teaType);
        DEFAULT_REGISTRY.put(id, teaType);
        return teaType;
    }

    private static TeaType register(String id, TeaType type) {
        return register(TeaTime.asId(id), type);
    }

    public static TeaType getFromIngredients(List<Item> stacks) {
        main: for (Identifier i : REGISTRY.keySet()) {
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

    public static TeaType get(Identifier id) {
        return REGISTRY.get(id);
    }

    public static void send(ServerPlayerEntity player) {

        Map<Identifier, JsonObject> types = new HashMap<>();
        REGISTRY.forEach((identifier, type) -> types.put(identifier, type.getAsJson()));

        ServerPlayNetworking.send(player, new SyncCustomTeaTypesRegistererS2CPacket(types));

    }

    public static boolean containsId(Identifier id) {
        return REGISTRY.containsKey(id);
    }

    public static Identifier getId(TeaType teaType) {
        for (Identifier i : REGISTRY.keySet()) {
            TeaType type = REGISTRY.get(i);
            if (type.equals(teaType)) {
                return i;
            }
        }
        return TeaTime.asId("empty");
    }

    public static void init() {}

    public static void resetRegistry() {
        REGISTRY.clear();
        REGISTRY.putAll(DEFAULT_REGISTRY);
    }

    static {
        EMPTY = register("empty", new TeaType(0xFFFFFF, List.of(), 0));
        HERBAL_TEA = register("herbal_tea", new TeaType(0xf7e48f, List.of(), 0));
        WHITE_TEA = register("white_tea", new TeaType(0xf7e48f, List.of(ModItems.WHITE_TEA_LEAVES), 10));
        GREEN_TEA = register("green_tea", new TeaType(0xd1b849, List.of(ModItems.DRIED_TEA_LEAVES), 30, new StatusEffectInstance(StatusEffects.HASTE, 20 * 60)));
        BLACK_TEA = register("black_tea", new TeaType(0x4d0705, List.of(ModItems.FERMENTED_TEA_LEAVES), 50, new StatusEffectInstance(StatusEffects.HASTE, 20 * 60),
                new StatusEffectInstance(StatusEffects.SPEED, 20 * 60)));
    }

}
