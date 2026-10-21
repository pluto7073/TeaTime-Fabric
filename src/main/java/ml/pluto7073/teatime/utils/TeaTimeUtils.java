package ml.pluto7073.teatime.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ml.pluto7073.pdapi.item.AbstractCustomizableDrinkItem;
import ml.pluto7073.pdapi.util.ColorUtil;
import ml.pluto7073.pdapi.util.DrinkUtil;
import ml.pluto7073.pdapi.addition.DrinkAddition;
import ml.pluto7073.teatime.item.TTItems;
import ml.pluto7073.teatime.teatypes.TeaType;
import ml.pluto7073.teatime.teatypes.TeaTypeManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class TeaTimeUtils {

    public static final Map<String, Item> DRYING_RESULTS = new HashMap<>();
    public static final int MAX_TIME_DRYING = 200;
    public static final Codec<MobEffectInstance> MOB_EFFECT_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(BuiltInRegistries.MOB_EFFECT.byNameCodec().fieldOf("effect")
                                .forGetter(MobEffectInstance::getEffect),
                            Codec.INT.fieldOf("duration").forGetter(MobEffectInstance::getDuration),
                            Codec.INT.fieldOf("amplifier").forGetter(MobEffectInstance::getAmplifier))
                    .apply(instance, MobEffectInstance::new));

    public static List<ItemStack> getRolledLeaves() {
        List<ItemStack> stacks = new ArrayList<>();
        for (String s : DRYING_RESULTS.keySet()) {
            ItemStack stack = new ItemStack(TTItems.ROLLED_TEA_LEAVES);
            stack.getOrCreateTagElement("TeaData").putString("mod", s);
            stacks.add(stack);
        }
        return stacks;
    }

    public static List<ItemStack> getTeaBags(Level level) {
        List<ItemStack> stacks = new ArrayList<>();
        ItemStack stack = new ItemStack(TTItems.TEA_BAG);
        for (ResourceLocation i : level.getTeaTypeManager().getOrderedIdListDisplayed()) {
            if (i.equals(new ResourceLocation("teatime:empty"))) continue;
            stacks.add(setTeaType(stack.copy(), level.getTeaTypeManager().get(i), level));
        }
        return stacks;
    }

    public static List<ItemStack> getTea(Level level) {
        List<ItemStack> stacks = new ArrayList<>();
        ItemStack tea = new ItemStack(TTItems.TEA, 1);
        for (ResourceLocation i : level.getTeaTypeManager().getOrderedIdListDisplayed()) {
            if (i.equals(new ResourceLocation("teatime:empty"))) continue;
            stacks.add(setTeaType(tea.copy(), level.getTeaTypeManager().get(i), level));
        }
        return stacks;
    }

    public static List<ItemStack> getTeaMugs(Level level) {
        List<ItemStack> stacks = new ArrayList<>();
        ItemStack tea = new ItemStack(TTItems.TEA_MUG, 1);
        for (ResourceLocation i : level.getTeaTypeManager().getOrderedIdListDisplayed()) {
            if (i.equals(new ResourceLocation("teatime:empty"))) continue;
            stacks.add(setTeaType(tea.copy(), level.getTeaTypeManager().get(i), level));
        }
        return stacks;
    }

    public static Item getDryingResult(ItemStack stack) {
        String mod = stack.getOrCreateTagElement("TeaData").contains("mod") ?
                stack.getOrCreateTagElement("TeaData").getString("mod") : "teatime:null";
        return DRYING_RESULTS.getOrDefault(mod, TTItems.ROLLED_TEA_LEAVES);
    }

    public static boolean hasDryingResult(ItemStack stack) {
        String mod = stack.getOrCreateTagElement("TeaData").contains("mod") ?
                stack.getOrCreateTagElement("TeaData").getString("mod") : "teatime:null";
        return DRYING_RESULTS.containsKey(mod);
    }

    public static String getRolledTooltip(ItemStack stack) {
        String mod = stack.getOrCreateTagElement("TeaData").getString("mod");
        Item result = DRYING_RESULTS.get(mod);
        if (result == null) return "";
        return result.getDescriptionId();
    }

    public static String getTeaTypeStr(ItemStack stack) {
        return stack.getOrCreateTagElement("TeaData").contains("type") ? stack.getOrCreateTagElement("TeaData").getString("type") : "teatime:empty";
    }

    public static TeaType getTeaType(ItemStack stack, Level level) {
        return getTeaType(stack.getOrCreateTagElement("TeaData"), level);
    }

    public static TeaType getTeaType(CompoundTag teaData, Level level) {
        String type = teaData.contains("type") ? teaData.getString("type") : "teatime:empty";
        return level.getTeaTypeManager().containsId(new ResourceLocation(type)) ? level.getTeaTypeManager().get(new ResourceLocation(type)) : TeaTypeManager.EMPTY_TYPE;
    }

    public static ResourceLocation getTeaTypeId(ItemStack stack) {
        return new ResourceLocation(stack.getOrCreateTagElement("TeaData").getString("type"));
    }

    public static ItemStack setTeaType(ItemStack stack, TeaType type, Level level) {
        return setTeaType(stack, level.getTeaTypeManager().getId(type));
    }

    public static ItemStack setTeaType(ItemStack stack, ResourceLocation teaId) {
        stack.getOrCreateTagElement("TeaData").putString("type", teaId.toString());
        return stack;
    }

    public static ItemStack setTeaType(ItemStack stack, ResourceKey<TeaType> id) {
        return setTeaType(stack, id.location());
    }

    public static <T> T create(Supplier<T> supplier) {
        return supplier.get();
    }

    public static int getTeaColor(ItemStack stack, Level level) {
        int colour = 0;
        if (getTeaType(stack.getOrCreateTag().getCompound("TeaData"), level) != null) colour = getTeaType(stack.getOrCreateTag().getCompound("TeaData"), level).getColour(level);
        return ColorUtil.getColorForDrinkWithDefault(stack, colour, 1, level);
    }

    static {
        DRYING_RESULTS.put("teatime:steamed", TTItems.FERMENTED_TEA_LEAVES);
        DRYING_RESULTS.put("teatime:withered", TTItems.DRIED_TEA_LEAVES);
    }

}
