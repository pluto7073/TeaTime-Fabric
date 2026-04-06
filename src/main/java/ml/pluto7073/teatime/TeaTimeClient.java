package ml.pluto7073.teatime;

import ml.pluto7073.teatime.block.TTBlocks;
import ml.pluto7073.teatime.block.entity.TTBlockEntityTypes;
import ml.pluto7073.teatime.gui.SteamerScreen;
import ml.pluto7073.teatime.gui.TeaKettleScreen;
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
        ColorProviderRegistry.ITEM.register((stack, index) -> index == 1 ? TeaTimeUtils.getTeaColor(stack, Minecraft.getInstance().level) : -1, TTItems.TEA_MUG);
        ColorProviderRegistry.BLOCK.register((state, getter, pos, i) ->
                i == 1 ? TeaTimeUtils.getTeaColor(getter.getBlockEntity(pos, TTBlockEntityTypes.TEA_MUG).orElseThrow().saveToItem(), Minecraft.getInstance().level) : -1, TTBlocks.TEA_MUG);
        MenuScreens.register(TTMenuTypes.STEAMER, SteamerScreen::new);
        MenuScreens.register(TTMenuTypes.TEA_KETTLE, TeaKettleScreen::new);
        BlockRenderLayerMap.INSTANCE.putBlock(TTBlocks.TEA_SHRUB, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(TTBlocks.STEAMER, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(TTBlocks.TEA_MUG, RenderType.cutout());
    }

}
