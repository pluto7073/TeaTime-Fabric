package ml.pluto7073.teatime.item;

import ml.pluto7073.teatime.TeaTime;
import ml.pluto7073.teatime.component.TTComponents;
import ml.pluto7073.teatime.utils.TeaTimeUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class RolledTeaLeavesItem extends Item {

    public static final ResourceLocation DEFAULT_SOURCE = TeaTime.asId("null");

    public RolledTeaLeavesItem(Properties settings) {
        super(settings);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        ResourceLocation source = stack.getOrDefault(TTComponents.ROLLED_TEA_LEAVES_SOURCE, DEFAULT_SOURCE);
        if (source == DEFAULT_SOURCE) {
            super.appendHoverText(stack, context, tooltip, flag);
            return;
        }
        tooltip.add(Component.translatable("rolled_will_create.teatime.title").withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltip.add(Component.translatable("tooltip.teatime.one_x").append(Component.translatable(TeaTimeUtils.getRolledTooltip(source))).withStyle(ChatFormatting.GRAY));
    }
}
