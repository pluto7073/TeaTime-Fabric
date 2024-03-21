package ml.pluto7073.teatime.teatypes;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import ml.pluto7073.teatime.TeaTime;
import ml.pluto7073.teatime.item.ModItems;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeaType {

    public static final Map<Identifier, TeaType> TYPES = new HashMap<>();

    private final List<Item> ingredients;
    private final int colour;
    private final int caffeine;
    private final StatusEffectInstance[] effects;

    public TeaType(int colour, List<Item> ingredients, int caffeine, StatusEffectInstance... effects) {
        this.colour = colour;
        this.ingredients = ingredients;
        this.effects = effects;
        this.caffeine = caffeine;
    }

    public List<Item> getIngredients() {
        return ingredients;
    }

    public int getColour() {
        return colour;
    }

    public String getTranslationKey() {
        Identifier id = TeaTypes.getId(this);
        return "teatype." + id.getNamespace() + "." + id.getPath();
    }

    public int getCaffeine() {
        return this.caffeine;
    }

    public StatusEffectInstance[] getEffects() {
        return effects;
    }

    public JsonObject getAsJson() {
        JsonObject object = new JsonObject();
        object.add("parent", new JsonPrimitive(TeaTypes.getId(this).toString()));
        object.add("isParent", new JsonPrimitive(true));
        return object;
    }

}
