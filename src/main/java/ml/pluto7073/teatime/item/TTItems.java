package ml.pluto7073.teatime.item;

import ml.pluto7073.pdapi.item.PDItems;
import ml.pluto7073.teatime.TeaTime;
import ml.pluto7073.teatime.block.TTBlocks;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;

public class TTItems {

    public static final Item TEA_LEAVES = new Item(new Item.Properties());
    public static final Item WITHERED_TEA_LEAVES = new Item(new Item.Properties());
    public static final Item WHITE_TEA_LEAVES = new Item(new Item.Properties());
    public static final Item STEAMED_TEA_LEAVES = new Item(new Item.Properties());
    public static final Item ROLLED_TEA_LEAVES = new RolledTeaLeavesItem(new Item.Properties());
    public static final Item FERMENTED_TEA_LEAVES = new Item(new Item.Properties());
    public static final Item DRIED_TEA_LEAVES = new Item(new Item.Properties());
    public static final Item TEA_BAG = new TeaBagItem(new Item.Properties());
    public static final Item TEA = new TeaItem(new Item.Properties().stacksTo(1));
    public static final Item TEA_SEEDS = new ItemNameBlockItem(TTBlocks.TEA_SHRUB, new Item.Properties());
    public static final Item STEAMER = new BlockItem(TTBlocks.STEAMER, new Item.Properties());
    public static final Item TEA_KETTLE = new TeaKettleItem(TTBlocks.TEA_KETTLE, new Item.Properties());
    public static final Item TEA_MUG = new TeaMugItem(TTBlocks.TEA_MUG, PDItems.MUG, 10.0, new Item.Properties().stacksTo(1));

    private static void register(String id, Item item) {
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(TeaTime.MOD_ID, id), item);
    }

    private static void register(ResourceLocation id, Item item) {
        Registry.register(BuiltInRegistries.ITEM, id, item);
    }

    public static void init() {
        register("tea_leaves", TEA_LEAVES);
        register("withered_tea_leaves", WITHERED_TEA_LEAVES);
        register("white_tea_leaves", WHITE_TEA_LEAVES);
        register("steamed_tea_leaves", STEAMED_TEA_LEAVES);
        register("rolled_tea_leaves", ROLLED_TEA_LEAVES);
        register("fermented_tea_leaves", FERMENTED_TEA_LEAVES);
        register("dried_tea_leaves", DRIED_TEA_LEAVES);
        register("tea_bag", TEA_BAG);
        register("tea", TEA);
        register("tea_seeds", TEA_SEEDS);
        register("steamer", STEAMER);
        register("tea_kettle", TEA_KETTLE);
        register("tea_mug", TEA_MUG);
    }

}
