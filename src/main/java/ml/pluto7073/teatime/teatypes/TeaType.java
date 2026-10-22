package ml.pluto7073.teatime.teatypes;

import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ml.pluto7073.pdapi.util.DrinkUtil;
import ml.pluto7073.teatime.TTRegistries;
import ml.pluto7073.teatime.TeaTime;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.*;

public class TeaType {

    public static final Codec<TeaType> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Codec.optionalField("parent", ResourceKey.codec(TTRegistries.TEA_TYPE), true)
                            .forGetter(type -> type.parent),
                            Codec.INT.fieldOf("color").orElse(0).forGetter(type -> type.colour),
                            Codec.list(BuiltInRegistries.ITEM.byNameCodec()).fieldOf("ingredients")
                                    .forGetter(type -> type.ingredients),
                            Codec.INT.fieldOf("caffeine").orElse(0)
                                    .forGetter(type -> type.caffeine),
                            Codec.list(MobEffectTemplate.CODEC).fieldOf("effects")
                                    .forGetter(type -> type.effects),
                            Codec.BOOL.fieldOf("internal").orElse(false)
                                    .forGetter(TeaType::internal),
                            Codec.STRING.fieldOf("name").orElse("")
                                    .forGetter(type -> type.name))
                    .apply(instance, TeaType::new));

    public static final Holder<TeaType> EMPTY = Holder.direct(new TeaType(
            Optional.empty(),
            0, List.of(), 0, List.of(), true, ""
    ));

    public static final Codec<Holder<TeaType>> COMPONENT_CODEC =
            RegistryFixedCodec.create(TTRegistries.TEA_TYPE);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<TeaType>> COMPONENT_STREAM_CODEC =
            ByteBufCodecs.optional(ByteBufCodecs.holderRegistry(TTRegistries.TEA_TYPE))
                    .map(opt -> opt.orElse(EMPTY),
                            type -> type == EMPTY || type == null
                                    ? Optional.empty() : Optional.of(type));

    public static final ResourceKey<TeaType> HERBAL_TEA = key("herbal_tea"),
    WHITE_TEA = key("white_tea"),
    GREEN_TEA = key("green_tea"),
    BLACK_TEA = key("black_tea");

    private final Optional<ResourceKey<TeaType>> parent;
    private final List<Item> ingredients;
    private final int colour;
    private final int caffeine;
    private final List<MobEffectTemplate> effects;
    private final boolean internal;
    private final String name;

    public TeaType(Optional<ResourceKey<TeaType>> parent, int colour, List<Item> ingredients, int caffeine, List<MobEffectTemplate> effects, boolean internal, String name) {
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

    public List<Item> getIngredients(HolderLookup.Provider provider) {
        ArrayList<Item> list = Lists.newArrayList(this.ingredients.iterator());
        var lookup = provider.lookupOrThrow(TTRegistries.TEA_TYPE);
        parent.ifPresent(type ->
                list.addAll(lookup.getOrThrow(type).value().getIngredients(provider)));
        return list;
    }

    public int getColour(HolderLookup.Provider provider) {
        return parent.isPresent() && colour == 0 ? provider.lookupOrThrow(TTRegistries.TEA_TYPE)
                .getOrThrow(parent.get()).value().colour : colour;
    }

    public static String getTranslationKey(Holder<TeaType> type) {
        if (type.value().name != null && !type.value().name.isEmpty()) return type.value().name;
        return type.unwrapKey().orElseThrow().location().toLanguageKey("tea_type");
    }

    public int getCaffeine(HolderLookup.Provider provider) {
        return parent.map(teaTypeResourceKey -> provider.lookupOrThrow(TTRegistries.TEA_TYPE)
                        .getOrThrow(teaTypeResourceKey).value().caffeine).orElse(caffeine);
    }

    public List<MobEffectTemplate> getEffects(HolderLookup.Provider provider) {
        ArrayList<MobEffectTemplate> list = Lists.newArrayList(this.effects.iterator());
        parent.ifPresent(teaTypeResourceKey ->
                list.addAll(provider.lookupOrThrow(TTRegistries.TEA_TYPE).getOrThrow(teaTypeResourceKey)
                        .value().getEffects(provider)));
        return list;
    }

    public static Optional<Reference<TeaType>> getFromIngredients(List<ItemStack> stacks, HolderLookup.Provider provider) {
        return provider.lookupOrThrow(TTRegistries.TEA_TYPE).listElements().filter(type -> {
            List<Ingredient> list = type.value().getIngredients(provider).stream().map(Ingredient::of).toList();
            if (list.size() != stacks.size()) return false;
            for (Ingredient i : list) {
                if (stacks.stream().noneMatch(i)) return false;
            }
            return true;
        }).findFirst();
    }

    public static List<Reference<TeaType>> getOrderedIdListDisplayed(Registry<TeaType> registry) {
        List<Reference<TeaType>> herbalTeas = new ArrayList<>();
        List<Reference<TeaType>> whiteTeas = new ArrayList<>();
        List<Reference<TeaType>> greenTeas = new ArrayList<>();
        List<Reference<TeaType>> blackTeas = new ArrayList<>();
        List<Reference<TeaType>> miscTeas = new ArrayList<>();
        for (Reference<TeaType> type : registry.holders().toList()) {
            if (type == null) continue;
            if (type.value().internal()) continue;
            if (type.value().parent().isEmpty()) {
                miscTeas.add(type);
                continue;
            }
            if (type.value().parent().equals(Optional.of(HERBAL_TEA))) herbalTeas.add(type);
            else if (type.value().parent().equals(Optional.of(WHITE_TEA))) whiteTeas.add(type);
            else if (type.value().parent().equals(Optional.of(GREEN_TEA))) greenTeas.add(type);
            else if (type.value().parent().equals(Optional.of(BLACK_TEA))) blackTeas.add(type);
            else miscTeas.add(type);
        }
        Comparator<Reference<TeaType>> comparator =
                DrinkUtil.alphabetizer(r -> r.key().location().toString());
        herbalTeas.sort(comparator);
        whiteTeas.sort(comparator);
        greenTeas.sort(comparator);
        blackTeas.sort(comparator);
        miscTeas.sort(comparator);
        whiteTeas.add(0, registry.getHolderOrThrow(WHITE_TEA));
        greenTeas.add(0, registry.getHolderOrThrow(GREEN_TEA));
        blackTeas.add(0, registry.getHolderOrThrow(BLACK_TEA));
        return Streams.concat(herbalTeas.stream(), whiteTeas.stream(), greenTeas.stream(), blackTeas.stream())
                .toList();
    }

    private static ResourceKey<TeaType> key(String id) {
        return ResourceKey.create(TTRegistries.TEA_TYPE, TeaTime.asId(id));
    }

    public record MobEffectTemplate(Holder<MobEffect> effect, int duration, int amplifier) {

        public static final Codec<MobEffectTemplate> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("effect")
                                        .forGetter(MobEffectTemplate::effect),
                                Codec.INT.fieldOf("duration").forGetter(MobEffectTemplate::duration),
                                Codec.INT.fieldOf("amplifier").forGetter(MobEffectTemplate::amplifier))
                        .apply(instance, MobEffectTemplate::new));

        public MobEffectInstance instantiate() {
            return new MobEffectInstance(effect, duration, amplifier);
        }

    }

}
