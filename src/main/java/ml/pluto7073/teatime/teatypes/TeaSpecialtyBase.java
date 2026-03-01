package ml.pluto7073.teatime.teatypes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ml.pluto7073.pdapi.PDRegistries;
import ml.pluto7073.pdapi.specialty.SpecialtyDrinkBase;
import ml.pluto7073.pdapi.specialty.SpecialtyDrinkBaseSerializer;
import ml.pluto7073.teatime.TeaTime;
import ml.pluto7073.teatime.item.TTItems;
import ml.pluto7073.teatime.utils.TeaTimeUtils;
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
    public ItemStack buildItemStack() {
        return TeaTimeUtils.setTeaType(new ItemStack(TTItems.TEA), teaType);
    }

    @Override
    public boolean matches(ItemStack stack) {
        return TeaTimeUtils.getTeaTypeId(stack).equals(teaType.location());
    }

    @Override
    public SpecialtyDrinkBaseSerializer serializer() {
        return INSTANCE;
    }

    public static void init() {
        Registry.register(PDRegistries.SPECIALTY_DRINK_BASE, TeaTime.asId("tea"), INSTANCE);
    }

    public static class TeaBaseSerializer implements SpecialtyDrinkBaseSerializer {

        public static final Codec<TeaSpecialtyBase> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(ResourceKey.codec(TeaTypeManager.TEA_TYPE).fieldOf("tea")
                        .forGetter(base -> base.teaType))
                        .apply(instance, TeaSpecialtyBase::new));

        @Override
        public Codec<? extends SpecialtyDrinkBase> codec() {
            return CODEC;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, SpecialtyDrinkBase base) {
            if (!(base instanceof TeaSpecialtyBase tea)) return;
            buf.writeResourceKey(tea.teaType);
        }

        @Override
        public SpecialtyDrinkBase fromNetwork(FriendlyByteBuf buf) {
            ResourceKey<TeaType> tea = buf.readResourceKey(TeaTypeManager.TEA_TYPE);
            return new TeaSpecialtyBase(tea);
        }
    }

}
