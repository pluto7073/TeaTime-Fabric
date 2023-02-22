package ml.pluto7073.teatime.gui.handlers;

import ml.pluto7073.teatime.block.entity.SteamerBlockEntity;
import ml.pluto7073.teatime.recipe.ModRecipes;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static ml.pluto7073.teatime.block.entity.SteamerBlockEntity.*;

public class SteamerScreenHandler extends AbstractRecipeScreenHandler<Inventory> {

    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    protected final World world;
    private final RecipeBookCategory category;

    public SteamerScreenHandler(int syncId, PlayerInventory inventory) {
        this(syncId, inventory, new SimpleInventory(3), new ArrayPropertyDelegate(PROPERTY_COUNT));
    }

    public SteamerScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(ModScreenHandlerTypes.STEAMER_SCREEN_HANDLER, syncId);
        this.category = RecipeBookCategory.CRAFTING;
        checkSize(inventory, 3);
        checkDataCount(propertyDelegate, PROPERTY_COUNT);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        this.world = playerInventory.player.world;
        this.addSlot(new Slot(inventory, INPUT_SLOT_INDEX, 56, 17));
        this.addSlot(new SteamerWaterSlot(this, inventory, WATER_SLOT_INDEX, 56, 53));
        this.addSlot(new SteamerOutputSlot(playerInventory.player, inventory, OUTPUT_SLOT_INDEX, 116, 35));

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }

        this.addProperties(propertyDelegate);
    }

    @Override
    public void populateRecipeFinder(RecipeMatcher finder) {
        if (this.inventory instanceof RecipeInputProvider inputProvider) {
            inputProvider.provideRecipeInputs(finder);
        }
    }

    @Override
    public void clearCraftingSlots() {
        this.getSlot(INPUT_SLOT_INDEX).setStack(ItemStack.EMPTY);
        this.getSlot(OUTPUT_SLOT_INDEX).setStack(ItemStack.EMPTY);
    }

    @Override
    public boolean matches(Recipe<? super Inventory> recipe) {
        return recipe.matches(this.inventory, this.world);
    }

    @Override
    public int getCraftingResultSlotIndex() {
        return OUTPUT_SLOT_INDEX;
    }

    @Override
    public int getCraftingWidth() {
        return 1;
    }

    @Override
    public int getCraftingHeight() {
        return 1;
    }

    @Override
    public int getCraftingSlotCount() {
        return 3;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack slotStack = slot.getStack();
            stack = slotStack.copy();
            if (index == OUTPUT_SLOT_INDEX) {
                if (!this.insertItem(slotStack, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickTransfer(slotStack, stack);
            } else if (index != WATER_SLOT_INDEX && index != INPUT_SLOT_INDEX) {
                if (this.isSteamable(slotStack)) {
                    if (!this.insertItem(slotStack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.isWater(slotStack)) {
                    if (!this.insertItem(slotStack, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 3 && index < 30) {
                    if (!this.insertItem(slotStack, 30, 39, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 30 && index < 39 && !this.insertItem(slotStack, 3, 30, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(slotStack, 3, 39, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (slotStack.getCount() == stack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTakeItem(player, slotStack);
        }

        return stack;
    }

    protected boolean isSteamable(ItemStack stack) {
        return this.world.getRecipeManager().getFirstMatch(ModRecipes.STEAMING, new SimpleInventory(stack), this.world).isPresent();
    }

    protected boolean isWater(ItemStack stack) {
        return SteamerBlockEntity.canUseAsWater(stack);
    }

    public int getSteamProgress() {
        int steamTime = this.propertyDelegate.get(STEAM_TIME_PROPERTY_INDEX);
        int steamTimeTotal = this.propertyDelegate.get(TOTAL_STEAM_TIME_PROPERTY_INDEX);
        return steamTimeTotal != 0 && steamTime != 0 ? steamTime * 24 / steamTimeTotal : 0;
    }

    public int getWaterProgress() {
        int waterTotal = this.propertyDelegate.get(TOTAL_WATER_PROPERTY_INDEX);
        if (waterTotal == 0) {
            waterTotal = 200;
        }

        return this.propertyDelegate.get(WATER_TIME_PROPERTY_INDEX) * 13 / waterTotal;
    }

    public boolean isBoiling() {
        return this.propertyDelegate.get(IS_BOILING_PROPERTY_INDEX) == 1;
    }

    @Override
    public RecipeBookCategory getCategory() {
        return this.category;
    }

    @Override
    public boolean canInsertIntoSlot(int index) {
        return index != WATER_SLOT_INDEX;
    }

    public static class SteamerWaterSlot extends Slot {
        private final SteamerScreenHandler handler;

        public SteamerWaterSlot(SteamerScreenHandler handler, Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
            this.handler = handler;
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return this.handler.isWater(stack);
        }
    }

    public static class SteamerOutputSlot extends Slot {
        private final PlayerEntity player;
        private int amount;

        public SteamerOutputSlot(PlayerEntity entity, Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
            this.player = entity;
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return false;
        }

        @Override
        public ItemStack takeStack(int amount) {
            if (this.hasStack()) {
                this.amount += Math.min(amount, this.getStack().getCount());
            }
            return super.takeStack(amount);
        }

        @Override
        public void onTakeItem(PlayerEntity player, ItemStack stack) {
            this.onCrafted(stack);
            super.onTakeItem(player, stack);
        }

        @Override
        protected void onCrafted(ItemStack stack, int amount) {
            this.amount += amount;
            this.onCrafted(stack);
        }

        @Override
        protected void onCrafted(ItemStack stack) {
            stack.onCraft(this.player.world, this.player, this.amount);
            this.amount = 0;
        }
    }

}
