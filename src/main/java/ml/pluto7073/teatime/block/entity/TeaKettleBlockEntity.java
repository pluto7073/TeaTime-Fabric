package ml.pluto7073.teatime.block.entity;

import com.google.common.collect.Lists;
import ml.pluto7073.teatime.TeaTime;
import ml.pluto7073.teatime.gui.handlers.TeaKettleMenu;
import ml.pluto7073.teatime.item.TTItems;
import ml.pluto7073.teatime.tags.TTBlockTags;
import ml.pluto7073.teatime.tags.TTItemTags;
import ml.pluto7073.teatime.teatypes.TeaType;
import ml.pluto7073.teatime.teatypes.TeaTypeManager;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TeaKettleBlockEntity extends BaseContainerBlockEntity {

    public static final int SLOT_ITEM_INSERT = 0;
    public static final int SLOT_WATER_INSERT = 1;
    public static final int CONTAINER_SIZE = 2;

    public static final int DATA_WATER_AMOUNT = 0;
    public static final int DATA_TEMPERATURE = 1;
    public static final int DATA_POS_X = 2;
    public static final int DATA_POS_Y = 3;
    public static final int DATA_POS_Z = 4;
    public static final int DATA_COUNT = 5;

    private final NonNullList<ItemStack> items;
    private final ArrayList<ItemStack> insertedStacks;
    private final ContainerData containerData;

    private int waterAmount = 0;
    private int temperature = 15;
    private ResourceKey<TeaType> teaType = TeaTypeManager.EMPTY;

    public TeaKettleBlockEntity(BlockPos pos, BlockState blockState) {
        super(TTBlockEntityTypes.TEA_KETTLE, pos, blockState);
        items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
        insertedStacks = Lists.newArrayList();
        containerData = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case DATA_WATER_AMOUNT -> waterAmount;
                    case DATA_TEMPERATURE -> temperature;
                    case DATA_POS_X -> pos.getX();
                    case DATA_POS_Y -> pos.getY();
                    case DATA_POS_Z -> pos.getZ();
                    default -> -1;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case DATA_WATER_AMOUNT -> waterAmount = value;
                    case DATA_TEMPERATURE -> temperature = value;
                }
            }

            @Override
            public int getCount() {
                return DATA_COUNT;
            }
        };
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TeaKettleBlockEntity kettle) {
        boolean changed = false;

        if (kettle.isHeated() && kettle.temperature < 100 && level.getGameTime() % 20 == 0) {
            kettle.temperature++;
            changed = true;
        } else if (!kettle.isHeated() && kettle.temperature > 15 && level.getGameTime() % 40 == 0) {
            kettle.temperature--;
            changed = true;
        }

        if (kettle.waterAmount >= 1000 && kettle.temperature >= 40 && !kettle.getItem(SLOT_ITEM_INSERT).isEmpty() && (kettle.getItem(SLOT_ITEM_INSERT).is(TTItemTags.BREWABLE_TEA_LEAVES) || level.getTeaTypeManager().allIngredients().test(kettle.getItem(SLOT_ITEM_INSERT))) && kettle.insertedStacks.size() < 9) {
            if (!kettle.items.get(SLOT_ITEM_INSERT).is(TTItemTags.BREWABLE_TEA_LEAVES) || !kettle.contains(Ingredient.of(TTItemTags.BREWABLE_TEA_LEAVES))) {
                kettle.insertedStacks.add(kettle.getItem(SLOT_ITEM_INSERT).copy());
                kettle.items.set(SLOT_ITEM_INSERT, ItemStack.EMPTY);

                Optional<TeaType> matchingType = level.getTeaTypeManager().getValues().stream().filter(type -> {
                    List<Ingredient> list = type.getIngredients(level.getTeaTypeManager()).stream().map(Ingredient::of).toList();
                    if (list.size() != kettle.insertedStacks.size()) return false;
                    for (Ingredient i : list) {
                        if (!kettle.contains(i)) return false;
                    }
                    return true;
                }).findFirst();

                matchingType.ifPresentOrElse(type -> kettle.teaType = level.getTeaTypeManager().getKey(type), () -> kettle.teaType = TeaTypeManager.EMPTY);

                changed = true;
            }
        }

        if (kettle.getItem(SLOT_WATER_INSERT).is(Items.WATER_BUCKET) && kettle.waterAmount <= 0) {
            kettle.waterAmount = 1000;
            kettle.setItem(SLOT_WATER_INSERT, new ItemStack(Items.BUCKET));
            changed = true;
        }

        if (changed) {
            kettle.setChanged();
        }
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    public ResourceKey<TeaType> getTeaType() {
        return teaType;
    }

    public List<ItemStack> getInsertedItems() {
        return insertedStacks;
    }

    public int getWaterAmount() {
        return waterAmount;
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        temperature = nbt.getInt("Temperature");
        items.clear();
        ContainerHelper.loadAllItems(nbt, items);

        ListTag insertedItems = nbt.getList("InsertedItems", Tag.TAG_COMPOUND);
        insertedStacks.clear();
        for(int i = 0; i < insertedItems.size(); ++i) {
            CompoundTag compoundTag = insertedItems.getCompound(i);
            insertedStacks.add(ItemStack.of(compoundTag));
        }

        waterAmount = nbt.getInt("Water");
        teaType = ResourceKey.create(TeaTypeManager.TEA_TYPE, new ResourceLocation(nbt.getString("TeaType")));
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("Temperature", temperature);
        ContainerHelper.saveAllItems(nbt, items);

        ListTag inserted = new ListTag();

        for (ItemStack itemStack : insertedStacks) {
            if (!itemStack.isEmpty()) {
                inserted.add(itemStack.save(new CompoundTag()));
            }
        }

        nbt.put("InsertedItems", inserted);
        nbt.putInt("Water", waterAmount);
        nbt.putString("TeaType", teaType.location().toString());
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null) {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, 3);
        }
    }

    public ItemStack createDroppedStack() {
        ItemStack stack = new ItemStack(TTItems.TEA_KETTLE);
        CompoundTag tag = stack.getOrCreateTag();
        saveAdditional(tag);
        if (hasCustomName()) {
            stack.setHoverName(getCustomName());
        }
        return stack;
    }

    public boolean isHeated() {
        if (level == null) return false;
        BlockState below = level.getBlockState(getBlockPos().below());
        if (!below.is(TTBlockTags.HEATING_BLOCKS)) return false;
        if (below.hasProperty(BlockStateProperties.LIT)) {
            return below.getValue(BlockStateProperties.LIT);
        }
        return true;
    }

    public boolean contains(Ingredient ingredient) {
        for (ItemStack stack : insertedStacks) {
            if (ingredient.test(stack)) return true;
        }
        return false;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.tea_kettle");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new TeaKettleMenu(containerId, inventory, this, containerData);
    }

    @Override
    public int getContainerSize() {
        return CONTAINER_SIZE;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack item : items) {
            if (item.isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ContainerHelper.removeItem(items, slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
    }

    @Override
    public boolean stillValid(Player player) {
        if (level == null) return false;
        if (this.level.getBlockEntity(worldPosition) != this) {
            return false;
        } else {
            return player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) <= 64.0;
        }
    }

    @Override
    public void clearContent() {
        items.clear();
        insertedStacks.clear();
    }
}
