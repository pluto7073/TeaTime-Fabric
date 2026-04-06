package ml.pluto7073.teatime.item;

import ml.pluto7073.pdapi.item.AbstractMugDrinkItem;
import ml.pluto7073.teatime.stats.TTStats;
import ml.pluto7073.teatime.teatypes.TeaType;
import ml.pluto7073.teatime.teatypes.TeaTypeManager;
import ml.pluto7073.teatime.utils.TeaTimeUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
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
    public ItemStack getDefaultInstance() {
        return TeaTimeUtils.setTeaType(super.getDefaultInstance(), TeaTypeManager.EMPTY);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        if (world == null) return;
        TeaType type = TeaTimeUtils.getTeaType(stack, world);
        tooltip.add(Component.translatable(type.getTranslationKey(world)).withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, world, tooltip, context);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        Player entity = user instanceof Player ? (Player) user : null;

        if (!world.isClientSide) {
            TeaType type = TeaTimeUtils.getTeaType(stack, world);
            for (MobEffectInstance instance : type.getEffects(world)) {
                user.addEffect(instance);
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
        fromAdditions += TeaTimeUtils.getTeaType(stack, level).getCaffeine(level);
        return fromAdditions;
    }

}
