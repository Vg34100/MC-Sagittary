package net.vg.sagittary.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.vg.sagittary.component.ArrowComponent;
import net.vg.sagittary.item.ComponentArrowItem;
import net.vg.sagittary.registry.ObjectRegistry;

public class FletchingTableMenu extends AbstractContainerMenu {
    private final Container fletchingContainer;
    private final ResultContainer resultContainer;
    private final ContainerLevelAccess access;
    private final Player player;
    
    // Slot indices
    private static final int TIP_SLOT = 0;
    private static final int SHAFT_SLOT = 1;
    private static final int FLETCHING_SLOT = 2;
    private static final int RESULT_SLOT = 3;
    
    public FletchingTableMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, BlockPos.ZERO);
    }
    
    public FletchingTableMenu(int containerId, Inventory playerInventory, BlockPos pos) {
        super(ObjectRegistry.FLETCHING_TABLE_MENU_TYPE.get(), containerId);
        this.player = playerInventory.player;
        this.fletchingContainer = new SimpleContainer(3) {
            @Override
            public void setChanged() {
                super.setChanged();
                FletchingTableMenu.this.slotsChanged(this);
            }
        };
        this.resultContainer = new ResultContainer();
        this.access = ContainerLevelAccess.create(playerInventory.player.level(), pos);
        
        // Add component slots (tip, shaft, fletching)
        this.addSlot(new FletchingSlot(fletchingContainer, TIP_SLOT, 27, 47, ArrowComponent.ComponentType.TIP));
        this.addSlot(new FletchingSlot(fletchingContainer, SHAFT_SLOT, 76, 47, ArrowComponent.ComponentType.SHAFT));
        this.addSlot(new FletchingSlot(fletchingContainer, FLETCHING_SLOT, 125, 47, ArrowComponent.ComponentType.FLETCHING));
        
        // Add result slot using separate ResultContainer - positioned above shaft slot
        this.addSlot(new ResultSlot(resultContainer, 0, 76, 24));
        
        // Add player inventory slots
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        
        // Add player hotbar slots
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
    
    @Override
    public void slotsChanged(Container container) {
        if (container == this.fletchingContainer) {
            this.updateResult();
        }
        super.slotsChanged(container);
    }
    
    private void updateResult() {
        ItemStack tipStack = this.fletchingContainer.getItem(TIP_SLOT);
        ItemStack shaftStack = this.fletchingContainer.getItem(SHAFT_SLOT);
        ItemStack fletchingStack = this.fletchingContainer.getItem(FLETCHING_SLOT);
        
        if (!tipStack.isEmpty() && !shaftStack.isEmpty() && !fletchingStack.isEmpty()) {
            // Validate components
            ArrowComponent tip = getComponentFromItem(tipStack, ArrowComponent.ComponentType.TIP);
            ArrowComponent shaft = getComponentFromItem(shaftStack, ArrowComponent.ComponentType.SHAFT);
            ArrowComponent fletching = getComponentFromItem(fletchingStack, ArrowComponent.ComponentType.FLETCHING);
            
            if (tip != null && shaft != null && fletching != null) {
                ItemStack result = ComponentArrowItem.createComponentArrow(tip, shaft, fletching);
                result.setCount(6); // Make 6 arrows like bulk crafting
                this.resultContainer.setItem(0, result);
            } else {
                this.resultContainer.setItem(0, ItemStack.EMPTY);
            }
        } else {
            this.resultContainer.setItem(0, ItemStack.EMPTY);
        }
    }
    
    private ArrowComponent getComponentFromItem(ItemStack stack, ArrowComponent.ComponentType type) {
        // Check if the item is valid for this component type
        for (ArrowComponent component : ArrowComponent.values()) {
            if (component.getType() == type && component.getCraftingItem() == stack.getItem()) {
                return component;
            }
        }
        return null;
    }
    
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        
        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemStack = stackInSlot.copy();
            
            if (index == RESULT_SLOT) {
                // Taking from result slot
                if (!this.moveItemStackTo(stackInSlot, 4, 40, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stackInSlot, itemStack);
            } else if (index >= 4) {
                // Moving from player inventory to fletching slots
                if (canPlaceInFletchingSlot(stackInSlot)) {
                    if (!this.moveItemStackTo(stackInSlot, 0, 3, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 31) {
                    if (!this.moveItemStackTo(stackInSlot, 31, 40, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(stackInSlot, 4, 31, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Moving from fletching slots to player inventory
                if (!this.moveItemStackTo(stackInSlot, 4, 40, false)) {
                    return ItemStack.EMPTY;
                }
            }
            
            if (stackInSlot.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            
            if (stackInSlot.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            
            slot.onTake(player, stackInSlot);
        }
        
        return itemStack;
    }
    
    private boolean canPlaceInFletchingSlot(ItemStack stack) {
        // Check if this item can be used as any component
        for (ArrowComponent component : ArrowComponent.values()) {
            if (component.getCraftingItem() == stack.getItem()) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, Blocks.FLETCHING_TABLE);
    }
    
    @Override
    public void removed(Player player) {
        super.removed(player);
        this.access.execute((level, pos) -> {
            this.clearContainer(player, this.fletchingContainer);
        });
    }
    
    // Custom slot classes
    private static class FletchingSlot extends Slot {
        private final ArrowComponent.ComponentType componentType;
        
        public FletchingSlot(Container container, int slot, int x, int y, ArrowComponent.ComponentType componentType) {
            super(container, slot, x, y);
            this.componentType = componentType;
        }
        
        @Override
        public boolean mayPlace(ItemStack stack) {
            for (ArrowComponent component : ArrowComponent.values()) {
                if (component.getType() == this.componentType && component.getCraftingItem() == stack.getItem()) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public int getMaxStackSize() {
            return 64; // Allow full stacks for bulk crafting
        }
    }
    
    private class ResultSlot extends Slot {
        public ResultSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }
        
        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }
        
        @Override
        public void onTake(Player player, ItemStack stack) {
            // Consume one item from each input slot
            fletchingContainer.removeItem(TIP_SLOT, 1);
            fletchingContainer.removeItem(SHAFT_SLOT, 1);
            fletchingContainer.removeItem(FLETCHING_SLOT, 1);
            
            // Update result after taking
            updateResult();
        }
    }
}