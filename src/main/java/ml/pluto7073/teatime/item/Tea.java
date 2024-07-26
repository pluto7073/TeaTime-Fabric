package ml.pluto7073.teatime.item;

import ml.pluto7073.pdapi.item.AbstractCustomizableDrinkItem;
import ml.pluto7073.teatime.stats.TTStats;
import ml.pluto7073.teatime.teatypes.TeaType;
import ml.pluto7073.teatime.teatypes.TeaTypes;
import ml.pluto7073.teatime.utils.TeaTimeUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@MethodsReturnNonnullByDefault
public class Tea extends AbstractCustomizableDrinkItem {

    private static final int MAX_USE_TIME = 32;

    public Tea(Properties settings) {
        super(Items.GLASS_BOTTLE, Temperature.HOT, settings);
    }

    @Override
    public ItemStack getDefaultInstance() {
        return TeaTimeUtils.setTeaType(super.getDefaultInstance(), TeaTypes.EMPTY);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        TeaType type = TeaTimeUtils.getTeaType(stack);
        tooltip.add(Component.translatable(type.getTranslationKey()).withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, world, tooltip, context);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        Player entity = user instanceof Player ? (Player) user : null;

        if (!world.isClientSide) {
            TeaType type = TeaTimeUtils.getTeaType(stack);
            for (MobEffectInstance instance : type.getEffects()) {
                user.addEffect(instance);
            }
        }

        if (entity != null) {
            entity.awardStat(TTStats.DRINK_TEA);
        }

        return super.finishUsingItem(stack, world, user);
    }

    @Override
    public int getCaffeineContent(ItemStack stack) {
        return TeaTimeUtils.getTeaType(stack).getCaffeine() + super.getCaffeineContent(stack);
    }

}
