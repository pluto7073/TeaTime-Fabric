package ml.pluto7073.teatime.component;

import ml.pluto7073.teatime.TeaTime;
import ml.pluto7073.teatime.item.KettleData;
import ml.pluto7073.teatime.teatypes.TeaType;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.function.UnaryOperator;

public class TTComponents {

    public static final DataComponentType<Holder<TeaType>> TEA_TYPE = register("tea_type", builder ->
            builder.persistent(TeaType.COMPONENT_CODEC).networkSynchronized(TeaType.COMPONENT_STREAM_CODEC));
    public static final DataComponentType<KettleData> KETTLE_DATA = register("kettle_data", builder ->
            builder.persistent(KettleData.CODEC).networkSynchronized(KettleData.STREAM_CODEC));
    public static final DataComponentType<ResourceLocation> ROLLED_TEA_LEAVES_SOURCE = register("rolled_tea_leaves_source", builder ->
            builder.persistent(ResourceLocation.CODEC).networkSynchronized(ResourceLocation.STREAM_CODEC));


    private static <T> DataComponentType<T> register(String id, UnaryOperator<DataComponentType.Builder<T>> operator) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, TeaTime.asId(id), (operator.apply(DataComponentType.builder())).build());
    }

    public static void init() {}

}
