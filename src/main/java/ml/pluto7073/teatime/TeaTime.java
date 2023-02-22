package ml.pluto7073.teatime;

import ml.pluto7073.teatime.block.ModBlocks;
import ml.pluto7073.teatime.block.entity.ModBlockEntityTypes;
import ml.pluto7073.teatime.event.ModEvents;
import ml.pluto7073.teatime.item.ModItems;
import ml.pluto7073.teatime.recipe.ModRecipes;
import ml.pluto7073.teatime.teatypes.TeaType;
import ml.pluto7073.teatime.utils.TeaTimeUtils;
import ml.pluto7073.teatime.utils.VersionChecker;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TeaTime implements ModInitializer {

    public static final String MOD_ID = "teatime";
    public static final Logger logger = LogManager.getLogger("TeaTime");
    public static final int MOD_VERSION = 0;
    public static ItemGroup TT_GROUP;
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
        TeaType.init();
        createItemGroup();
        registerResourceReloadListener();
        ModEvents.init();
    }

    public static void createItemGroup() {
        TT_GROUP = FabricItemGroupBuilder.create(new Identifier(MOD_ID, "tt_group"))
                .icon(() -> new ItemStack(ModItems.TEA_LEAVES)).appendItems(stacks -> {
                    stacks.add(new ItemStack(ModItems.STEAMER));
                    stacks.add(new ItemStack(ModItems.TEA_SEEDS));
                    stacks.add(new ItemStack(ModItems.TEA_LEAVES));
                    stacks.add(new ItemStack(ModItems.STEAMED_TEA_LEAVES));
                    stacks.addAll(TeaTimeUtils.getRolledLeaves());
                    stacks.add(new ItemStack(ModItems.DRIED_TEA_LEAVES));
                    stacks.add(new ItemStack(ModItems.FERMENTED_TEA_LEAVES));
                    stacks.addAll(TeaTimeUtils.getTeaBags());
                    stacks.addAll(TeaTimeUtils.getTea());
                }).build();
    }

    public static TeaTime getInstance() {
        return INSTANCE;
    }

    public void loadLater() {
        if (loadLaterDone) return;
        VersionChecker.checkOutdated();
        if (VersionChecker.isOutdated()) {
            logger.warn("TeaTime is outdated, please download the new version from Curseforge or GitHub");
        } else {
            logger.info("TeaTime is up to date");
        }
        loadLaterDone = true;
    }

    public static void registerResourceReloadListener() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new ModServerResourceManager());
    }

}
