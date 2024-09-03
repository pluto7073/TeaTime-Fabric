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

    final ResourceLocation id;
    protected final List<Item> ingredients;
    protected final int colour;
    protected final int caffeine;
    protected final MobEffectInstance[] effects;

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
        ResourceLocation id = TeaTypeManager.getId(this);
        return id.toLanguageKey("teatype");
    }

    public int getCaffeine() {
        return this.caffeine;
    }

    public MobEffectInstance[] getEffects() {
        return effects;
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("parent", TeaTypeManager.getId(this).toString());
        object.addProperty("isParent", true);
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
