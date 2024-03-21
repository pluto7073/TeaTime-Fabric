package ml.pluto7073.teatime;

import ml.pluto7073.pdapi.item.PDItems;
import ml.pluto7073.teatime.block.ModBlocks;
import ml.pluto7073.teatime.block.entity.ModBlockEntityTypes;
import ml.pluto7073.teatime.entity.TTTrackedData;
import ml.pluto7073.teatime.event.CustomTeaTypesRegisterer;
import ml.pluto7073.teatime.event.ModEvents;
import ml.pluto7073.teatime.gui.handlers.ModScreenHandlerTypes;
import ml.pluto7073.teatime.item.ModItems;
import ml.pluto7073.teatime.recipe.ModRecipes;
import ml.pluto7073.teatime.stats.TTStats;
import ml.pluto7073.teatime.teatypes.TeaType;
import ml.pluto7073.teatime.teatypes.TeaTypes;
import ml.pluto7073.teatime.utils.TeaTimeUtils;
import ml.pluto7073.teatime.utils.VersionChecker;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TeaTime implements ModInitializer {

    public static final String MOD_ID = "teatime";
    public static final Logger logger = LogManager.getLogger("TeaTime");
    public static final int MOD_VERSION = 0;
    public static RegistryKey<ItemGroup> TT_GROUP;
    public static boolean PLUTOSCOFFEEMOD_LOADED = false;

    private static TeaTime INSTANCE;
    private static boolean loadLaterDone = false;

    @Override
    public void onInitialize() {
        INSTANCE = this;
        PLUTOSCOFFEEMOD_LOADED = FabricLoader.getInstance().isModLoaded("plutoscoffee");
        logger.info(PLUTOSCOFFEEMOD_LOADED ? "PlutosCoffeeMod Found!" : "PlutosCoffeeMod does not appear to be loaded");
        ModBlocks.init();
        ModBlockEntityTypes.init();
        ModRecipes.init();
        ModItems.init();
        TeaTypes.init();
        TTStats.init();
        TTTrackedData.init();
        createItemGroup();
        registerResourceReloadListener();
        ModEvents.init();
        ModScreenHandlerTypes.init();
    }

    public static void createItemGroup() {
        TT_GROUP = RegistryKey.of(RegistryKeys.ITEM_GROUP, asId("tt_group"));
        Registry.register(Registries.ITEM_GROUP, TT_GROUP, FabricItemGroup.builder()
                .icon(() -> new ItemStack(ModItems.TEA_LEAVES)).displayName(Text.translatable("itemGroup.teatime.tt_group")).build());
        ItemGroupEvents.modifyEntriesEvent(TT_GROUP).register(stacks -> {
                    stacks.add(new ItemStack(ModItems.STEAMER));
                    stacks.add(new ItemStack(ModItems.TEA_SEEDS));
                    stacks.add(new ItemStack(ModItems.TEA_LEAVES));
                    stacks.add(new ItemStack(ModItems.WITHERED_TEA_LEAVES));
                    stacks.add(new ItemStack(ModItems.WHITE_TEA_LEAVES));
                    stacks.add(new ItemStack(ModItems.STEAMED_TEA_LEAVES));
                    stacks.addAll(TeaTimeUtils.getRolledLeaves());
                    stacks.add(new ItemStack(ModItems.DRIED_TEA_LEAVES));
                    stacks.add(new ItemStack(ModItems.FERMENTED_TEA_LEAVES));
                    stacks.addAll(TeaTimeUtils.getTeaBags());
                    stacks.addAll(TeaTimeUtils.getTea());
                    stacks.add(PDItems.MILK_BOTTLE);
                    stacks.add(PDItems.DRINK_WORKSTATION);
                });
    }

    public static TeaTime getInstance() {
        return INSTANCE;
    }

    public static void registerResourceReloadListener() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new ModServerResourceManager());
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new CustomTeaTypesRegisterer());
    }

    public static Identifier asId(String name) {
        return new Identifier(MOD_ID, name);
    }

}
