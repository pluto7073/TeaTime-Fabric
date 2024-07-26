package ml.pluto7073.teatime.networking.packets.s2c;

import com.google.gson.JsonObject;
import ml.pluto7073.pdapi.networking.NetworkingUtils;
import ml.pluto7073.teatime.TeaTime;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public record SyncCustomTeaTypesRegistererS2CPacket(Map<ResourceLocation, JsonObject> teaTypes) implements FabricPacket {

    public static final PacketType<SyncCustomTeaTypesRegistererS2CPacket> TYPE = PacketType.create(
            TeaTime.asId("s2c/sync_custom_tea_types"), SyncCustomTeaTypesRegistererS2CPacket::read
    );

    private static SyncCustomTeaTypesRegistererS2CPacket read(FriendlyByteBuf buf) {
        return new SyncCustomTeaTypesRegistererS2CPacket(buf.readMap(FriendlyByteBuf::readResourceLocation, NetworkingUtils::readJsonObject));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeMap(teaTypes, FriendlyByteBuf::writeResourceLocation, NetworkingUtils::writeJsonObjectStart);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

}
