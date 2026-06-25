package net.vg.sagittary.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.vg.sagittary.client.QuiverTooltip;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.vg.sagittary.mixin.BundleContentsMutableAccessor;
import org.apache.commons.lang3.math.Fraction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * A quiver item that stores arrows like a bundle.
 * Only accepts arrow items (ArrowItem and subclasses).
 * Capacity: 256 arrows (4 stacks of 64)
 */
public class QuiverItem extends Item {
    public static final int MAX_CAPACITY = 256; // 4 stacks of arrows
    // Weight per arrow - 1/256 so that 256 arrows = weight 1.0 (full) in tooltip display
    private static final Fraction ARROW_WEIGHT = Fraction.getFraction(1, MAX_CAPACITY);
    public static final int BAR_COLOR = 0x8B4513; // Brown color for quiver

    public QuiverItem(Properties properties) {
        super(properties.stacksTo(1).component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY));
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack quiver, Slot slot, ClickAction action, Player player) {
        if (action != ClickAction.SECONDARY) {
            return false;
        }

        BundleContents contents = quiver.get(DataComponents.BUNDLE_CONTENTS);
        if (contents == null) {
            return false;
        }

        ItemStack stackInSlot = slot.getItem();

        if (stackInSlot.isEmpty()) {
            // Remove item from quiver and place in slot
            BundleContents.Mutable mutable = new BundleContents.Mutable(contents);
            ItemStack removed = mutable.removeOne();
            if (removed != null) {
                ItemStack result = slot.safeInsert(removed);
                if (!result.isEmpty()) {
                    insertArrowDirect(mutable, result);
                }
                quiver.set(DataComponents.BUNDLE_CONTENTS, mutable.toImmutable());
                this.playRemoveSound(player);
            }
        } else if (isValidArrow(stackInSlot)) {
            // Add arrow to quiver using direct insertion (bypasses weight limit)
            int currentCount = getTotalArrowCount(contents);
            int spaceLeft = MAX_CAPACITY - currentCount;
            int toInsert = Math.min(stackInSlot.getCount(), spaceLeft);

            if (toInsert > 0) {
                ItemStack toAdd = stackInSlot.split(toInsert);
                BundleContents newContents = addArrowsToContents(contents, toAdd);
                quiver.set(DataComponents.BUNDLE_CONTENTS, newContents);
                this.playInsertSound(player);
            }
        }

        return true;
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack quiver, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (action != ClickAction.SECONDARY || !slot.allowModification(player)) {
            return false;
        }

        BundleContents contents = quiver.get(DataComponents.BUNDLE_CONTENTS);
        if (contents == null) {
            return false;
        }

        if (other.isEmpty()) {
            // Remove item from quiver
            BundleContents.Mutable mutable = new BundleContents.Mutable(contents);
            ItemStack removed = mutable.removeOne();
            if (removed != null) {
                this.playRemoveSound(player);
                access.set(removed);
                quiver.set(DataComponents.BUNDLE_CONTENTS, mutable.toImmutable());
            }
        } else if (isValidArrow(other)) {
            // Add arrow to quiver using direct insertion (bypasses weight limit)
            int currentCount = getTotalArrowCount(contents);
            int spaceLeft = MAX_CAPACITY - currentCount;
            int toInsert = Math.min(other.getCount(), spaceLeft);

            if (toInsert > 0) {
                ItemStack toAdd = other.copyWithCount(toInsert);
                other.shrink(toInsert);
                BundleContents newContents = addArrowsToContents(contents, toAdd);
                quiver.set(DataComponents.BUNDLE_CONTENTS, newContents);
                this.playInsertSound(player);
            }
        }

        return true;
    }

    /**
     * Add arrows to contents, bypassing weight limits. Merges with existing stacks of same type,
     * respecting max stack size (64). Creates new stacks if needed.
     */
    private static BundleContents addArrowsToContents(BundleContents contents, ItemStack toAdd) {
        List<ItemStack> items = new ArrayList<>();
        contents.itemCopyStream().forEach(items::add);

        int remaining = toAdd.getCount();
        int maxStackSize = toAdd.getMaxStackSize();

        // Try to merge with existing stacks of same type
        for (ItemStack existing : items) {
            if (remaining <= 0) break;

            if (ItemStack.isSameItemSameComponents(existing, toAdd)) {
                int canAdd = maxStackSize - existing.getCount();
                if (canAdd > 0) {
                    int toMerge = Math.min(canAdd, remaining);
                    existing.grow(toMerge);
                    remaining -= toMerge;
                }
            }
        }

        // Add remaining as new stacks
        while (remaining > 0) {
            int stackSize = Math.min(remaining, maxStackSize);
            items.add(0, toAdd.copyWithCount(stackSize)); // Add to front
            remaining -= stackSize;
        }

        // Rebuild contents
        return buildContentsFromList(items);
    }

    /**
     * Build BundleContents from a list of items, bypassing weight limits.
     */
    private static BundleContents buildContentsFromList(List<ItemStack> items) {
        return buildContentsFromList(items, -1);
    }

    /**
     * Build BundleContents from a list of items, bypassing weight limits.
     * Preserves selectedIndex if valid.
     */
    private static BundleContents buildContentsFromList(List<ItemStack> items, int selectedIndex) {
        BundleContents.Mutable mutable = new BundleContents.Mutable(BundleContents.EMPTY);
        BundleContentsMutableAccessor accessor = (BundleContentsMutableAccessor) mutable;

        // Directly add items to the internal list
        List<ItemStack> internalItems = accessor.sagittary$getItems();
        Fraction totalWeight = Fraction.ZERO;

        for (int i = items.size() - 1; i >= 0; i--) {
            ItemStack item = items.get(i);
            if (!item.isEmpty()) {
                internalItems.add(0, item.copy());
                totalWeight = totalWeight.add(ARROW_WEIGHT.multiplyBy(Fraction.getFraction(item.getCount(), 1)));
            }
        }

        accessor.sagittary$setWeight(totalWeight);

        // Set selected item index if valid
        if (selectedIndex >= 0 && selectedIndex < internalItems.size()) {
            accessor.sagittary$setSelectedItem(selectedIndex);
        }

        return mutable.toImmutable();
    }

    /**
     * Insert arrow directly into mutable, bypassing weight limits.
     * Respects max stack size (64), creates new stacks if needed.
     */
    private static void insertArrowDirect(BundleContents.Mutable mutable, ItemStack toAdd) {
        BundleContentsMutableAccessor accessor = (BundleContentsMutableAccessor) mutable;
        List<ItemStack> items = accessor.sagittary$getItems();

        int remaining = toAdd.getCount();
        int maxStackSize = toAdd.getMaxStackSize();

        // Try to merge with existing stacks
        for (ItemStack existing : items) {
            if (remaining <= 0) break;

            if (ItemStack.isSameItemSameComponents(existing, toAdd)) {
                int canAdd = maxStackSize - existing.getCount();
                if (canAdd > 0) {
                    int toMerge = Math.min(canAdd, remaining);
                    existing.grow(toMerge);
                    remaining -= toMerge;
                }
            }
        }

        // Add remaining as new stacks
        while (remaining > 0) {
            int stackSize = Math.min(remaining, maxStackSize);
            items.add(0, toAdd.copyWithCount(stackSize));
            remaining -= stackSize;
        }

        // Update weight
        Fraction currentWeight = accessor.sagittary$getWeight();
        Fraction addedWeight = ARROW_WEIGHT.multiplyBy(Fraction.getFraction(toAdd.getCount(), 1));
        accessor.sagittary$setWeight(currentWeight.add(addedWeight));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack quiver = player.getItemInHand(hand);
        BundleContents contents = quiver.get(DataComponents.BUNDLE_CONTENTS);

        if (contents != null && !contents.isEmpty()) {
            // Drop all arrows on use
            BundleContents.Mutable mutable = new BundleContents.Mutable(contents);

            ItemStack removed;
            while ((removed = mutable.removeOne()) != null) {
                if (!player.addItem(removed)) {
                    player.drop(removed, true);
                }
            }

            quiver.set(DataComponents.BUNDLE_CONTENTS, mutable.toImmutable());
            this.playDropSound(player);

            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.awardStat(Stats.ITEM_USED.get(this));
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    /**
     * Check if the item is a valid arrow that can be stored in the quiver
     */
    public static boolean isValidArrow(ItemStack stack) {
        return stack.getItem() instanceof ArrowItem;
    }

    /**
     * Find the first quiver in the player's inventory that has arrows.
     * Returns the quiver ItemStack or ItemStack.EMPTY if none found.
     */
    public static ItemStack findQuiverWithArrows(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() instanceof QuiverItem) {
                BundleContents contents = stack.get(DataComponents.BUNDLE_CONTENTS);
                if (contents != null && !contents.isEmpty()) {
                    return stack;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    /**
     * Get the selected arrow from a quiver without removing it (for checking).
     * Uses the bundle's selected item if set, otherwise returns first item.
     */
    public static ItemStack peekArrow(ItemStack quiver) {
        if (!(quiver.getItem() instanceof QuiverItem)) {
            return ItemStack.EMPTY;
        }
        BundleContents contents = quiver.get(DataComponents.BUNDLE_CONTENTS);
        if (contents == null || contents.isEmpty()) {
            return ItemStack.EMPTY;
        }

        // Check if there's a selected item
        var selectedItem = contents.getSelectedItem();
        if (selectedItem != null) {
            return selectedItem.create();
        }

        // Fallback to first item
        return contents.itemCopyStream().findFirst().orElse(ItemStack.EMPTY);
    }

    /**
     * Count total arrows across all stacks in the quiver.
     */
    public static int getTotalArrowCount(BundleContents contents) {
        if (contents == null || contents.isEmpty()) return 0;
        return contents.itemCopyStream()
                .mapToInt(ItemStack::getCount)
                .sum();
    }

    /**
     * Remove and return one arrow from a quiver (shrinks selected stack by 1).
     * Uses the bundle's selected item if set, otherwise uses first item.
     */
    public static ItemStack removeOneArrow(ItemStack quiver) {
        if (!(quiver.getItem() instanceof QuiverItem)) {
            return ItemStack.EMPTY;
        }
        BundleContents contents = quiver.get(DataComponents.BUNDLE_CONTENTS);
        if (contents == null || contents.isEmpty()) {
            return ItemStack.EMPTY;
        }

        // Get the items as a mutable list
        List<ItemStack> items = new ArrayList<>();
        contents.itemCopyStream().forEach(items::add);

        if (items.isEmpty()) {
            return ItemStack.EMPTY;
        }

        // Determine which index to remove from (selected or first)
        int selectedIndex = contents.getSelectedItemIndex();
        int removeIndex = (selectedIndex >= 0 && selectedIndex < items.size()) ? selectedIndex : 0;

        // Get the stack and create a copy of 1 arrow
        ItemStack targetStack = items.get(removeIndex);
        ItemStack removed = targetStack.copyWithCount(1);

        // Shrink the stack or remove it entirely
        int newSelectedIndex = selectedIndex;
        if (targetStack.getCount() > 1) {
            targetStack.shrink(1);
        } else {
            items.remove(removeIndex);
            // Adjust selected index if needed
            if (selectedIndex >= items.size()) {
                newSelectedIndex = items.isEmpty() ? -1 : items.size() - 1;
            }
        }

        // Rebuild the contents using direct method (preserves order and selection)
        quiver.set(DataComponents.BUNDLE_CONTENTS, buildContentsFromList(items, newSelectedIndex));

        return removed;
    }

    /**
     * Get an arrow from the player's inventory, checking quivers first.
     * This is meant to be called by bow items to find arrows.
     */
    public static ItemStack getArrowFromInventory(Player player, ItemStack bowStack) {
        // First check if player already has a suitable arrow (vanilla behavior)
        ItemStack vanillaProjectile = player.getProjectile(bowStack);

        // Check quivers in inventory
        ItemStack quiver = findQuiverWithArrows(player);
        if (!quiver.isEmpty()) {
            ItemStack quiverArrow = peekArrow(quiver);
            if (!quiverArrow.isEmpty()) {
                // Prefer quiver arrows, or use vanilla if quiver is empty
                return quiverArrow;
            }
        }

        return vanillaProjectile;
    }

    /**
     * Consume an arrow from inventory, checking quivers first.
     * Returns true if an arrow was consumed.
     */
    public static boolean consumeArrowFromInventory(Player player, ItemStack bowStack) {
        if (player.hasInfiniteMaterials()) {
            return true; // Creative mode, don't consume
        }

        // Check quivers first
        ItemStack quiver = findQuiverWithArrows(player);
        if (!quiver.isEmpty()) {
            ItemStack removed = removeOneArrow(quiver);
            if (!removed.isEmpty()) {
                return true;
            }
        }

        // Fall back to consuming from regular inventory
        ItemStack arrow = player.getProjectile(bowStack);
        if (!arrow.isEmpty()) {
            arrow.shrink(1);
            if (arrow.isEmpty()) {
                player.getInventory().removeItem(arrow);
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        BundleContents contents = stack.get(DataComponents.BUNDLE_CONTENTS);
        return contents != null && !contents.isEmpty();
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        BundleContents contents = stack.get(DataComponents.BUNDLE_CONTENTS);
        if (contents == null) return 0;
        int totalArrows = getTotalArrowCount(contents);
        return Mth.clamp(Math.round(13.0F * (float) totalArrows / MAX_CAPACITY), 0, 13);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return BAR_COLOR;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> consumer, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, consumer, flag);
        BundleContents contents = stack.get(DataComponents.BUNDLE_CONTENTS);
        if (contents != null && !contents.isEmpty()) {
            // Show selected/equipped arrow
            ItemStack selectedArrow = peekArrow(stack);
            if (!selectedArrow.isEmpty()) {
                consumer.accept(Component.literal("Equipped: ")
                        .withStyle(ChatFormatting.GRAY)
                        .append(selectedArrow.getHoverName().copy().withStyle(ChatFormatting.WHITE)));
            }

            // Show capacity
            int totalArrows = getTotalArrowCount(contents);
            consumer.accept(Component.literal("Arrows: " + totalArrows + "/" + MAX_CAPACITY)
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    /**
     * Cycle to the next arrow type in the quiver.
     * Call this when the player scrolls while holding the quiver.
     */
    public static void cycleSelectedArrow(ItemStack quiver, boolean forward) {
        if (!(quiver.getItem() instanceof QuiverItem)) {
            return;
        }
        BundleContents contents = quiver.get(DataComponents.BUNDLE_CONTENTS);
        if (contents == null || contents.isEmpty()) {
            return;
        }

        // Get items as a list
        List<ItemStack> items = new ArrayList<>();
        contents.itemCopyStream().forEach(items::add);

        if (items.size() <= 1) {
            return; // Nothing to cycle
        }

        // Rotate the list (move first to last, or last to first)
        if (forward) {
            ItemStack first = items.remove(0);
            items.add(first);
        } else {
            ItemStack last = items.remove(items.size() - 1);
            items.add(0, last);
        }

        // Rebuild the contents using direct method (preserves order, no weight limit)
        quiver.set(DataComponents.BUNDLE_CONTENTS, buildContentsFromList(items));
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        BundleContents contents = stack.get(DataComponents.BUNDLE_CONTENTS);
        if (contents == null || contents.isEmpty()) {
            return Optional.empty();
        }

        // Use custom QuiverTooltip with correct capacity (256)
        return Optional.of(QuiverTooltip.fromContents(contents, MAX_CAPACITY));
    }

    private void playRemoveSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    private void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    private void playDropSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_DROP_CONTENTS, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }
}
