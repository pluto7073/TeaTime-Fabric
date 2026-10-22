package ml.pluto7073.teatime.block.entity;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import ml.pluto7073.teatime.gui.handlers.SteamerMenu;
import ml.pluto7073.teatime.recipe.SteamerRecipeInput;
import ml.pluto7073.teatime.recipe.TTRecipes;
import ml.pluto7073.teatime.recipe.SteamerRecipe;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SteamerBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, StackedContentsCompatible {

    public static final int INPUT_SLOT_INDEX = 0;
    public static final int WATER_SLOT_INDEX = 1;
    public static final int OUTPUT_SLOT_INDEX = 2;
    public static final int WATER_TIME_PROPERTY_INDEX = 0;
    private static final int[] TOP_SLOTS = {INPUT_SLOT_INDEX};
    private static final int[] SIDE_SLOTS = {WATER_SLOT_INDEX};
    private static final int[] BOTTOM_SLOTS = {OUTPUT_SLOT_INDEX, WATER_SLOT_INDEX};
    public static final int TOTAL_WATER_PROPERTY_INDEX = 1;
    public static final int STEAM_TIME_PROPERTY_INDEX = 2;
    public static final int TOTAL_STEAM_TIME_PROPERTY_INDEX = 3;
    public static final int IS_BOILING_PROPERTY_INDEX = 4;
    public static final int PROPERTY_COUNT = 5;
    public static final int DEFAULT_STEAM_TIME = 1200;
    protected NonNullList<ItemStack> inventory;
    public int waterTime;
    public int totalWater;
    public int steamTime;
    public int steamTimeTotal;
    public final ContainerData propertyDelegate;
    private final Object2IntOpenHashMap<ResourceLocation> recipesUsed;
    private final RecipeManager.CachedCheck<SteamerRecipeInput, SteamerRecipe> matchGetter;

    public SteamerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(TTBlockEntityTypes.STEAMER, blockPos, blockState);
        this.inventory = NonNullList.withSize(3, ItemStack.EMPTY);
        this.propertyDelegate = new ContainerData() {
            @Override
            public int get(int index) {
                switch (index) {
                    case WATER_TIME_PROPERTY_INDEX -> {
                        return SteamerBlockEntity.this.waterTime;
                    }
                    case TOTAL_WATER_PROPERTY_INDEX -> {
                        return SteamerBlockEntity.this.totalWater;
                    }
                    case STEAM_TIME_PROPERTY_INDEX -> {
                        return SteamerBlockEntity.this.steamTime;
                    }
                    case TOTAL_STEAM_TIME_PROPERTY_INDEX -> {
                        return SteamerBlockEntity.this.steamTimeTotal;
                    }
                    case IS_BOILING_PROPERTY_INDEX -> {
                        return SteamerBlockEntity.this.isBoiling(blockPos, level) ? 1 : 0;
                    }
                    default -> {
                        return 0;
                    }
                }
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case WATER_TIME_PROPERTY_INDEX:
                        SteamerBlockEntity.this.waterTime = value;
                        break;
                    case TOTAL_WATER_PROPERTY_INDEX:
                        SteamerBlockEntity.this.totalWater = value;
                        break;
                    case STEAM_TIME_PROPERTY_INDEX:
                        SteamerBlockEntity.this.steamTime = value;
                        break;
                    case TOTAL_STEAM_TIME_PROPERTY_INDEX:
                        SteamerBlockEntity.this.steamTimeTotal = value;
                        break;
                    case IS_BOILING_PROPERTY_INDEX:
                        break;
                }
            }

            @Override
            public int getCount() {
                return PROPERTY_COUNT;
            }
        };
        this.recipesUsed = new Object2IntOpenHashMap<>();
        this.matchGetter = RecipeManager.createCheck(TTRecipes.STEAMING);
    }

    public static Map<Item, Integer> createWaterTimeMap() {
        Map<Item, Integer> map = Maps.newLinkedHashMap();
        addWaterInput(map, Items.WATER_BUCKET, 20000);
        addWaterInput(map, Items.POTION, 6666);
        return map;
    }

    private static void addWaterInput(Map<Item, Integer> waterSizes, ItemLike item, int waterSize) {
        waterSizes.put(item.asItem(), waterSize);
    }

    public boolean isBoiling(BlockPos pos, @Nullable Level level) {
        if (level == null) return false;
        BlockState campfire = level.getBlockState(pos.below());
        if (!campfire.is(BlockTags.CAMPFIRES)) {
            return false;
        }
        if (!CampfireBlock.isLitCampfire(campfire)) {
            return false;
        }
        return this.waterTime > 0;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.steamer");
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        inventory.clear();
        for (int i = 0; i < items.size(); i++) {
            inventory.set(i, items.get(i));
        }
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
        super.loadAdditional(nbt, provider);
        this.inventory = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(nbt, this.inventory, provider);
        this.waterTime = nbt.getShort("WaterTime");
        this.steamTime = nbt.getShort("SteamTime");
        this.steamTimeTotal = nbt.getShort("SteamTimeTotal");
        this.totalWater = getWaterSize(this.inventory.get(WATER_SLOT_INDEX));
        CompoundTag recipes = nbt.getCompound("RecipesUsed");
        for (String key : recipes.getAllKeys()) {
            this.recipesUsed.put(ResourceLocation.parse(key), recipes.getInt(key));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
        super.saveAdditional(nbt, provider);
        nbt.putShort("WaterTime", (short) this.waterTime);
        nbt.putShort("SteamTime", (short) this.steamTime);
        nbt.putShort("SteamTimeTotal", (short) this.steamTimeTotal);
        ContainerHelper.saveAllItems(nbt, this.inventory, provider);
        CompoundTag recipes = new CompoundTag();
        this.recipesUsed.forEach((identifier, count) -> recipes.putInt(identifier.toString(), count));
        nbt.put("RecipesUsed", recipes);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SteamerBlockEntity blockEntity) {
        boolean boiling = blockEntity.isBoiling(pos, level);
        boolean dirty = false;
        if (blockEntity.isBoiling(pos, level)) {
            --blockEntity.waterTime;
        }

        ItemStack waterStack = blockEntity.inventory.get(WATER_SLOT_INDEX);
        boolean hasInput = !blockEntity.inventory.get(INPUT_SLOT_INDEX).isEmpty();
        boolean hasWaterItem = !waterStack.isEmpty();
        if (blockEntity.isBoiling(pos, level) || hasInput && hasWaterItem) {
            RecipeHolder<SteamerRecipe> recipe;
            if (hasInput) {
                recipe = blockEntity.matchGetter.getRecipeFor(blockEntity.asInput(), level).orElse(null);
            } else {
                recipe = null;
            }

            int i = blockEntity.getMaxStackSize();
            if (!blockEntity.isBoiling(pos, level) && canAcceptRecipeOutput(recipe, blockEntity.inventory, i)) {
                blockEntity.waterTime = blockEntity.getWaterSize(waterStack);
                blockEntity.totalWater = blockEntity.waterTime;
                if (blockEntity.isBoiling(pos, level)) {
                    dirty = true;
                    if (hasWaterItem) {
                        Item item = waterStack.getItem();
                        waterStack.shrink(1);
                        if (waterStack.isEmpty()) {
                            Item remainder = item.getCraftingRemainingItem();
                            blockEntity.inventory.set(WATER_SLOT_INDEX, remainder == null ? ItemStack.EMPTY : new ItemStack(remainder));
                        }
                    }
                }
            }

            if (blockEntity.isBoiling(pos, level) && canAcceptRecipeOutput(recipe, blockEntity.inventory, i)) {
                ++blockEntity.steamTime;
                if (blockEntity.steamTime == blockEntity.steamTimeTotal) {
                    blockEntity.steamTime = 0;
                    blockEntity.steamTimeTotal = getSteamTime(level, blockEntity);
                    craftRecipe(recipe, blockEntity.inventory, i);

                    dirty = true;
                }
            } else {
                blockEntity.steamTime = 0;
            }
        } else if (!blockEntity.isBoiling(pos, level) && blockEntity.steamTime > 0) {
            blockEntity.steamTime = Mth.clamp(blockEntity.steamTime - 2, 0, blockEntity.steamTimeTotal);
        }

        if (boiling != blockEntity.isBoiling(pos, level)) {
            dirty = true;
        }

        if (dirty) {
            setChanged(level, pos, state);
        }
    }

    private static boolean canAcceptRecipeOutput(@Nullable RecipeHolder<SteamerRecipe> recipe, NonNullList<ItemStack> slots, int count) {
        if (!slots.get(INPUT_SLOT_INDEX).isEmpty() && recipe != null) {
            ItemStack wantedOutput = recipe.value().output;
            if (wantedOutput.isEmpty()) {
                return false;
            } else {
                ItemStack outputSlot = slots.get(OUTPUT_SLOT_INDEX);
                if (outputSlot.isEmpty()) {
                    return true;
                } else if (!outputSlot.getItem().equals(wantedOutput.getItem())) {
                    return false;
                } else if (outputSlot.getCount() < count && outputSlot.getCount() < outputSlot.getMaxStackSize()) {
                    return true;
                } else {
                    return outputSlot.getCount() < wantedOutput.getMaxStackSize();
                }
            }
        } else {
            return false;
        }
    }

    private static boolean craftRecipe(@Nullable RecipeHolder<SteamerRecipe> recipe, NonNullList<ItemStack> slots, int count) {
        if (recipe != null && canAcceptRecipeOutput(recipe, slots, count)) {
            ItemStack input = slots.get(INPUT_SLOT_INDEX);
            ItemStack output = recipe.value().output.copy();
            ItemStack outputSlot = slots.get(OUTPUT_SLOT_INDEX);
            if (outputSlot.isEmpty()) {
                slots.set(OUTPUT_SLOT_INDEX, output.copy());
            } else if (outputSlot.is(output.getItem())) {
                outputSlot.grow(1);
            }

            input.shrink(1);
            return true;
        } else {
            return false;
        }
    }

    protected int getWaterSize(ItemStack water) {
        if (water.isEmpty()) {
            return 0;
        } else {
            Item item = water.getItem();
            return createWaterTimeMap().getOrDefault(item, 0);
        }
    }

    public SteamerRecipeInput asInput() {
        return new SteamerRecipeInput(getItem(INPUT_SLOT_INDEX));
    }

    private static int getSteamTime(Level level, SteamerBlockEntity blockEntity) {
        return blockEntity.matchGetter.getRecipeFor(blockEntity.asInput(), level).map(RecipeHolder::value).map(SteamerRecipe::getSteamTime).orElse(DEFAULT_STEAM_TIME);
    }

    public static boolean canUseAsWater(ItemStack item) {
        return createWaterTimeMap().containsKey(item.getItem());
    }

    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.DOWN) {
            return BOTTOM_SLOTS;
        } else {
            return side == Direction.UP ? TOP_SLOTS : SIDE_SLOTS;
        }
    }

    @Override
    protected AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
        return new SteamerMenu(syncId, playerInventory, this, this.propertyDelegate);
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction dir) {
        return this.isValid(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        if (dir == Direction.DOWN && slot == WATER_SLOT_INDEX) {
            return stack.is(Items.BUCKET) || stack.is(Items.GLASS_BOTTLE);
        } else {
            return true;
        }
    }

    @Override
    public int getContainerSize() {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : inventory) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.inventory.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ContainerHelper.removeItem(this.inventory, slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.inventory, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        ItemStack s = this.inventory.get(slot);
        boolean canInsert = !stack.isEmpty() && stack.getItem().equals(s.getItem()) && ItemStack.isSameItemSameComponents(s, stack);
        this.inventory.set(slot, stack);
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }

        if (slot == 0 && !canInsert) {
            this.steamTimeTotal = getSteamTime(this.level, this);
            this.steamTime = 0;
            this.setChanged();
        }
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.level.getBlockEntity(worldPosition) != this) {
            return false;
        } else {
            return player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) <= 64.0;
        }
    }

    public boolean isValid(int slot, ItemStack stack) {
        if (slot == OUTPUT_SLOT_INDEX) {
            return false;
        } else if (slot != WATER_SLOT_INDEX) {
            return true;
        } else {
            return canUseAsWater(stack);
        }
    }

    @Override
    public void fillStackedContents(StackedContents contents) {
        for (ItemStack stack : this.inventory) {
            contents.accountStack(stack);
        }
    }

    @Override
    public void clearContent() {
        this.inventory.clear();
    }

}
