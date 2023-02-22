package ml.pluto7073.teatime.utils;

import ml.pluto7073.teatime.item.ModItems;
import ml.pluto7073.teatime.teatypes.TeaType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        fresh.getOrCreateSubNbt("TeaData").putString("mod", "teatime:fresh");
        stacks.add(fresh);
        return stacks;
    }

    public static List<ItemStack> getTeaBags() {
        List<ItemStack> stacks = new ArrayList<>();
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
        ItemStack greenTea = setTeaType(tea.copy(), TeaType.GREEN_TEA);
        stacks.add(greenTea);
        ItemStack blackTea = setTeaType(tea.copy(), TeaType.BLACK_TEA);
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
        Identifier id = new Identifier(mod);
        return "rolled_will_create." + id.getNamespace() + "." + id.getPath();
    }

    public static TeaType getTeaType(ItemStack stack) {
        return getTeaType(stack.getOrCreateSubNbt("TeaData"));
    }

    public static TeaType getTeaType(NbtCompound teaData) {
        String type = teaData.contains("type") ? teaData.getString("type") : "teatime:empty";
        return TeaType.TYPES.getOrDefault(new Identifier(type), TeaType.EMPTY);
    }

    public static ItemStack setTeaType(ItemStack stack, TeaType type) {
        stack.getOrCreateSubNbt("TeaData").putString("type", TeaType.getId(type).toString());
        return stack;
    }

    static {
        DRYING_RESULTS.put("teatime:steamed", ModItems.DRIED_TEA_LEAVES);
        DRYING_RESULTS.put("teatime:fresh", ModItems.FERMENTED_TEA_LEAVES);
    }

}
