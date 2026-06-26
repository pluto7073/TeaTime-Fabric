package ml.pluto7073.teatime;

import ml.pluto7073.pdapi.item.PDItems;
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
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemStack;

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
        ItemGroupEvents.modifyEntriesEvent(TeaTime.TT_GROUP).register(stacks -> {
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

}
