package ml.pluto7073.teatime.teatypes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ml.pluto7073.pdapi.networking.NetworkingUtils;
import ml.pluto7073.teatime.utils.TeaTimeUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.apache.commons.compress.utils.Lists;

import java.util.*;

public class TeaType {

    public static final Codec<TeaType> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Codec.optionalField("parent", ResourceKey.codec(TeaTypeManager.TEA_TYPE))
                            .forGetter(type -> type.parent),
                            Codec.INT.fieldOf("color").orElse(0).forGetter(type -> type.colour),
                            Codec.list(BuiltInRegistries.ITEM.byNameCodec()).fieldOf("ingredients")
                                    .forGetter(type -> type.ingredients),
                            Codec.INT.fieldOf("caffeine").orElse(0)
                                    .forGetter(type -> type.caffeine),
                            Codec.list(TeaTimeUtils.MOB_EFFECT_CODEC).fieldOf("effects")
                                    .forGetter(type -> type.effects),
                            Codec.BOOL.fieldOf("internal").orElse(false)
                                    .forGetter(TeaType::internal),
                            Codec.STRING.fieldOf("name").orElse("")
                                    .forGetter(type -> type.name))
                    .apply(instance, TeaType::new));

    private final Optional<ResourceKey<TeaType>> parent;
    private final List<Item> ingredients;
    private final int colour;
    private final int caffeine;
    private final List<MobEffectInstance> effects;
    private final boolean internal;
    private final String name;

    public TeaType(Optional<ResourceKey<TeaType>> parent, int colour, List<Item> ingredients, int caffeine, List<MobEffectInstance> effects, boolean internal, String name) {
        if (parent.isPresent() && internal)
            throw new IllegalStateException("Internal TeaType's can't have parents");
        this.parent = parent;
        this.colour = colour;
        this.ingredients = ingredients;
        this.effects = effects;
        this.caffeine = caffeine;
        this.internal = internal;
        this.name = name;
    }

    public Optional<ResourceKey<TeaType>> parent() {
        return parent;
    }

    public boolean internal() {
        return internal;
    }

    public List<Item> getIngredients(TeaTypeManager manager) {
        ArrayList<Item> list = Lists.newArrayList(this.ingredients.iterator());
        parent.ifPresent(type ->
                list.addAll(manager.get(type).getIngredients(manager)));
        return list;
    }

    public int getColour(Level level) {
        return parent.isPresent() && colour == 0 ? level.getTeaTypeManager().get(parent.get()).colour : colour;
    }

    public String getTranslationKey(Level level) {
        if (name != null && !name.isEmpty()) return name;
        ResourceLocation id = level.getTeaTypeManager().getId(this);
        return id.toLanguageKey("tea_type");
    }

    public int getCaffeine(Level level) {
        return parent.map(teaTypeResourceKey -> level.getTeaTypeManager().get(teaTypeResourceKey).caffeine)
                .orElse(caffeine);
    }

    public List<MobEffectInstance> getEffects(Level level) {
        ArrayList<MobEffectInstance> list = Lists.newArrayList(this.effects.iterator());
        parent.ifPresent(teaTypeResourceKey ->
                list.addAll(level.getTeaTypeManager().get(teaTypeResourceKey).getEffects(level)));
        return list;
    }

    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeOptional(parent, FriendlyByteBuf::writeResourceKey);
        buf.writeInt(colour);
        NetworkingUtils.arrayToNetwork(buf, ingredients.stream()
                .map(BuiltInRegistries.ITEM::getKey).toArray(ResourceLocation[]::new),
                FriendlyByteBuf::writeResourceLocation);
        buf.writeInt(caffeine);
        NetworkingUtils.arrayToNetwork(buf, effects.toArray(MobEffectInstance[]::new), (b, instance) -> {
            b.writeResourceLocation(BuiltInRegistries.MOB_EFFECT.getKey(instance.getEffect()));
            b.writeInt(instance.getDuration());
            b.writeInt(instance.getAmplifier());
        });
        buf.writeBoolean(internal);
        buf.writeUtf(name == null ? "" : name);
    }

    public static TeaType fromNetwork(FriendlyByteBuf buf) {
        Optional<ResourceKey<TeaType>> parent =
                buf.readOptional(b -> b.readResourceKey(TeaTypeManager.TEA_TYPE));
        int color = buf.readInt();
        List<Item> ingredients = NetworkingUtils.listFromNetwork(buf, FriendlyByteBuf::readResourceLocation)
                .stream().map(BuiltInRegistries.ITEM::get).toList();
        int caffeine = buf.readInt();
        List<MobEffectInstance> effects = NetworkingUtils.listFromNetwork(buf, b -> {
            MobEffect effect = BuiltInRegistries.MOB_EFFECT.getOptional(b.readResourceLocation())
                    .orElseThrow();
            int duration = b.readInt();
            int amplifier = b.readInt();
            return new MobEffectInstance(effect, duration, amplifier);
        });
        boolean internal = buf.readBoolean();
        String name = buf.readUtf();
        return new TeaType(parent, color, ingredients, caffeine, effects, internal, name);
    }

}
