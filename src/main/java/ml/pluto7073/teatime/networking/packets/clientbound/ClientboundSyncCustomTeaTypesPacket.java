package ml.pluto7073.teatime.networking.packets.clientbound;

import ml.pluto7073.teatime.TeaTime;
import ml.pluto7073.teatime.teatypes.TeaType;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.Map;

public record ClientboundSyncCustomTeaTypesPacket(Map<ResourceLocation, TeaType> teaTypes, List<ResourceLocation> ingredients) implements FabricPacket {

    public static final PacketType<ClientboundSyncCustomTeaTypesPacket> TYPE = PacketType.create(
            TeaTime.asId("clientbound/sync_custom_tea_types"), ClientboundSyncCustomTeaTypesPacket::read
    );

    private static ClientboundSyncCustomTeaTypesPacket read(FriendlyByteBuf buf) {
        return new ClientboundSyncCustomTeaTypesPacket(buf.readMap(FriendlyByteBuf::readResourceLocation, TeaType::fromNetwork), buf.readList(FriendlyByteBuf::readResourceLocation));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeMap(teaTypes, FriendlyByteBuf::writeResourceLocation, (b, type) -> type.toNetwork(b));
        buf.writeCollection(ingredients, FriendlyByteBuf::writeResourceLocation);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

}
