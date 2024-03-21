package ml.pluto7073.teatime.networking.packets.s2c;

import com.google.gson.JsonObject;
import ml.pluto7073.pdapi.networking.NetworkingUtils;
import ml.pluto7073.teatime.TeaTime;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.Map;

public record SyncCustomTeaTypesRegistererS2CPacket(Map<Identifier, JsonObject> teaTypes) implements FabricPacket {

    public static final PacketType<SyncCustomTeaTypesRegistererS2CPacket> TYPE = PacketType.create(
            TeaTime.asId("s2c/sync_custom_tea_types"), SyncCustomTeaTypesRegistererS2CPacket::read
    );

    private static SyncCustomTeaTypesRegistererS2CPacket read(PacketByteBuf buf) {
        return new SyncCustomTeaTypesRegistererS2CPacket(buf.readMap(PacketByteBuf::readIdentifier, NetworkingUtils::readJsonObject));
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeMap(teaTypes, PacketByteBuf::writeIdentifier, NetworkingUtils::writeJsonObjectStart);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

}
