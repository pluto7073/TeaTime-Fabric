package ml.pluto7073.teatime;

import ml.pluto7073.teatime.block.ModBlocks;
import ml.pluto7073.teatime.gui.handlers.ModScreenHandlerTypes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;

@Environment(EnvType.CLIENT)
public class TeaTimeClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModScreenHandlerTypes.init();
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.TEA_SHRUB, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.STEAMER, RenderLayer.getCutout());
    }

}
