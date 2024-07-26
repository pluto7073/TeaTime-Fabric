package ml.pluto7073.teatime.item;

import ml.pluto7073.teatime.utils.TeaTimeUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RolledTeaLeaves extends Item {

    public RolledTeaLeaves(Properties settings) {
        super(settings);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        if (!stack.getOrCreateTagElement("TeaData").contains("mod")) {
            super.appendHoverText(stack, world, tooltip, context);
            return;
        }
        tooltip.add(Component.translatable("rolled_will_create.teatime.title").withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltip.add(Component.translatable("tooltip.teatime.one_x").append(Component.translatable(TeaTimeUtils.getRolledTooltip(stack))).withStyle(ChatFormatting.GRAY));
    }
}
