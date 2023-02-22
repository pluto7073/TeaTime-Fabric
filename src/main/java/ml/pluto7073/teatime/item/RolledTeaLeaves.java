package ml.pluto7073.teatime.item;

import ml.pluto7073.teatime.utils.TeaTimeUtils;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RolledTeaLeaves extends Item {

    public RolledTeaLeaves(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (!stack.getOrCreateSubNbt("TeaData").contains("mod")) {
            super.appendTooltip(stack, world, tooltip, context);
            return;
        }
        tooltip.add(Text.translatable("rolled_will_create.teatime.title").formatted(Formatting.LIGHT_PURPLE));
        tooltip.add(Text.translatable(TeaTimeUtils.getRolledTooltip(stack).formatted(Formatting.GRAY)));
    }
}
