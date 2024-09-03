package ml.pluto7073.teatime.action;

import com.google.gson.JsonObject;
import ml.pluto7073.pdapi.addition.action.OnDrinkAction;
import ml.pluto7073.pdapi.addition.action.OnDrinkSerializer;
import ml.pluto7073.teatime.teatypes.TeaType;
import ml.pluto7073.teatime.teatypes.TeaTypeManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class AddTeaEffectsAction implements OnDrinkAction {

    private final TeaType type;

    public AddTeaEffectsAction(TeaType type) {
        this.type = type;
    }

    @Override
    public void onDrink(ItemStack stack, Level level, LivingEntity user) {
        List<MobEffectInstance> list = List.of(type.getEffects());
        list.forEach(user::addEffect);
    }

    @Override
    public OnDrinkSerializer<?> serializer() {
        return TeaTimeActions.ADD_TEA_EFFECTS;
    }

    public static class Serializer implements OnDrinkSerializer<AddTeaEffectsAction> {

        @Override
        public AddTeaEffectsAction fromJson(JsonObject json) {
            ResourceLocation id = new ResourceLocation(GsonHelper.getAsString(json, "tea"));
            TeaType type = TeaTypeManager.get(id);
            return new AddTeaEffectsAction(type);
        }

        @Override
        public void toJson(JsonObject json, AddTeaEffectsAction action) {
            ResourceLocation id = TeaTypeManager.getId(action.type);
            json.addProperty("tea", id.toString());
        }

        @Override
        public AddTeaEffectsAction fromNetwork(FriendlyByteBuf buf) {
            TeaType type = TeaTypeManager.get(buf.readResourceLocation());
            return new AddTeaEffectsAction(type);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, AddTeaEffectsAction action) {
            ResourceLocation id = TeaTypeManager.getId(action.type);
            buf.writeResourceLocation(id);
        }
    }

}
