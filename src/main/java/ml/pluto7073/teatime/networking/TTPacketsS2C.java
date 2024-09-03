package ml.pluto7073.teatime.networking;

import ml.pluto7073.teatime.networking.packets.s2c.SyncCustomTeaTypesRegistererS2CPacket;
import ml.pluto7073.teatime.teatypes.TeaTypeManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.player.LocalPlayer;

import java.util.AbstractMap;

public class TTPacketsS2C {

    @Environment(EnvType.CLIENT)
    public static void register() {

        ClientPlayConnectionEvents.INIT.register((handler, client) ->
                ClientPlayNetworking.registerGlobalReceiver(SyncCustomTeaTypesRegistererS2CPacket.TYPE, TTPacketsS2C::receiveCustomTeaTypesList));

    }

    @Environment(EnvType.CLIENT)
    private static void receiveCustomTeaTypesList(SyncCustomTeaTypesRegistererS2CPacket packet, LocalPlayer player, PacketSender sender) {
        TeaTypeManager.resetRegistry();

        packet.teaTypes().entrySet().stream()
                .filter(entry -> !TeaTypeManager.containsId(entry.getKey()))
                .map(entry -> new AbstractMap.SimpleEntry<>(
                        entry.getKey(), TeaTypeManager.loadFromJson(entry.getKey(), entry.getValue())
                ))
                .forEach(entry -> TeaTypeManager.register(entry.getKey(), entry.getValue()));
    }

}
