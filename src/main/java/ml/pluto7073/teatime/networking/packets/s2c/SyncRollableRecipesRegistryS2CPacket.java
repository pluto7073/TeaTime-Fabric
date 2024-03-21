package ml.pluto7073.teatime.networking.packets.s2c;

import com.google.gson.JsonObject;
import ml.pluto7073.pdapi.networking.NetworkingUtils;
import ml.pluto7073.teatime.TeaTime;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.Map;

public record SyncRollableRecipesRegistryS2CPacket(Map<Identifier, JsonObject> recipes) implements FabricPacket {

    public static final PacketType<SyncRollableRecipesRegistryS2CPacket> TYPE = PacketType.create(
            TeaTime.asId("s2c/sync_rollables_registry"), SyncRollableRecipesRegistryS2CPacket::read
    );

    private static SyncRollableRecipesRegistryS2CPacket read(PacketByteBuf buf) {
        return new SyncRollableRecipesRegistryS2CPacket(buf.readMap(PacketByteBuf::readIdentifier, NetworkingUtils::readJsonObject));
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeMap(recipes, PacketByteBuf::writeIdentifier, NetworkingUtils::writeJsonObjectStart);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
