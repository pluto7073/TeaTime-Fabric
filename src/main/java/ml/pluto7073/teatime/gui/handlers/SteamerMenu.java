package ml.pluto7073.teatime.gui.handlers;

import ml.pluto7073.teatime.block.entity.SteamerBlockEntity;
import ml.pluto7073.teatime.recipe.TTRecipes;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

import static ml.pluto7073.teatime.block.entity.SteamerBlockEntity.*;

@MethodsReturnNonnullByDefault
public class SteamerMenu extends RecipeBookMenu<Container> {

    private final Container container;
    private final ContainerData propertyDelegate;
    protected final Level world;
    private final RecipeBookType category;

    public SteamerMenu(int syncId, Inventory inventory) {
        this(syncId, inventory, new SimpleContainer(3), new SimpleContainerData(PROPERTY_COUNT));
    }

    public SteamerMenu(int syncId, Inventory inventory, Container container, ContainerData propertyDelegate) {
        super(TTMenuTypes.STEAMER, syncId);
        this.category = RecipeBookType.CRAFTING;
        checkContainerSize(container, 3);
        checkContainerDataCount(propertyDelegate, PROPERTY_COUNT);
        this.container = container;
        this.propertyDelegate = propertyDelegate;
        this.world = inventory.player.level();
        this.addSlot(new Slot(container, INPUT_SLOT_INDEX, 56, 17));
        this.addSlot(new SteamerWaterSlot(this, container, WATER_SLOT_INDEX, 56, 53));
        this.addSlot(new SteamerOutputSlot(inventory.player, container, OUTPUT_SLOT_INDEX, 116, 35));

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(inventory, i, 8 + i * 18, 142));
        }

        this.addDataSlots(propertyDelegate);
    }

    @Override
    public void fillCraftSlotsStackedContents(StackedContents itemHelper) {
        if (this.container instanceof StackedContentsCompatible) {
            ((StackedContentsCompatible) this.container).fillStackedContents(itemHelper);
        }
    }

    @Override
    public void clearCraftingContent() {
        this.getSlot(INPUT_SLOT_INDEX).set(ItemStack.EMPTY);
        this.getSlot(OUTPUT_SLOT_INDEX).set(ItemStack.EMPTY);
    }

    @Override
    public boolean recipeMatches(Recipe<? super Container> recipe) {
        return recipe.matches(this.container, this.world);
    }

    @Override
    public int getResultSlotIndex() {
        return OUTPUT_SLOT_INDEX;
    }

    @Override
    public int getGridWidth() {
        return 1;
    }

    @Override
    public int getGridHeight() {
        return 1;
    }

    @Override
    public int getSize() {
        return 3;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            stack = slotStack.copy();
            if (index == OUTPUT_SLOT_INDEX) {
                if (!this.moveItemStackTo(slotStack, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(slotStack, stack);
            } else if (index != WATER_SLOT_INDEX && index != INPUT_SLOT_INDEX) {
                if (this.isSteamable(slotStack)) {
                    if (!this.moveItemStackTo(slotStack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.isWater(slotStack)) {
                    if (!this.moveItemStackTo(slotStack, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 3 && index < 30) {
                    if (!this.moveItemStackTo(slotStack, 30, 39, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 30 && index < 39 && !this.moveItemStackTo(slotStack, 3, 30, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(slotStack, 3, 39, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (slotStack.getCount() == stack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, slotStack);
        }

        return stack;
    }

    protected boolean isSteamable(ItemStack stack) {
        return this.world.getRecipeManager().getRecipeFor(TTRecipes.STEAMING, new SimpleContainer(stack), this.world).isPresent();
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
    public RecipeBookType getRecipeBookType() {
        return this.category;
    }

    @Override
    public boolean shouldMoveToInventory(int index) {
        return index != WATER_SLOT_INDEX;
    }

    public static class SteamerWaterSlot extends Slot {
        private final SteamerMenu handler;

        public SteamerWaterSlot(SteamerMenu handler, Container container, int index, int x, int y) {
            super(container, index, x, y);
            this.handler = handler;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return this.handler.isWater(stack);
        }
    }

    public static class SteamerOutputSlot extends Slot {
        private final Player player;
        private int amount;

        public SteamerOutputSlot(Player entity, Container container, int index, int x, int y) {
            super(container, index, x, y);
            this.player = entity;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public ItemStack remove(int amount) {
            if (this.hasItem()) {
                this.amount += Math.min(amount, this.getItem().getCount());
            }
            return super.remove(amount);
        }

        @Override
        public void onTake(Player player, ItemStack stack) {
            this.checkTakeAchievements(stack);
            super.onTake(player, stack);
        }

        @Override
        protected void onQuickCraft(ItemStack stack, int amount) {
            this.amount += amount;
            this.checkTakeAchievements(stack);
        }

        @Override
        protected void checkTakeAchievements(ItemStack stack) {
            stack.onCraftedBy(this.player.level(), this.player, this.amount);
            this.amount = 0;
        }
    }

}
