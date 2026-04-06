package ml.pluto7073.teatime.gui.handlers;

import ml.pluto7073.teatime.block.TeaKettleBlock;
import ml.pluto7073.teatime.block.entity.TTBlockEntityTypes;
import ml.pluto7073.teatime.block.entity.TeaKettleBlockEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Predicate;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TeaKettleMenu extends AbstractContainerMenu {

    private final Container container;
    private final ContainerData data;
    private final Level level;
    private TeaKettleBlockEntity kettle;

    public TeaKettleMenu(int syncId, Inventory inventory) {
        this(syncId, inventory, new SimpleContainer(TeaKettleBlockEntity.CONTAINER_SIZE), new SimpleContainerData(TeaKettleBlockEntity.DATA_COUNT));
    }

    public TeaKettleMenu(int syncId, Inventory inventory, Container container, ContainerData data) {
        super(TTMenuTypes.TEA_KETTLE, syncId);
        this.container = container;
        this.data = data;
        this.level = inventory.player.level();
        checkContainerSize(container, TeaKettleBlockEntity.CONTAINER_SIZE);
        checkContainerDataCount(data, TeaKettleBlockEntity.DATA_COUNT);
        this.addSlot(new FilterSlot(stack -> true, container, TeaKettleBlockEntity.SLOT_ITEM_INSERT, 80, 16));
        this.addSlot(new FilterSlot(stack -> stack.is(Items.WATER_BUCKET), container, TeaKettleBlockEntity.SLOT_WATER_INSERT, 80, 52));
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(inventory, i, 8 + i * 18, 142));
        }

        this.addDataSlots(data);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int fromIndex) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    public int getWaterAmount() {
        return data.get(TeaKettleBlockEntity.DATA_WATER_AMOUNT);
    }

    public int getTemperature() {
        return data.get(TeaKettleBlockEntity.DATA_TEMPERATURE);
    }

    public TeaKettleBlockEntity getKettle() {
        return level.getBlockEntity(new BlockPos(data.get(TeaKettleBlockEntity.DATA_POS_X), data.get(TeaKettleBlockEntity.DATA_POS_Y), data.get(TeaKettleBlockEntity.DATA_POS_Z)), TTBlockEntityTypes.TEA_KETTLE)
                .orElseThrow(IllegalStateException::new);
    }

    public static class FilterSlot extends Slot {

        private final Predicate<ItemStack> filter;

        public FilterSlot(Predicate<ItemStack> filter, Container inventory, int index, int x, int y) {
            super(inventory, index, x, y);
            this.filter = filter;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return filter.test(stack);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }

}
