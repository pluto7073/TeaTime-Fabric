package ml.pluto7073.teatime.recipe;

import com.google.common.collect.Maps;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.PDRegistries;
import ml.pluto7073.pdapi.addition.action.OnDrinkAction;
import ml.pluto7073.pdapi.addition.action.OnDrinkSerializer;
import ml.pluto7073.pdapi.addition.chemicals.ConsumableChemicalRegistry;
import ml.pluto7073.pdapi.networking.NetworkingUtils;
import ml.pluto7073.pdapi.specialty.SpecialtyDrink;
import ml.pluto7073.teatime.TeaTime;
import ml.pluto7073.teatime.item.ModItems;
import ml.pluto7073.teatime.teatypes.TeaType;
import ml.pluto7073.teatime.teatypes.TeaTypeManager;
import ml.pluto7073.teatime.utils.TeaTimeUtils;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@MethodsReturnNonnullByDefault
public class TeaBaseSpecialtyDrink extends SpecialtyDrink {

    private final TeaType type;

    public TeaBaseSpecialtyDrink(ResourceLocation id, TeaType type, ResourceLocation[] steps, OnDrinkAction[] actions, int color, HashMap<String, Integer> chemicals, @Nullable String name) {
        super(id, ModItems.TEA, steps, actions, color, chemicals, name);
        this.type = type;
    }

    @Override
    public ResourceLocation type() {
        return TeaTime.asId("tea_base_specialty_drink");
    }

    @Override
    public ItemStack baseAsStack() {
        return TeaTimeUtils.setTeaType(new ItemStack(ModItems.TEA), type);
    }

    public static TeaBaseSpecialtyDrink fromJson(ResourceLocation id, JsonObject json) {
        TeaType base = TeaTypeManager.get(new ResourceLocation(GsonHelper.getAsString(json, "tea")));
        List<ResourceLocation> additions = Streams.stream(GsonHelper.getAsJsonArray(json, "additions")).map(JsonElement::getAsString).map(ResourceLocation::new).toList();
        HashMap<String, Integer> chemicals = new HashMap<>();
        ConsumableChemicalRegistry.forEach(handler -> {
            if (json.has(handler.getName())) {
                chemicals.put(handler.getName(), GsonHelper.getAsInt(json, handler.getName()));
            }
        });
        int color = GsonHelper.getAsInt(json, "color");
        JsonArray actionsArray = GsonHelper.getAsJsonArray(json, "onDrinkActions");
        List<OnDrinkAction> actions = new ArrayList<>();
        for (JsonElement e : actionsArray) {
            if (!e.isJsonObject()) {
                PDAPI.LOGGER.warn("Non-JsonObject item in 'onDrinkActions' in Specialty file: {}", id);
                continue;
            }
            JsonObject actionObject = e.getAsJsonObject();
            ResourceLocation type = new ResourceLocation(GsonHelper.getAsString(actionObject, "type"));
            @SuppressWarnings("unchecked")
            OnDrinkSerializer<OnDrinkAction> serializer = (OnDrinkSerializer<OnDrinkAction>)
                    PDRegistries.ON_DRINK_SERIALIZER.get(type);
            if (serializer == null) throw new IllegalArgumentException("Unknown OnDrinkAction " + type);
            actions.add(serializer.fromJson(actionObject));
        }
        String name = null;
        if (json.has("name")) {
            name = GsonHelper.getAsString(json, "name");
        }
        return new TeaBaseSpecialtyDrink(
                id, base,
                additions.toArray(ResourceLocation[]::new),
                actions.toArray(OnDrinkAction[]::new),
                color, chemicals, name
        );
    }

    public static TeaBaseSpecialtyDrink fromNetwork(FriendlyByteBuf buf) {
        ResourceLocation id = buf.readResourceLocation();
        ResourceLocation base = buf.readResourceLocation();
        ResourceLocation[] steps = NetworkingUtils.listFromNetwork(buf, FriendlyByteBuf::readResourceLocation).toArray(new ResourceLocation[0]);
        HashMap<String, Integer> chemicals = Maps.newHashMap(buf.readMap(FriendlyByteBuf::readUtf, FriendlyByteBuf::readInt));
        int color = buf.readInt();
        List<OnDrinkAction> list = NetworkingUtils.readDrinkActionsList(buf);
        String name = buf.readUtf();
        return new TeaBaseSpecialtyDrink(id, TeaTypeManager.get(base), steps, list.toArray(OnDrinkAction[]::new), color, chemicals, name);
    }

    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeResourceLocation(id());
        buf.writeResourceLocation(TeaTypeManager.getId(type));
        NetworkingUtils.arrayToNetwork(buf, steps(), FriendlyByteBuf::writeResourceLocation);
        buf.writeMap(chemicals(), FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeInt);
        buf.writeInt(color());
        NetworkingUtils.writeDrinkActionsList(buf, actions());
        buf.writeUtf(name());
    }

}
