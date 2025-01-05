package ml.pluto7073.teatime.networking;

import ml.pluto7073.teatime.networking.packets.clientbound.ClientboundSyncCustomTeaTypesPacket;
import ml.pluto7073.teatime.teatypes.TeaTypeManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.player.LocalPlayer;

import java.util.AbstractMap;

public class ClientboundTTPackets {

    @Environment(EnvType.CLIENT)
    public static void register() {

        ClientPlayConnectionEvents.INIT.register((handler, client) ->
                ClientPlayNetworking.registerGlobalReceiver(ClientboundSyncCustomTeaTypesPacket.TYPE, ClientboundTTPackets::receiveCustomTeaTypesList));

    }

    @Environment(EnvType.CLIENT)
    private static void receiveCustomTeaTypesList(ClientboundSyncCustomTeaTypesPacket packet, LocalPlayer player, PacketSender sender) {
        TeaTypeManager.resetRegistry();

        packet.teaTypes().forEach(TeaTypeManager::register);
    }

}
