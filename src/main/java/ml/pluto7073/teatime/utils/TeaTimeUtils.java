package ml.pluto7073.teatime.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ml.pluto7073.pdapi.item.AbstractCustomizableDrinkItem;
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
        return getTeaColor(stack.getOrCreateTag(), level);
    }

    public static int getTeaColor(CompoundTag nbt, Level level) {
        int colour = 0;
        if (getTeaType(nbt.getCompound("TeaData"), level) != null) colour = getTeaType(nbt.getCompound("TeaData"), level).getColour(level);
        float r = (colour >> 16 & 255) / 255.0F;
        float g = (colour >> 8 & 255) / 255.0F;
        float b = (colour & 255) / 255.0F;
        int colourCount = 1;

        for (DrinkAddition addition : DrinkUtil.getAdditionsFromTag(nbt.getCompound(AbstractCustomizableDrinkItem.DRINK_DATA_NBT_KEY), level)) {
            if (!addition.changesColor()) continue;
            int additionColour = addition.getColor();
            r += (additionColour >> 16 & 255) / 255.0F;
            g += (additionColour >> 8 & 255) / 255.0F;
            b += (additionColour & 255) / 255.0F;
            colourCount += 1;
        }
        r = r / (float) colourCount * 255.0F;
        g = g / (float) colourCount * 255.0F;
        b = b / (float) colourCount * 255.0F;
        return (int) r << 16 | (int) g << 8 | (int) b;
    }

    static {
        DRYING_RESULTS.put("teatime:steamed", TTItems.FERMENTED_TEA_LEAVES);
        DRYING_RESULTS.put("teatime:withered", TTItems.DRIED_TEA_LEAVES);
    }

}
