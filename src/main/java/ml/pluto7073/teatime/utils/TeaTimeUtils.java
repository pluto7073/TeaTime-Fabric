package ml.pluto7073.teatime.utils;

import ml.pluto7073.pdapi.DrinkUtil;
import ml.pluto7073.pdapi.addition.DrinkAddition;
import ml.pluto7073.pdapi.addition.DrinkAdditions;
import ml.pluto7073.teatime.item.ModItems;
import ml.pluto7073.teatime.teatypes.TeaType;
import ml.pluto7073.teatime.teatypes.TeaTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class TeaTimeUtils {

    public static Map<Vec3d, LeafDryerManager> TEA_LEAF_AGE_MAP = new HashMap<>();
    public static final Map<String, Item> DRYING_RESULTS = new HashMap<>();
    public static final int MAX_TIME_DRYING = 200;

    public static List<ItemStack> getRolledLeaves() {
        List<ItemStack> stacks = new ArrayList<>();
        ItemStack steamed = new ItemStack(ModItems.ROLLED_TEA_LEAVES);
        steamed.getOrCreateSubNbt("TeaData").putString("mod", "teatime:steamed");
        stacks.add(steamed);
        ItemStack fresh = new ItemStack(ModItems.ROLLED_TEA_LEAVES);
        fresh.getOrCreateSubNbt("TeaData").putString("mod", "teatime:withered");
        stacks.add(fresh);
        return stacks;
    }

    public static List<ItemStack> getTeaBags() {
        List<ItemStack> stacks = new ArrayList<>();
        ItemStack herbalTea = new ItemStack(ModItems.TEA_BAG);
        herbalTea.getOrCreateSubNbt("TeaData").putString("type", "teatime:herbal_tea");
        stacks.add(herbalTea);
        ItemStack whiteTea = new ItemStack(ModItems.TEA_BAG);
        whiteTea.getOrCreateSubNbt("TeaData").putString("type", "teatime:white_tea");
        stacks.add(whiteTea);
        ItemStack greenTea = new ItemStack(ModItems.TEA_BAG);
        greenTea.getOrCreateSubNbt("TeaData").putString("type", "teatime:green_tea");
        stacks.add(greenTea);
        ItemStack blackTea = new ItemStack(ModItems.TEA_BAG);
        blackTea.getOrCreateSubNbt("TeaData").putString("type", "teatime:black_tea");
        stacks.add(blackTea);
        return stacks;
    }

    public static List<ItemStack> getTea() {
        List<ItemStack> stacks = new ArrayList<>();
        ItemStack tea = new ItemStack(ModItems.TEA, 1);
        stacks.add(setTeaType(tea.copy(), TeaTypes.HERBAL_TEA));
        stacks.add(setTeaType(tea.copy(), TeaTypes.WHITE_TEA));
        ItemStack greenTea = setTeaType(tea.copy(), TeaTypes.GREEN_TEA);
        stacks.add(greenTea);
        ItemStack blackTea = setTeaType(tea.copy(), TeaTypes.BLACK_TEA);
        stacks.add(blackTea);
        return stacks;
    }

    public static Item getDryingResult(ItemStack stack) {
        String mod = stack.getOrCreateSubNbt("TeaData").contains("mod") ?
                stack.getOrCreateSubNbt("TeaData").getString("mod") : "teatime:null";
        return DRYING_RESULTS.getOrDefault(mod, ModItems.ROLLED_TEA_LEAVES);
    }

    public static boolean hasDryingResult(ItemStack stack) {
        String mod = stack.getOrCreateSubNbt("TeaData").contains("mod") ?
                stack.getOrCreateSubNbt("TeaData").getString("mod") : "teatime:null";
        return DRYING_RESULTS.containsKey(mod);
    }

    public static String getRolledTooltip(ItemStack stack) {
        String mod = stack.getOrCreateSubNbt("TeaData").getString("mod");
        Item result = DRYING_RESULTS.get(mod);
        if (result == null) return "";
        return result.getTranslationKey();
    }

    public static TeaType getTeaType(ItemStack stack) {
        return getTeaType(stack.getOrCreateSubNbt("TeaData"));
    }

    public static TeaType getTeaType(NbtCompound teaData) {
        String type = teaData.contains("type") ? teaData.getString("type") : "teatime:empty";
        return TeaTypes.containsId(new Identifier(type)) ? TeaTypes.get(new Identifier(type)) : TeaTypes.EMPTY;
    }

    public static ItemStack setTeaType(ItemStack stack, TeaType type) {
        stack.getOrCreateSubNbt("TeaData").putString("type", TeaTypes.getId(type).toString());
        return stack;
    }

    public static <T> T create(Supplier<T> supplier) {
        return supplier.get();
    }

    public static int getTeaColor(ItemStack stack) {
        int colour = getTeaType(stack).getColour();
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
