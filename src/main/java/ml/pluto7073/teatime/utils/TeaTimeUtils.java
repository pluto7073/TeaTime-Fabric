package ml.pluto7073.teatime.utils;

import ml.pluto7073.pdapi.util.DrinkUtil;
import ml.pluto7073.pdapi.addition.DrinkAddition;
import ml.pluto7073.teatime.item.ModItems;
import ml.pluto7073.teatime.teatypes.TeaType;
import ml.pluto7073.teatime.teatypes.TeaTypeManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class TeaTimeUtils {

    public static final Map<String, Item> DRYING_RESULTS = new HashMap<>();
    public static final int MAX_TIME_DRYING = 200;

    public static List<ItemStack> getRolledLeaves() {
        List<ItemStack> stacks = new ArrayList<>();
        for (String s : DRYING_RESULTS.keySet()) {
            ItemStack stack = new ItemStack(ModItems.ROLLED_TEA_LEAVES);
            stack.getOrCreateTagElement("TeaData").putString("mod", s);
            stacks.add(stack);
        }
        return stacks;
    }

    public static List<ItemStack> getTeaBags() {
        List<ItemStack> stacks = new ArrayList<>();
        ItemStack stack = new ItemStack(ModItems.TEA_BAG);
        for (ResourceLocation i : TeaTypeManager.getOrderedIdListDisplayed()) {
            if (i.equals(new ResourceLocation("teatime:empty"))) continue;
            stacks.add(setTeaType(stack.copy(), TeaTypeManager.get(i)));
        }
        return stacks;
    }

    public static List<ItemStack> getTea() {
        List<ItemStack> stacks = new ArrayList<>();
        ItemStack tea = new ItemStack(ModItems.TEA, 1);
        for (ResourceLocation i : TeaTypeManager.getOrderedIdListDisplayed()) {
            if (i.equals(new ResourceLocation("teatime:empty"))) continue;
            stacks.add(setTeaType(tea.copy(), TeaTypeManager.get(i)));
        }
        return stacks;
    }

    public static Item getDryingResult(ItemStack stack) {
        String mod = stack.getOrCreateTagElement("TeaData").contains("mod") ?
                stack.getOrCreateTagElement("TeaData").getString("mod") : "teatime:null";
        return DRYING_RESULTS.getOrDefault(mod, ModItems.ROLLED_TEA_LEAVES);
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

    public static TeaType getTeaType(ItemStack stack) {
        return getTeaType(stack.getOrCreateTagElement("TeaData"));
    }

    public static TeaType getTeaType(CompoundTag teaData) {
        String type = teaData.contains("type") ? teaData.getString("type") : "teatime:empty";
        return TeaTypeManager.containsId(new ResourceLocation(type)) ? TeaTypeManager.get(new ResourceLocation(type)) : TeaTypeManager.EMPTY;
    }

    public static ResourceLocation getTeaTypeId(ItemStack stack) {
        TeaType type = getTeaType(stack);
        return TeaTypeManager.getId(type);
    }

    public static ItemStack setTeaType(ItemStack stack, TeaType type) {
        return setTeaType(stack, TeaTypeManager.getId(type));
    }

    public static ItemStack setTeaType(ItemStack stack, ResourceLocation teaId) {
        stack.getOrCreateTagElement("TeaData").putString("type", teaId.toString());
        return stack;
    }

    public static <T> T create(Supplier<T> supplier) {
        return supplier.get();
    }

    public static int getTeaColor(ItemStack stack) {
        int colour = 0;
        if (getTeaType(stack) != null) colour = getTeaType(stack).getColour();
        float r = (colour >> 16 & 255) / 255.0F;
        float g = (colour >> 8 & 255) / 255.0F;
        float b = (colour & 255) / 255.0F;
        int colourCount = 1;

        for (DrinkAddition addition : DrinkUtil.getAdditionsFromStack(stack)) {
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
        DRYING_RESULTS.put("teatime:steamed", ModItems.FERMENTED_TEA_LEAVES);
        DRYING_RESULTS.put("teatime:withered", ModItems.DRIED_TEA_LEAVES);
    }

}
