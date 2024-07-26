package ml.pluto7073.teatime.teatypes;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeaType {

    public static final Map<ResourceLocation, TeaType> TYPES = new HashMap<>();

    final ResourceLocation id;
    private final List<Item> ingredients;
    private final int colour;
    private final int caffeine;
    private final MobEffectInstance[] effects;

    public TeaType(ResourceLocation id, int colour, List<Item> ingredients, int caffeine, MobEffectInstance... effects) {
        this.colour = colour;
        this.ingredients = ingredients;
        this.effects = effects;
        this.caffeine = caffeine;
        this.id = id;
    }

    public List<Item> getIngredients() {
        return ingredients;
    }

    public int getColour() {
        return colour;
    }

    public String getTranslationKey() {
        ResourceLocation id = TeaTypes.getId(this);
        return "teatype." + id.getNamespace() + "." + id.getPath();
    }

    public int getCaffeine() {
        return this.caffeine;
    }

    public MobEffectInstance[] getEffects() {
        return effects;
    }

    public JsonObject getAsJson() {
        JsonObject object = new JsonObject();
        object.add("parent", new JsonPrimitive(TeaTypes.getId(this).toString()));
        object.add("isParent", new JsonPrimitive(true));
        return object;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TeaType type)) return false;

        if (type == this) return true;

        if (type.hashCode() == this.hashCode()) return true;

        return this.id == type.id;
    }
}
