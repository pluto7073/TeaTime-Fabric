package ml.pluto7073.teatime.item;

import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.item.AbstractMugDrinkItem;
import ml.pluto7073.teatime.component.TTComponents;
import ml.pluto7073.teatime.stats.TTStats;
import ml.pluto7073.teatime.teatypes.TeaType;
import ml.pluto7073.teatime.teatypes.TeaType.MobEffectTemplate;
import ml.pluto7073.teatime.utils.TeaTimeUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@MethodsReturnNonnullByDefault
public class TeaMugItem extends AbstractMugDrinkItem {

    public TeaMugItem(Block mugBlock, Item baseItem, double baseVolume, Properties settings) {
        super(mugBlock, baseItem, baseVolume, settings);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Holder<TeaType> type = stack.get(TTComponents.TEA_TYPE);
        if (type != null) {
            tooltip.add(Component.translatable(TeaType.getTranslationKey(type)).withStyle(ChatFormatting.GRAY));
        }
        super.appendHoverText(stack, context, tooltip, flag);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        Player entity = user instanceof Player ? (Player) user : null;

        if (!world.isClientSide) {
            Holder<TeaType> type = stack.getOrDefault(TTComponents.TEA_TYPE, TeaType.EMPTY);
            for (MobEffectTemplate template : type.value().getEffects(world.registryAccess())) {
                user.addEffect(template.instantiate());
            }
        }

        if (entity != null) {
            entity.awardStat(TTStats.DRINK_TEA);
        }

        return super.finishUsingItem(stack, world, user);
    }

    @Override
    public float getChemicalContent(ResourceLocation name, ItemStack stack, Level level) {
        if (!"pdapi:caffeine".equals(name.toString()))
            return super.getChemicalContent(name, stack, level);
        float fromAdditions = super.getChemicalContent(name, stack, level);
        Holder<TeaType> type = stack.get(TTComponents.TEA_TYPE);
        if (type != null) {
            fromAdditions += type.value().getCaffeine(level.registryAccess());
        }
        return fromAdditions;
    }

}
