package ml.pluto7073.teatime.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import ml.pluto7073.teatime.teatypes.TeaType;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record KettleData(
        int temperature,
        List<ItemStack> items,
        List<ItemStack> insertedItems,
        int waterAmount,
        Holder<TeaType> teaType) {

    public static final Codec<KettleData> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Codec.INT.fieldOf("temperature").forGetter(KettleData::temperature),
                    Codec.list(ItemStack.OPTIONAL_CODEC).fieldOf("items").forGetter(KettleData::items),
                    Codec.list(ItemStack.OPTIONAL_CODEC).fieldOf("insertedItems").forGetter(KettleData::insertedItems),
                    Codec.INT.fieldOf("waterAmount").forGetter(KettleData::waterAmount),
                    TeaType.COMPONENT_CODEC.optionalFieldOf("teaType", TeaType.EMPTY).forGetter(KettleData::teaType))
            .apply(instance, KettleData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, KettleData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, KettleData::temperature,
            ItemStack.OPTIONAL_LIST_STREAM_CODEC, KettleData::items,
            ItemStack.OPTIONAL_LIST_STREAM_CODEC, KettleData::insertedItems,
            ByteBufCodecs.INT, KettleData::waterAmount,
            TeaType.COMPONENT_STREAM_CODEC, KettleData::teaType,
            KettleData::new
    );

    public static final KettleData EMPTY = new KettleData(15, List.of(), List.of(), 0, TeaType.EMPTY);

    public KettleData copyWithWater(int waterAmount) {
        return new KettleData(temperature, items, insertedItems, waterAmount, teaType);
    }

}
