package ml.pluto7073.teatime.item;

import ml.pluto7073.teatime.component.TTComponents;
import ml.pluto7073.teatime.teatypes.TeaType;
import ml.pluto7073.teatime.utils.TeaTimeUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TeaBagItem extends Item {

    public TeaBagItem(Properties settings) {
        super(settings);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Holder<TeaType> teaType = stack.getOrDefault(TTComponents.TEA_TYPE, TeaType.EMPTY);
        tooltip.add(Component.translatable(TeaType.getTranslationKey(teaType)).withStyle(ChatFormatting.GRAY));
    }
}
