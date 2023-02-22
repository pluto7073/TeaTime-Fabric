package ml.pluto7073.teatime.item;

import ml.pluto7073.teatime.TeaTime;
import ml.pluto7073.teatime.block.ModBlocks;
import net.fabricmc.fabric.api.registry.VillagerPlantableRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModItems {

    public static final Item TEA_LEAVES = new Item(new Item.Settings());
    public static final Item STEAMED_TEA_LEAVES = new Item(new Item.Settings());
    public static final Item ROLLED_TEA_LEAVES = new RolledTeaLeaves(new Item.Settings());
    public static final Item FERMENTED_TEA_LEAVES = new Item(new Item.Settings());
    public static final Item DRIED_TEA_LEAVES = new Item(new Item.Settings());
    public static final Item TEA_BAG = new TeaBagItem(new Item.Settings());
    public static final Item TEA = new Tea(new Item.Settings());
    private static final Item MILK_BOTTLE = new MilkBottle();
    public static final Item TEA_SEEDS = new AliasedBlockItem(ModBlocks.TEA_SHRUB, new Item.Settings());
    public static final Item STEAMER = new BlockItem(ModBlocks.STEAMER, new Item.Settings());

    private static void register(String id, Item item) {
        Registry.register(Registry.ITEM, new Identifier(TeaTime.MOD_ID, id), item);
    }

    private static void register(Identifier id, Item item) {
        Registry.register(Registry.ITEM, id, item);
    }

    public static void init() {
        register("tea_leaves", TEA_LEAVES);
        register("steamed_tea_leaves", STEAMED_TEA_LEAVES);
        register("rolled_tea_leaves", ROLLED_TEA_LEAVES);
        register("fermented_tea_leaves", FERMENTED_TEA_LEAVES);
        register("dried_tea_leaves", DRIED_TEA_LEAVES);
        register("tea_bag", TEA_BAG);
        register("tea", TEA);
        register("tea_seeds", TEA_SEEDS);
        VillagerPlantableRegistry.register(TEA_SEEDS);
        register("steamer", STEAMER);
        if (!TeaTime.PLUTOSCOFFEEMOD_LOADED) {
            register(new Identifier("plutoscoffee:milk_bottle"), MILK_BOTTLE);
        }
    }

    public static Item getMilkBottle() {
        if (TeaTime.PLUTOSCOFFEEMOD_LOADED) {
            return Registry.ITEM.get(new Identifier("plutoscoffee:milk_bottle"));
        } else {
            return MILK_BOTTLE;
        }
    }

}
