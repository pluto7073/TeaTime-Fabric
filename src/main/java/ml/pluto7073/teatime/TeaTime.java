package ml.pluto7073.teatime;

import ml.pluto7073.pdapi.item.PDItems;
import ml.pluto7073.teatime.action.TeaTimeActions;
import ml.pluto7073.teatime.block.TTBlocks;
import ml.pluto7073.teatime.block.entity.TTBlockEntityTypes;
import ml.pluto7073.teatime.entity.TTTrackedData;
import ml.pluto7073.teatime.event.TTEvents;
import ml.pluto7073.teatime.gui.handlers.TTMenuTypes;
import ml.pluto7073.teatime.item.TTItems;
import ml.pluto7073.teatime.recipe.TTRecipes;
import ml.pluto7073.teatime.stats.TTStats;
import ml.pluto7073.teatime.teatypes.TeaSpecialtyBase;
import ml.pluto7073.teatime.teatypes.TeaTypeManager;
import ml.pluto7073.teatime.utils.TeaTimeUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
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
    public static final TeaTypeManager SERVER_TEA_TYPE_MANAGER = new TeaTypeManager();

    @Override
    public void onInitialize() {
        TeaSpecialtyBase.init();
        TTBlocks.init();
        TTBlockEntityTypes.init();
        TTRecipes.init();
        TTItems.init();
        TeaTypeManager.init();
        TTStats.init();
        TTTrackedData.init();
        TeaTimeActions.init();
        createItemGroup();
        registerResourceReloadListener();
        TTEvents.init();
        TTMenuTypes.init();
    }

    public static void createItemGroup() {
        TT_GROUP = ResourceKey.create(Registries.CREATIVE_MODE_TAB, asId("tt_group"));
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, TT_GROUP, FabricItemGroup.builder()
                .icon(() -> new ItemStack(TTItems.TEA_LEAVES)).title(Component.translatable("itemGroup.teatime.tt_group")).build());
        ItemGroupEvents.modifyEntriesEvent(TT_GROUP).register(stacks -> {
                    stacks.accept(new ItemStack(TTItems.STEAMER));
                    stacks.accept(PDItems.DRINK_WORKSTATION);
                    stacks.accept(new ItemStack(TTItems.TEA_SEEDS));
                    stacks.accept(new ItemStack(TTItems.TEA_LEAVES));
                    stacks.accept(new ItemStack(TTItems.WITHERED_TEA_LEAVES));
                    stacks.accept(new ItemStack(TTItems.WHITE_TEA_LEAVES));
                    stacks.accept(new ItemStack(TTItems.STEAMED_TEA_LEAVES));
                    stacks.acceptAll(TeaTimeUtils.getRolledLeaves());
                    stacks.accept(new ItemStack(TTItems.DRIED_TEA_LEAVES));
                    stacks.accept(new ItemStack(TTItems.FERMENTED_TEA_LEAVES));
                    stacks.accept(TTItems.TEA_KETTLE.getDefaultInstance());
                    if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                        if (Minecraft.getInstance().level != null) {
                            stacks.acceptAll(TeaTimeUtils.getTeaBags(Minecraft.getInstance().level));
                            stacks.acceptAll(TeaTimeUtils.getTea(Minecraft.getInstance().level));
                            stacks.acceptAll(TeaTimeUtils.getTeaMugs(Minecraft.getInstance().level));
                        }
                    }
                    stacks.accept(PDItems.MILK_BOTTLE);
                });
    }

    public static void registerResourceReloadListener() {
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(SERVER_TEA_TYPE_MANAGER);
    }

    public static ResourceLocation asId(String name) {
        return new ResourceLocation(MOD_ID, name);
    }

}
