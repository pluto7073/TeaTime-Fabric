package ml.pluto7073.teatime.action;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ml.pluto7073.pdapi.addition.action.OnDrinkAction;
import ml.pluto7073.pdapi.addition.action.OnDrinkSerializer;
import ml.pluto7073.teatime.TTRegistries;
import ml.pluto7073.teatime.component.TTComponents;
import ml.pluto7073.teatime.teatypes.TeaType;
import ml.pluto7073.teatime.teatypes.TeaType.MobEffectTemplate;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class AddTeaEffectsAction implements OnDrinkAction {

    private final ResourceKey<TeaType> type;

    public AddTeaEffectsAction(ResourceKey<TeaType> type) {
        this.type = type;
    }

    @Override
    public void onDrink(ItemStack stack, Level level, LivingEntity user) {
        List<MobEffectTemplate> list = level.registryAccess().registryOrThrow(TTRegistries.TEA_TYPE)
                .getHolderOrThrow(type).value().getEffects(level.registryAccess());
        list.stream().map(MobEffectTemplate::instantiate).forEach(user::addEffect);
    }

    @Override
    public OnDrinkSerializer<?> serializer() {
        return TeaTimeActions.ADD_TEA_EFFECTS;
    }

    public static class Serializer implements OnDrinkSerializer<AddTeaEffectsAction> {

        public static final MapCodec<AddTeaEffectsAction> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(ResourceKey.codec(TTRegistries.TEA_TYPE).fieldOf("tea")
                                .forGetter(a -> a.type))
                        .apply(instance, AddTeaEffectsAction::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, AddTeaEffectsAction> STREAM_CODEC = StreamCodec.composite(
                ResourceKey.streamCodec(TTRegistries.TEA_TYPE), a -> a.type,
                AddTeaEffectsAction::new
        );

        @Override
        public MapCodec<AddTeaEffectsAction> codec() {
            return CODEC;
        }

    }

}
