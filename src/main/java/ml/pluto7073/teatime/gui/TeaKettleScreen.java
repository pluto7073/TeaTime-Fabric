package ml.pluto7073.teatime.gui;

import com.google.common.collect.Lists;
import ml.pluto7073.teatime.TeaTime;
import ml.pluto7073.teatime.gui.handlers.TeaKettleMenu;
import ml.pluto7073.teatime.teatypes.TeaTypeManager;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TeaKettleScreen extends AbstractContainerScreen<TeaKettleMenu> {

    private static final ResourceLocation TEXTURE = TeaTime.asId("textures/gui/container/tea_kettle.png");

    public TeaKettleScreen(TeaKettleMenu handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
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
    protected void renderBg(GuiGraphics graphics, float delta, int mouseX, int mouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        graphics.blit(TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int x, int y) {
        super.renderTooltip(graphics, x, y);
        if (x >= leftPos + 99 && x <= leftPos + 108 && y >= topPos + 42 && y <= topPos + 51) {
            List<Component> tooltip = Lists.newArrayList(
                    Component.translatable("tooltip.teatime.kettle.water", menu.getWaterAmount()).withStyle(ChatFormatting.GRAY),
                    Component.translatable("tooltip.teatime.kettle.temperature", menu.getTemperature()).withStyle(ChatFormatting.GRAY),
                    Component.empty()
            );
            if (menu.getKettle().getTeaType() == TeaTypeManager.EMPTY) {
                tooltip.add(Component.translatable("tooltip.teatime.kettle.items").withStyle(ChatFormatting.GRAY));
                for (ItemStack item : menu.getKettle().getInsertedItems()) {
                    tooltip.add(item.getDisplayName());
                }
            } else {
                tooltip.add(Component.translatable("tooltip.teatime.kettle.tea", Component.translatable(menu.getKettle().getTeaType().location().toLanguageKey("tea_type")))
                        .withStyle(ChatFormatting.GRAY));
            }
            graphics.renderTooltip(font, tooltip, Optional.empty(), x, y);
        }
    }
}
