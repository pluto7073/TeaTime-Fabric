package ml.pluto7073.teatime;

import ml.pluto7073.pdapi.addition.DrinkAdditionManager;
import ml.pluto7073.pdapi.item.PDItems;
import ml.pluto7073.pdapi.specialty.SpecialtyDrinkManager;
import ml.pluto7073.teatime.action.TeaTimeActions;
import ml.pluto7073.teatime.block.ModBlocks;
import ml.pluto7073.teatime.block.entity.ModBlockEntityTypes;
import ml.pluto7073.teatime.entity.TTTrackedData;
import ml.pluto7073.teatime.event.ModEvents;
import ml.pluto7073.teatime.gui.handlers.TTMenuTypes;
import ml.pluto7073.teatime.item.ModItems;
import ml.pluto7073.teatime.recipe.ModRecipes;
import ml.pluto7073.teatime.stats.TTStats;
import ml.pluto7073.teatime.teatypes.TeaSpecialtyBase;
import ml.pluto7073.teatime.teatypes.TeaTypeManager;
import ml.pluto7073.teatime.utils.TeaTimeUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
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
        TeaSpecialtyBase.init();
        ModBlocks.init();
        ModBlockEntityTypes.init();
        ModRecipes.init();
        ModItems.init();
        TeaTypeManager.init();
        TTStats.init();
        TTTrackedData.init();
        TeaTimeActions.init();
        createItemGroup();
        registerResourceReloadListener();
        ModEvents.init();
        TTMenuTypes.init();
    }

    public static void createItemGroup() {
        TT_GROUP = ResourceKey.create(Registries.CREATIVE_MODE_TAB, asId("tt_group"));
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, TT_GROUP, FabricItemGroup.builder()
                .icon(() -> new ItemStack(ModItems.TEA_LEAVES)).title(Component.translatable("itemGroup.teatime.tt_group")).build());
        ItemGroupEvents.modifyEntriesEvent(TT_GROUP).register(stacks -> {
                    stacks.accept(new ItemStack(ModItems.STEAMER));
                    stacks.accept(PDItems.DRINK_WORKSTATION);
                    stacks.accept(new ItemStack(ModItems.TEA_SEEDS));
                    stacks.accept(new ItemStack(ModItems.TEA_LEAVES));
                    stacks.accept(new ItemStack(ModItems.WITHERED_TEA_LEAVES));
                    stacks.accept(new ItemStack(ModItems.WHITE_TEA_LEAVES));
                    stacks.accept(new ItemStack(ModItems.STEAMED_TEA_LEAVES));
                    stacks.acceptAll(TeaTimeUtils.getRolledLeaves());
                    stacks.accept(new ItemStack(ModItems.DRIED_TEA_LEAVES));
                    stacks.accept(new ItemStack(ModItems.FERMENTED_TEA_LEAVES));
                    stacks.acceptAll(TeaTimeUtils.getTeaBags());
                    stacks.acceptAll(TeaTimeUtils.getTea());
                    stacks.accept(PDItems.MILK_BOTTLE);
                });
    }

    public static void registerResourceReloadListener() {
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new TeaTypeManager());
    }

    public static ResourceLocation asId(String name) {
        return new ResourceLocation(MOD_ID, name);
    }

}
