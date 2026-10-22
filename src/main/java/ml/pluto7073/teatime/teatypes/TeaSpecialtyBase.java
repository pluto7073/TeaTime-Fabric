package ml.pluto7073.teatime.teatypes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ml.pluto7073.pdapi.PDRegistries;
import ml.pluto7073.pdapi.specialty.SpecialtyDrinkBase;
import ml.pluto7073.pdapi.specialty.SpecialtyDrinkBaseSerializer;
import ml.pluto7073.teatime.TTRegistries;
import ml.pluto7073.teatime.TeaTime;
import ml.pluto7073.teatime.component.TTComponents;
import ml.pluto7073.teatime.item.TTItems;
import ml.pluto7073.teatime.utils.TeaTimeUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;

public class TeaSpecialtyBase implements SpecialtyDrinkBase {

    public static final TeaBaseSerializer INSTANCE = new TeaBaseSerializer();

    private final ResourceKey<TeaType> teaType;

    public TeaSpecialtyBase(ResourceKey<TeaType> teaType) {
        this.teaType = teaType;
    }

    @Override
    public ItemStack buildItemStack(HolderLookup.Provider provider) {
        ItemStack stack = new ItemStack(TTItems.TEA);
        stack.set(TTComponents.TEA_TYPE, provider.lookupOrThrow(TTRegistries.TEA_TYPE).getOrThrow(teaType));
        return stack;
    }

    @Override
    public boolean matches(ItemStack stack) {
        return stack.getOrDefault(TTComponents.TEA_TYPE, TeaType.EMPTY).unwrapKey()
                .map(key -> key.equals(teaType)).orElse(false);
    }

    @Override
    public SpecialtyDrinkBaseSerializer serializer() {
        return INSTANCE;
    }

    public static void init() {
        Registry.register(PDRegistries.SPECIALTY_DRINK_BASE, TeaTime.asId("tea"), INSTANCE);
    }

    public static class TeaBaseSerializer implements SpecialtyDrinkBaseSerializer {

        public static final MapCodec<TeaSpecialtyBase> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(ResourceKey.codec(TTRegistries.TEA_TYPE).fieldOf("tea")
                        .forGetter(base -> base.teaType))
                        .apply(instance, TeaSpecialtyBase::new));

        @Override
        public MapCodec<? extends SpecialtyDrinkBase> codec() {
            return CODEC;
        }

    }

}
