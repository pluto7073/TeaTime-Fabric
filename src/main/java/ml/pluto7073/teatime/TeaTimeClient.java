package ml.pluto7073.teatime;

import ml.pluto7073.teatime.block.TTBlocks;
import ml.pluto7073.teatime.gui.SteamerScreen;
import ml.pluto7073.teatime.gui.handlers.TTMenuTypes;
import ml.pluto7073.teatime.item.TTItems;
import ml.pluto7073.teatime.networking.ClientboundTTPackets;
import ml.pluto7073.teatime.utils.TeaTimeUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;

@Environment(EnvType.CLIENT)
public class TeaTimeClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientboundTTPackets.register();
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex > 0 ? -1 : TeaTimeUtils.getTeaColor(stack, Minecraft.getInstance().level), TTItems.TEA);
        MenuScreens.register(TTMenuTypes.STEAMER_MENU_TYPE, SteamerScreen::new);
        BlockRenderLayerMap.INSTANCE.putBlock(TTBlocks.TEA_SHRUB, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(TTBlocks.STEAMER, RenderType.cutout());
    }

}
