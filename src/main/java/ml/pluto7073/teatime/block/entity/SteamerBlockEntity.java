package ml.pluto7073.teatime.block.entity;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import ml.pluto7073.teatime.gui.handlers.SteamerScreenHandler;
import ml.pluto7073.teatime.recipe.ModRecipes;
import ml.pluto7073.teatime.recipe.SteamerRecipe;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.*;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class SteamerBlockEntity extends LockableContainerBlockEntity implements SidedInventory, RecipeUnlocker, RecipeInputProvider {

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
    protected DefaultedList<ItemStack> inventory;
    public int waterTime;
    public int totalWater;
    public int steamTime;
    public int steamTimeTotal;
    public final PropertyDelegate propertyDelegate;
    private final Object2IntOpenHashMap<Identifier> recipesUsed;
    private final RecipeManager.MatchGetter<Inventory, ? extends SteamerRecipe> matchGetter;

    public SteamerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntityTypes.STEAMER_TYPE, blockPos, blockState);
        this.inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
        this.propertyDelegate = new PropertyDelegate() {
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
                        return SteamerBlockEntity.this.isBoiling(blockPos, world) ? 1 : 0;
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
            public int size() {
                return PROPERTY_COUNT;
            }
        };
        this.recipesUsed = new Object2IntOpenHashMap<>();
        this.matchGetter = RecipeManager.createCachedMatchGetter(ModRecipes.STEAMING);
    }

    public static Map<Item, Integer> createWaterTimeMap() {
        Map<Item, Integer> map = Maps.newLinkedHashMap();
        addWaterInput(map, Items.WATER_BUCKET, 20000);
        addWaterInput(map, Items.POTION, 6666);
        return map;
    }

    private static void addWaterInput(Map<Item, Integer> waterSizes, ItemConvertible item, int waterSize) {
        waterSizes.put(item.asItem(), waterSize);
    }

    public boolean isBoiling(BlockPos pos, World world) {
        BlockState campfire = world.getBlockState(pos.down());
        if (!campfire.isIn(BlockTags.CAMPFIRES)) {
            return false;
        }
        if (!CampfireBlock.isLitCampfire(campfire)) {
            return false;
        }
        return this.waterTime > 0;
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable("container.steamer");
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.readNbt(nbt, this.inventory);
        this.waterTime = nbt.getShort("WaterTime");
        this.steamTime = nbt.getShort("SteamTime");
        this.steamTimeTotal = nbt.getShort("SteamTimeTotal");
        this.totalWater = getWaterSize(this.inventory.get(WATER_SLOT_INDEX));
        NbtCompound recipes = nbt.getCompound("RecipesUsed");
        for (String key : recipes.getKeys()) {
            this.recipesUsed.put(new Identifier(key), recipes.getInt(key));
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putShort("WaterTime", (short) this.waterTime);
        nbt.putShort("SteamTime", (short) this.steamTime);
        nbt.putShort("SteamTimeTotal", (short) this.steamTimeTotal);
        Inventories.writeNbt(nbt, this.inventory);
        NbtCompound recipes = new NbtCompound();
        this.recipesUsed.forEach((identifier, count) -> recipes.putInt(identifier.toString(), count));
        nbt.put("RecipesUsed", recipes);
    }

    public static void tick(World world, BlockPos pos, BlockState state, SteamerBlockEntity blockEntity) {
        boolean boiling = blockEntity.isBoiling(pos, world);
        boolean dirty = false;
        if (blockEntity.isBoiling(pos, world)) {
            --blockEntity.waterTime;
        }

        ItemStack waterStack = blockEntity.inventory.get(WATER_SLOT_INDEX);
        boolean hasInput = !blockEntity.inventory.get(INPUT_SLOT_INDEX).isEmpty();
        boolean hasWaterItem = !waterStack.isEmpty();
        if (blockEntity.isBoiling(pos, world) || hasInput && hasWaterItem) {
            SteamerRecipe recipe;
            if (hasInput) {
                recipe = blockEntity.matchGetter.getFirstMatch(blockEntity, world).orElse(null);
            } else {
                recipe = null;
            }

            int i = blockEntity.getMaxCountPerStack();
            if (!blockEntity.isBoiling(pos, world) && canAcceptRecipeOutput(recipe, blockEntity.inventory, i)) {
                blockEntity.waterTime = blockEntity.getWaterSize(waterStack);
                blockEntity.totalWater = blockEntity.waterTime;
                if (blockEntity.isBoiling(pos, world)) {
                    dirty = true;
                    if (hasWaterItem) {
                        Item item = waterStack.getItem();
                        waterStack.decrement(1);
                        if (waterStack.isEmpty()) {
                            Item remainder = item.getRecipeRemainder();
                            blockEntity.inventory.set(WATER_SLOT_INDEX, remainder == null ? ItemStack.EMPTY : new ItemStack(remainder));
                        }
                    }
                }
            }

            if (blockEntity.isBoiling(pos, world) && canAcceptRecipeOutput(recipe, blockEntity.inventory, i)) {
                ++blockEntity.steamTime;
                if (blockEntity.steamTime == blockEntity.steamTimeTotal) {
                    blockEntity.steamTime = 0;
                    blockEntity.steamTimeTotal = getSteamTime(world, blockEntity);
                    if (craftRecipe(recipe, blockEntity.inventory, i)) {
                        blockEntity.setLastRecipe(recipe);
                    }

                    dirty = true;
                }
            } else {
                blockEntity.steamTime = 0;
            }
        } else if (!blockEntity.isBoiling(pos, world) && blockEntity.steamTime > 0) {
            blockEntity.steamTime = MathHelper.clamp(blockEntity.steamTime - 2, 0, blockEntity.steamTimeTotal);
        }

        if (boiling != blockEntity.isBoiling(pos, world)) {
            dirty = true;
        }

        if (dirty) {
            markDirty(world, pos, state);
        }
    }

    private static boolean canAcceptRecipeOutput(@Nullable Recipe<?> recipe, DefaultedList<ItemStack> slots, int count) {
        if (!slots.get(INPUT_SLOT_INDEX).isEmpty() && recipe != null) {
            ItemStack wantedOutput = recipe.getOutput();
            if (wantedOutput.isEmpty()) {
                return false;
            } else {
                ItemStack outputSlot = slots.get(OUTPUT_SLOT_INDEX);
                if (outputSlot.isEmpty()) {
                    return true;
                } else if (!outputSlot.isItemEqualIgnoreDamage(wantedOutput)) {
                    return false;
                } else if (outputSlot.getCount() < count && outputSlot.getCount() < outputSlot.getMaxCount()) {
                    return true;
                } else {
                    return outputSlot.getCount() < wantedOutput.getMaxCount();
                }
            }
        } else {
            return false;
        }
    }

    private static boolean craftRecipe(@Nullable Recipe<?> recipe, DefaultedList<ItemStack> slots, int count) {
        if (recipe != null && canAcceptRecipeOutput(recipe, slots, count)) {
            ItemStack input = slots.get(INPUT_SLOT_INDEX);
            ItemStack output = recipe.getOutput();
            ItemStack outputSlot = slots.get(OUTPUT_SLOT_INDEX);
            if (outputSlot.isEmpty()) {
                slots.set(OUTPUT_SLOT_INDEX, output.copy());
            } else if (outputSlot.isOf(output.getItem())) {
                outputSlot.increment(1);
            }

            input.decrement(1);
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

    private static int getSteamTime(World world, SteamerBlockEntity blockEntity) {
        return blockEntity.matchGetter.getFirstMatch(blockEntity, world).map(SteamerRecipe::getSteamTime).orElse(DEFAULT_STEAM_TIME);
    }

    public static boolean canUseAsWater(ItemStack item) {
        return createWaterTimeMap().containsKey(item.getItem());
    }

    public int[] getAvailableSlots(Direction side) {
        if (side == Direction.DOWN) {
            return BOTTOM_SLOTS;
        } else {
            return side == Direction.UP ? TOP_SLOTS : SIDE_SLOTS;
        }
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new SteamerScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return this.isValid(slot, stack);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        if (dir == Direction.DOWN && slot == WATER_SLOT_INDEX) {
            return stack.isOf(Items.BUCKET) || stack.isOf(Items.GLASS_BOTTLE);
        } else {
            return true;
        }
    }

    @Override
    public int size() {
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
    public ItemStack getStack(int slot) {
        return this.inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(this.inventory, slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.inventory, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        ItemStack s = this.inventory.get(slot);
        boolean canInsert = !stack.isEmpty() && stack.isItemEqualIgnoreDamage(s) && ItemStack.areEqual(s, stack);
        this.inventory.set(slot, stack);
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }

        if (slot == 0 && !canInsert) {
            this.steamTimeTotal = getSteamTime(this.world, this);
            this.steamTime = 0;
            this.markDirty();
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (this.world.getBlockEntity(this.pos) != this) {
            return false;
        } else {
            return player.squaredDistanceTo(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5) <= 64.0;
        }
    }

    @Override
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
    public void provideRecipeInputs(RecipeMatcher finder) {
        for (ItemStack stack : this.inventory) {
            finder.addInput(stack);
        }
    }

    @Override
    public void setLastRecipe(@Nullable Recipe<?> recipe) {
        if (recipe != null) {
            Identifier identifier = recipe.getId();
            this.recipesUsed.addTo(identifier, 1);
        }
    }

    @Override
    public void unlockLastRecipe(PlayerEntity player) {}

    @Nullable
    @Override
    public Recipe<?> getLastRecipe() {
        return null;
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }
}
