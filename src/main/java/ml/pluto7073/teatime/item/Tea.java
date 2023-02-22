package ml.pluto7073.teatime.item;

import ml.pluto7073.teatime.teatypes.TeaType;
import ml.pluto7073.teatime.utils.TeaTimeUtils;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Tea extends Item {

    private static final int MAX_USE_TIME = 32;

    public Tea(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack getDefaultStack() {
        return TeaTimeUtils.setTeaType(super.getDefaultStack(), TeaType.EMPTY);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        TeaType type = TeaTimeUtils.getTeaType(stack);
        tooltip.add(Text.translatable(type.getTranslationKey()).formatted(Formatting.GRAY));

        //TODO - Add-ins
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        PlayerEntity entity = user instanceof PlayerEntity ? (PlayerEntity) user : null;
        if (entity instanceof ServerPlayerEntity) {
            Criteria.CONSUME_ITEM.trigger((ServerPlayerEntity) entity, stack);
        }

        if (!world.isClient) {
            TeaType type = TeaTimeUtils.getTeaType(stack);
            for (StatusEffectInstance instance : type.getEffects()) {
                user.addStatusEffect(instance);
            }
            //TODO tea add-ins
        }

        if (entity != null) {
            entity.incrementStat(Stats.USED.getOrCreateStat(this));
            //TODO Drink Tea Stat
            if (!entity.getAbilities().creativeMode) {
                stack.decrement(1);
            }
        }

        if (entity == null || !entity.getAbilities().creativeMode) {
            if (stack.isEmpty()) {
                return new ItemStack(Items.GLASS_BOTTLE);
            }

            if (entity != null) {
                entity.getInventory().insertStack(new ItemStack(Items.GLASS_BOTTLE));
            }
        }

        user.emitGameEvent(GameEvent.DRINK);
        return stack;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return MAX_USE_TIME;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return ItemUsage.consumeHeldItem(world, user, hand);
    }
}
