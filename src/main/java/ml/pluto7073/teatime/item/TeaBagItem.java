package ml.pluto7073.teatime.item;

import ml.pluto7073.teatime.teatypes.TeaType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TeaBagItem extends Item {

    public TeaBagItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        String teaTypeId = stack.getOrCreateSubNbt("TeaData").contains("type") ?
                stack.getOrCreateSubNbt("TeaData").getString("type") : "teatime:empty";
        TeaType teaType = TeaType.TYPES.getOrDefault(new Identifier(teaTypeId), TeaType.EMPTY);
        tooltip.add(Text.translatable(teaType.getTranslationKey()).formatted(Formatting.GRAY));
    }

}
