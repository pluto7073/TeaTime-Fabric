package ml.pluto7073.teatime.teatypes;

import com.google.gson.JsonObject;
import ml.pluto7073.teatime.utils.TeaTimeUtils;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;

import java.util.List;

public class CustomTeaType extends TeaType {

    private final JsonObject data;
    public CustomTeaType(TeaType base, int color, List<Item> ingredients, List<StatusEffectInstance> effects, JsonObject data) {
        super(color, TeaTimeUtils.create(() -> {
            ingredients.addAll(base.getIngredients());
            return ingredients;
        }), base.getCaffeine(), TeaTimeUtils.create(() -> {
            effects.addAll(List.of(base.getEffects()));
            return effects.toArray(new StatusEffectInstance[0]);
        }));
        this.data = data;
    }

    @Override
    public JsonObject getAsJson() {
        return data;
    }
}
