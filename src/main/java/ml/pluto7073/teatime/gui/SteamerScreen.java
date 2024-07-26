package ml.pluto7073.teatime.gui;

import ml.pluto7073.teatime.TeaTime;
import ml.pluto7073.teatime.gui.handlers.SteamerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class SteamerScreen extends AbstractContainerScreen<SteamerMenu> {

    private static final ResourceLocation TEXTURE = TeaTime.asId("textures/gui/container/steamer.png");

    public SteamerScreen(SteamerMenu handler, Inventory playerInventory, Component title) {
        super(handler, playerInventory, title);
    }

    public void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        this.renderTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        context.blit(TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight);
        int k;
        if (this.menu.isBoiling()) {
            k = this.menu.getWaterProgress();
            context.blit(TEXTURE, x + 56, y + 36 + 12 - k, 176, 12 - k, 14, k + 1);
        }

        k = this.menu.getSteamProgress();
        context.blit(TEXTURE, x + 79, y + 34, 176, 14, k + 1, 16);
    }

}
