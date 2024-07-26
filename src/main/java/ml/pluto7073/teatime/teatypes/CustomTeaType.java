package ml.pluto7073.teatime.teatypes;

import com.google.gson.JsonObject;
import ml.pluto7073.teatime.utils.TeaTimeUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;

import java.util.List;

public class CustomTeaType extends TeaType {

    private final JsonObject data;
    public CustomTeaType(ResourceLocation id, TeaType base, int color, List<Item> ingredients, List<MobEffectInstance> effects, JsonObject data) {
        super(id, color, TeaTimeUtils.create(() -> {
            ingredients.addAll(base.getIngredients());
            return ingredients;
        }), base.getCaffeine(), TeaTimeUtils.create(() -> {
            effects.addAll(List.of(base.getEffects()));
            return effects.toArray(new MobEffectInstance[0]);
        }));
        this.data = data;
    }

    @Override
    public JsonObject getAsJson() {
        return data;
    }
}
