package ml.pluto7073.teatime.block.entity;

import com.google.common.collect.Lists;
import ml.pluto7073.teatime.TTRegistries;
import ml.pluto7073.teatime.item.KettleData;
import ml.pluto7073.teatime.component.TTComponents;
import ml.pluto7073.teatime.gui.handlers.TeaKettleMenu;
import ml.pluto7073.teatime.item.TTItems;
import ml.pluto7073.teatime.tags.TTBlockTags;
import ml.pluto7073.teatime.tags.TTItemTags;
import ml.pluto7073.teatime.teatypes.TeaType;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
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
    private Holder<TeaType> teaType = TeaType.EMPTY;

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

        if (kettle.waterAmount >= 1000 && kettle.temperature >= 40 && !kettle.getItem(SLOT_ITEM_INSERT).isEmpty() && kettle.insertedStacks.size() < 9) {
            if (!kettle.items.get(SLOT_ITEM_INSERT).is(TTItemTags.BREWABLE_TEA_LEAVES) || !kettle.contains(Ingredient.of(TTItemTags.BREWABLE_TEA_LEAVES))) {
                kettle.insertedStacks.add(kettle.getItem(SLOT_ITEM_INSERT).copy());
                kettle.items.set(SLOT_ITEM_INSERT, ItemStack.EMPTY);

                Optional<Holder.Reference<TeaType>> matchingType =
                        TeaType.getFromIngredients(kettle.insertedStacks, level.registryAccess());

                matchingType.ifPresentOrElse(type -> kettle.teaType = type, () -> kettle.teaType = TeaType.EMPTY);

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
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, provider);
        return tag;
    }

    public Holder<TeaType> getTeaType() {
        return teaType;
    }

    public List<ItemStack> getInsertedItems() {
        return insertedStacks;
    }

    public int getWaterAmount() {
        return waterAmount;
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
        super.loadAdditional(nbt, provider);
        temperature = nbt.getInt("Temperature");
        items.clear();
        ContainerHelper.loadAllItems(nbt, items, provider);

        ListTag insertedItems = nbt.getList("InsertedItems", Tag.TAG_COMPOUND);
        insertedStacks.clear();
        for(int i = 0; i < insertedItems.size(); ++i) {
            CompoundTag compoundTag = insertedItems.getCompound(i);
            insertedStacks.add(ItemStack.parseOptional(provider, compoundTag));
        }

        waterAmount = nbt.getInt("Water");
        teaType = nbt.contains("TeaType") ?
                provider.lookupOrThrow(TTRegistries.TEA_TYPE)
                        .getOrThrow(ResourceKey.create(TTRegistries.TEA_TYPE, ResourceLocation.parse(nbt.getString("TeaType")))) :
                TeaType.EMPTY;
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
        super.saveAdditional(nbt, provider);
        nbt.putInt("Temperature", temperature);
        ContainerHelper.saveAllItems(nbt, items, provider);

        ListTag inserted = new ListTag();

        for (ItemStack itemStack : insertedStacks) {
            if (!itemStack.isEmpty()) {
                inserted.add(itemStack.saveOptional(provider));
            }
        }

        nbt.put("InsertedItems", inserted);
        nbt.putInt("Water", waterAmount);
        if (teaType != TeaType.EMPTY) {
            nbt.putString("TeaType", teaType.unwrapKey().orElseThrow().location().toString());
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null) {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, 3);
        }
    }

    public void loadFromKettleData(KettleData data) {
        temperature = data.temperature();
        for (int i = 0; i < data.items().size(); i++) {
            items.set(i, data.items().get(i));
        }

        insertedStacks.clear();
        insertedStacks.addAll(data.insertedItems());
        waterAmount = data.waterAmount();
        teaType = data.teaType();
    }

    public ItemStack createDroppedStack() {
        ItemStack stack = new ItemStack(TTItems.TEA_KETTLE);
        KettleData data = new KettleData(temperature, List.copyOf(items), List.copyOf(insertedStacks), waterAmount, teaType);
        stack.set(TTComponents.KETTLE_DATA, data);
        if (hasCustomName()) {
            stack.set(DataComponents.CUSTOM_NAME, getCustomName());
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
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        for (int i = 0; i < this.items.size(); i++) {
            this.items.set(i, items.get(i));
        }
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
