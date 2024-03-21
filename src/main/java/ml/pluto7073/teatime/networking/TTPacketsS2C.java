package ml.pluto7073.teatime.networking;

import ml.pluto7073.teatime.ModServerResourceManager;
import ml.pluto7073.teatime.TeaTime;
import ml.pluto7073.teatime.event.CustomTeaTypesRegisterer;
import ml.pluto7073.teatime.networking.packets.s2c.SyncCustomTeaTypesRegistererS2CPacket;
import ml.pluto7073.teatime.networking.packets.s2c.SyncRollableRecipesRegistryS2CPacket;
import ml.pluto7073.teatime.teatypes.TeaType;
import ml.pluto7073.teatime.teatypes.TeaTypes;
import ml.pluto7073.teatime.utils.RollableTeaLeavesUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.AbstractMap;
import java.util.function.Predicate;

public class TTPacketsS2C {

    @Environment(EnvType.CLIENT)
    public static void register() {

        ClientPlayConnectionEvents.INIT.register((handler, client) ->
                ClientPlayNetworking.registerGlobalReceiver(SyncRollableRecipesRegistryS2CPacket.TYPE, TTPacketsS2C::receiveRollablesList));

        ClientPlayConnectionEvents.INIT.register((handler, client) ->
                ClientPlayNetworking.registerGlobalReceiver(SyncCustomTeaTypesRegistererS2CPacket.TYPE, TTPacketsS2C::receiveCustomTeaTypesList));

    }

    @Environment(EnvType.CLIENT)
    private static void receiveRollablesList(SyncRollableRecipesRegistryS2CPacket packet, ClientPlayerEntity player, PacketSender sender) {
        RollableTeaLeavesUtil.resetRegistry();

        packet.recipes().entrySet().stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(
                    entry.getKey(), ModServerResourceManager.loadRecipeFromJson(entry.getValue())
                ))
                .filter(entry -> !RollableTeaLeavesUtil.contains(entry.getKey()))
                .forEach(entry -> RollableTeaLeavesUtil.register(entry.getKey(), entry.getValue()));
    }

    @Environment(EnvType.CLIENT)
    private static void receiveCustomTeaTypesList(SyncCustomTeaTypesRegistererS2CPacket packet, ClientPlayerEntity player, PacketSender sender) {
        TeaTypes.resetRegistry();

        packet.teaTypes().entrySet().stream()
                .filter(entry -> !TeaTypes.containsId(entry.getKey()))
                .map(entry -> new AbstractMap.SimpleEntry<>(
                        entry.getKey(), CustomTeaTypesRegisterer.loadFromJson(entry.getValue())
                ))
                .forEach(entry -> TeaTypes.register(entry.getKey(), entry.getValue()));
    }

}
