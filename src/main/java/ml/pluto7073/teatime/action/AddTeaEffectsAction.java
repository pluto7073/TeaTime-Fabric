package ml.pluto7073.teatime.action;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ml.pluto7073.pdapi.addition.action.OnDrinkAction;
import ml.pluto7073.pdapi.addition.action.OnDrinkSerializer;
import ml.pluto7073.teatime.TeaTime;
import ml.pluto7073.teatime.teatypes.TeaType;
import ml.pluto7073.teatime.teatypes.TeaTypeManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffectInstance;
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
        List<MobEffectInstance> list = TeaTypeManager.get(type).getEffects();
        list.forEach(user::addEffect);
    }

    @Override
    public OnDrinkSerializer<?> serializer() {
        return TeaTimeActions.ADD_TEA_EFFECTS;
    }

    public static class Serializer implements OnDrinkSerializer<AddTeaEffectsAction> {

        public static final Codec<AddTeaEffectsAction> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(ResourceKey.codec(TeaTypeManager.TEA_TYPE).fieldOf("tea")
                                .forGetter(a -> a.type))
                        .apply(instance, AddTeaEffectsAction::new));

        @Override
        public Codec<AddTeaEffectsAction> codec() {
            return CODEC;
        }

        @Override
        public AddTeaEffectsAction fromNetwork(FriendlyByteBuf buf) {
            return new AddTeaEffectsAction(buf.readResourceKey(TeaTypeManager.TEA_TYPE));
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, AddTeaEffectsAction action) {
            buf.writeResourceKey(action.type);
        }
    }

}
