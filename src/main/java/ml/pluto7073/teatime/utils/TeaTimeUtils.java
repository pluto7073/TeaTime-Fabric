package ml.pluto7073.teatime.utils;

import com.google.common.collect.Streams;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import ml.pluto7073.pdapi.util.ColorUtil;
import ml.pluto7073.pdapi.util.DrinkUtil;
import ml.pluto7073.teatime.TeaTime;
import ml.pluto7073.teatime.component.TTComponents;
import ml.pluto7073.teatime.item.RolledTeaLeavesItem;
import ml.pluto7073.teatime.item.TTItems;
import ml.pluto7073.teatime.teatypes.TeaType;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.*;
import java.util.function.Supplier;

public final class TeaTimeUtils {

    public static final Map<ResourceLocation, Item> DRYING_RESULTS = new HashMap<>();
    public static final int MAX_TIME_DRYING = 200;

    public static List<ItemStack> getRolledLeaves() {
        List<ItemStack> stacks = new ArrayList<>();
        for (ResourceLocation source : DRYING_RESULTS.keySet()) {
            ItemStack stack = new ItemStack(TTItems.ROLLED_TEA_LEAVES);
            stack.set(TTComponents.ROLLED_TEA_LEAVES_SOURCE, source);
            stacks.add(stack);
        }
        return stacks;
    }

    public static List<ItemStack> getTeaBags(Registry<TeaType> registry) {
        List<ItemStack> stacks = new ArrayList<>();
        ItemStack stack = new ItemStack(TTItems.TEA_BAG);
        for (Holder<TeaType> i : TeaType.getOrderedIdListDisplayed(registry)) {
            stack.set(TTComponents.TEA_TYPE, i);
            stacks.add(stack.copy());
        }
        return stacks;
    }

    public static List<ItemStack> getTea(Registry<TeaType> registry) {
        List<ItemStack> stacks = new ArrayList<>();
        ItemStack tea = new ItemStack(TTItems.TEA, 1);
        for (Holder<TeaType> i : TeaType.getOrderedIdListDisplayed(registry)) {
            tea.set(TTComponents.TEA_TYPE, i);
            stacks.add(tea.copy());
        }
        return stacks;
    }

    public static List<ItemStack> getTeaMugs(Registry<TeaType> registry) {
        List<ItemStack> stacks = new ArrayList<>();
        ItemStack tea = new ItemStack(TTItems.TEA_MUG, 1);
        for (Holder<TeaType> i : TeaType.getOrderedIdListDisplayed(registry)) {
            tea.set(TTComponents.TEA_TYPE, i);
            stacks.add(tea.copy());
        }
        return stacks;
    }

    public static Item getDryingResult(ItemStack stack) {
        ResourceLocation source = stack.getOrDefault(TTComponents.ROLLED_TEA_LEAVES_SOURCE, RolledTeaLeavesItem.DEFAULT_SOURCE);
        return DRYING_RESULTS.getOrDefault(source, TTItems.ROLLED_TEA_LEAVES);
    }

    public static boolean hasDryingResult(ItemStack stack) {
        return DRYING_RESULTS.containsKey(stack.get(TTComponents.ROLLED_TEA_LEAVES_SOURCE));
    }

    public static String getRolledTooltip(ResourceLocation source) {
        Item result = DRYING_RESULTS.get(source);
        if (result == null) return "";
        return result.getDescriptionId();
    }

    public static <T> T create(Supplier<T> supplier) {
        return supplier.get();
    }

    public static int getTeaColor(ItemStack stack, HolderLookup.Provider provider) {
        int colour = 0;
        Holder<TeaType> type = stack.get(TTComponents.TEA_TYPE);
        if (type != null)
            colour = type.value().getColour(provider);
        return ColorUtil.getColorForDrinkWithDefault(stack, colour, 1);
    }

    static {
        DRYING_RESULTS.put(TeaTime.asId("steamed"), TTItems.FERMENTED_TEA_LEAVES);
        DRYING_RESULTS.put(TeaTime.asId("withered"), TTItems.DRIED_TEA_LEAVES);
    }

}
