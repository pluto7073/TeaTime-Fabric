package ml.pluto7073.teatime.item;

import ml.pluto7073.pdapi.block.PDBlocks;
import ml.pluto7073.pdapi.item.PDItems;
import ml.pluto7073.teatime.block.TTBlocks;
import ml.pluto7073.teatime.block.entity.TeaMugBlockEntity;
import ml.pluto7073.teatime.component.TTComponents;
import ml.pluto7073.teatime.teatypes.TeaType;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TeaKettleItem extends BlockItem {

    public TeaKettleItem(Block block, Properties settings) {
        super(block, settings);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.EAT;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 16;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack kettle = user.getItemInHand(hand);
        ItemStack opposite = user.getItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        KettleData data = kettle.getOrDefault(TTComponents.KETTLE_DATA, KettleData.EMPTY);
        if (data.waterAmount() >= 250 && data.teaType() != TeaType.EMPTY && (opposite.is(Items.GLASS_BOTTLE) || opposite.is(PDItems.MUG))) {
            return ItemUtils.startUsingInstantly(world, user, hand);
        }

        return InteractionResultHolder.pass(user.getItemInHand(hand));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() != null) {
            InteractionResult result = this.use(context.getLevel(), context.getPlayer(), context.getHand()).getResult();
            if (!result.consumesAction()) {
                result = fillMug(context);
                if (!result.consumesAction()) {
                    return super.useOn(context);
                }
            }
            return result == InteractionResult.CONSUME ? InteractionResult.CONSUME_PARTIAL : result;
        } else {
            return super.useOn(context);
        }
    }

    public InteractionResult fillMug(UseOnContext context) {
        BlockPos pos = context.getClickedPos();
        BlockState state = context.getLevel().getBlockState(pos);
        if (!state.is(PDBlocks.MUG)) return InteractionResult.PASS;
        KettleData kettle = context.getItemInHand().getOrDefault(TTComponents.KETTLE_DATA, KettleData.EMPTY);
        int water = kettle.waterAmount();
        if (water <= 0) {
            return InteractionResult.PASS;
        }
        if (context.getLevel().isClientSide) return InteractionResult.SUCCESS;
        BlockState newState = TTBlocks.TEA_MUG.withPropertiesOf(state);
        context.getLevel().setBlock(pos, newState, Block.UPDATE_ALL);
        TeaMugBlockEntity mug = new TeaMugBlockEntity(pos, newState);
        mug.setTeaType(kettle.teaType());
        context.getLevel().setBlockEntity(mug);
        water -= 250;
        context.getItemInHand().set(TTComponents.KETTLE_DATA, kettle.copyWithWater(water));
        if (water <= 0) {
            context.getItemInHand().set(TTComponents.KETTLE_DATA, KettleData.EMPTY);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        if (world.isClientSide) {
            return stack;
        }

        KettleData kettle = stack.getOrDefault(TTComponents.KETTLE_DATA, KettleData.EMPTY);

        InteractionHand hand = user.getUsedItemHand();
        ItemStack opposite = user.getItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        Item teaInstanceItem = opposite.is(Items.GLASS_BOTTLE) ? TTItems.TEA : TTItems.TEA_MUG;
        ItemStack newStack = teaInstanceItem.getDefaultInstance();
        newStack.set(TTComponents.TEA_TYPE, kettle.teaType());
        int water = kettle.waterAmount() - 250;
        stack.set(TTComponents.KETTLE_DATA, kettle.copyWithWater(water));
        if (water <= 0) {
            stack.set(TTComponents.KETTLE_DATA, KettleData.EMPTY);
        }
        user.setItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND, newStack);

        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        KettleData data = stack.getOrDefault(TTComponents.KETTLE_DATA, KettleData.EMPTY);
        tooltip.add(Component.translatable("tooltip.teatime.kettle.water", data.waterAmount()).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.teatime.kettle.temperature", data.temperature()).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.empty());
        if (data.teaType() == TeaType.EMPTY) {
            List<ItemStack> insertedItems = data.insertedItems();
            if (!insertedItems.isEmpty()) {
                tooltip.add(Component.translatable("tooltip.teatime.kettle.items").withStyle(ChatFormatting.GRAY));
                for (ItemStack item : insertedItems) {
                    tooltip.add(Component.literal("\t").append(item.getDisplayName()).withStyle(ChatFormatting.GRAY));
                }
            } else {
                tooltip.add(Component.translatable("tooltip.teatime.kettle.empty"));
            }
        } else {
            tooltip.add(Component.translatable("tooltip.teatime.kettle.tea", Component.translatable(TeaType.getTranslationKey(data.teaType()))).withStyle(ChatFormatting.GRAY));
        }
    }
}
