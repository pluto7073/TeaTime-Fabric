package ml.pluto7073.teatime;

import ml.pluto7073.teatime.block.ModBlocks;
import ml.pluto7073.teatime.gui.SteamerScreen;
import ml.pluto7073.teatime.gui.handlers.TTMenuTypes;
import ml.pluto7073.teatime.item.ModItems;
import ml.pluto7073.teatime.networking.TTPacketsS2C;
import ml.pluto7073.teatime.utils.TeaTimeUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;

@Environment(EnvType.CLIENT)
public class TeaTimeClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        TTPacketsS2C.register();
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex > 0 ? -1 : TeaTimeUtils.getTeaColor(stack), ModItems.TEA);
        MenuScreens.register(TTMenuTypes.STEAMER_MENU_TYPE, SteamerScreen::new);
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.TEA_SHRUB, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.STEAMER, RenderType.cutout());
    }

}
