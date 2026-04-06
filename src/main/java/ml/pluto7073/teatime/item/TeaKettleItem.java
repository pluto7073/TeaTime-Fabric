package ml.pluto7073.teatime.item;

import ml.pluto7073.pdapi.block.PDBlocks;
import ml.pluto7073.pdapi.item.PDItems;
import ml.pluto7073.teatime.block.TTBlocks;
import ml.pluto7073.teatime.block.entity.TeaMugBlockEntity;
import ml.pluto7073.teatime.teatypes.TeaType;
import ml.pluto7073.teatime.teatypes.TeaTypeManager;
import ml.pluto7073.teatime.utils.TeaTimeUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerHelper;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.apache.commons.compress.utils.Lists;
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
    public ItemStack getDefaultInstance() {
        ItemStack stack = new ItemStack(this);
        resetTag(stack.getOrCreateTag());
        return stack;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.EAT;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 16;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack kettle = user.getItemInHand(hand);
        ItemStack opposite = user.getItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        if (getWaterAmount(kettle) >= 250 && getTeaType(kettle) != TeaTypeManager.EMPTY && (opposite.is(Items.GLASS_BOTTLE) || opposite.is(PDItems.MUG))) {
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
        int water = getWaterAmount(context.getItemInHand());
        if (water <= 0) {
            return InteractionResult.PASS;
        }
        if (context.getLevel().isClientSide) return InteractionResult.SUCCESS;
        BlockState newState = TTBlocks.TEA_MUG.withPropertiesOf(state);
        context.getLevel().setBlock(pos, newState, Block.UPDATE_ALL);
        TeaMugBlockEntity mug = new TeaMugBlockEntity(pos, newState);
        mug.setTeaType(getTeaType(context.getItemInHand()));
        context.getLevel().setBlockEntity(mug);
        setWaterAmount(context.getItemInHand(), water - 250);
        if (water - 250 <= 0) {
            resetTag(context.getItemInHand().getOrCreateTag());
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        if (world.isClientSide) {
            return stack;
        }

        InteractionHand hand = user.getUsedItemHand();
        ItemStack opposite = user.getItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        Item teaInstanceItem = opposite.is(Items.GLASS_BOTTLE) ? TTItems.TEA : TTItems.TEA_MUG;
        ItemStack newStack = teaInstanceItem.getDefaultInstance();
        TeaTimeUtils.setTeaType(newStack, getTeaType(stack).location());
        setWaterAmount(stack, getWaterAmount(stack) - 250);
        if (getWaterAmount(stack) <= 0) {
            resetTag(stack.getOrCreateTag());
        }
        user.setItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND, newStack);

        return stack;
    }

    public static int getWaterAmount(ItemStack kettle) {
        if (kettle.getTag() == null || kettle.getTag().isEmpty()) {
            resetTag(kettle.getOrCreateTag());
        }
        return kettle.getOrCreateTag().getInt("Water");
    }

    public static void setWaterAmount(ItemStack kettle, int waterAmount) {
        if (kettle.getTag() == null || kettle.getTag().isEmpty()) {
            resetTag(kettle.getOrCreateTag());
        }
        kettle.getOrCreateTag().putInt("Water", waterAmount);
    }

    public static List<ItemStack> getInsertedItems(ItemStack kettle) {
        if (kettle.getTag() == null || kettle.getTag().isEmpty()) {
            resetTag(kettle.getOrCreateTag());
        }
        ListTag list = kettle.getOrCreateTag().getList("InsertedItems", Tag.TAG_COMPOUND);
        List<ItemStack> items = Lists.newArrayList();
        for (Tag tag : list) {
            if (!(tag instanceof CompoundTag item)) continue;
            ItemStack stack = ItemStack.of(item);
            items.add(stack);
        }
        return items;
    }

    public static ResourceKey<TeaType> getTeaType(ItemStack kettle) {
        if (kettle.getTag() == null || kettle.getTag().isEmpty()) {
            resetTag(kettle.getOrCreateTag());
        }
        return ResourceKey.create(TeaTypeManager.TEA_TYPE, new ResourceLocation(kettle.getOrCreateTag().getString("TeaType")));
    }

    public static void resetTag(CompoundTag tag) {
        tag.putInt("Temperature", 15);
        tag.put("Items", new ListTag());
        tag.put("InsertedItems", new ListTag());
        tag.putInt("Water", 0);
        tag.putString("TeaType", TeaTypeManager.EMPTY.location().toString());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        tooltip.add(Component.translatable("tooltip.teatime.kettle.water", getWaterAmount(stack)).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.teatime.kettle.temperature", stack.getOrCreateTag().getInt("Temperature")).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.empty());
        if (getTeaType(stack) == TeaTypeManager.EMPTY) {
            List<ItemStack> insertedItems = getInsertedItems(stack);
            if (!insertedItems.isEmpty()) {
                tooltip.add(Component.translatable("tooltip.teatime.kettle.items").withStyle(ChatFormatting.GRAY));
                for (ItemStack item : insertedItems) {
                    tooltip.add(Component.literal("\t").append(item.getDisplayName()).withStyle(ChatFormatting.GRAY));
                }
            } else {
                tooltip.add(Component.translatable("tooltip.teatime.kettle.empty"));
            }
        } else {
            tooltip.add(Component.translatable("tooltip.teatime.kettle.tea", Component.translatable(getTeaType(stack).location().toLanguageKey("tea_type"))).withStyle(ChatFormatting.GRAY));
        }
    }
}
