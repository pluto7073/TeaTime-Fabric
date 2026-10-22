package ml.pluto7073.teatime;

import ml.pluto7073.teatime.action.TeaTimeActions;
import ml.pluto7073.teatime.block.TTBlocks;
import ml.pluto7073.teatime.block.entity.TTBlockEntityTypes;
import ml.pluto7073.teatime.component.TTComponents;
import ml.pluto7073.teatime.entity.TTTrackedData;
import ml.pluto7073.teatime.event.TTEvents;
import ml.pluto7073.teatime.gui.handlers.TTMenuTypes;
import ml.pluto7073.teatime.item.TTItems;
import ml.pluto7073.teatime.recipe.TTRecipes;
import ml.pluto7073.teatime.stats.TTStats;
import ml.pluto7073.teatime.teatypes.TeaSpecialtyBase;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TeaTime implements ModInitializer {

    public static final String MOD_ID = "teatime";
    public static final Logger logger = LogManager.getLogger("TeaTime");
    public static ResourceKey<CreativeModeTab> TT_GROUP;

    @Override
    public void onInitialize() {
        TTRegistries.init();
        TeaSpecialtyBase.init();
        TTComponents.init();
        TTBlocks.init();
        TTBlockEntityTypes.init();
        TTRecipes.init();
        TTItems.init();
        TTStats.init();
        TTTrackedData.init();
        TeaTimeActions.init();
        createItemGroup();
        TTEvents.init();
        TTMenuTypes.init();
    }

    public static void createItemGroup() {
        TT_GROUP = ResourceKey.create(Registries.CREATIVE_MODE_TAB, asId("tt_group"));
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, TT_GROUP, FabricItemGroup.builder()
                .icon(() -> new ItemStack(TTItems.TEA_LEAVES)).title(Component.translatable("itemGroup.teatime.tt_group")).build());
    }

    public static ResourceLocation asId(String name) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
    }

}
