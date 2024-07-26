package ml.pluto7073.teatime.networking.packets.s2c;

import com.google.gson.JsonObject;
import ml.pluto7073.pdapi.networking.NetworkingUtils;
import ml.pluto7073.teatime.TeaTime;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public record SyncRollableRecipesRegistryS2CPacket(Map<ResourceLocation, JsonObject> recipes) implements FabricPacket {

    public static final PacketType<SyncRollableRecipesRegistryS2CPacket> TYPE = PacketType.create(
            TeaTime.asId("s2c/sync_rollables_registry"), SyncRollableRecipesRegistryS2CPacket::read
    );

    private static SyncRollableRecipesRegistryS2CPacket read(FriendlyByteBuf buf) {
        return new SyncRollableRecipesRegistryS2CPacket(buf.readMap(FriendlyByteBuf::readResourceLocation, NetworkingUtils::readJsonObject));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeMap(recipes, FriendlyByteBuf::writeResourceLocation, NetworkingUtils::writeJsonObjectStart);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
