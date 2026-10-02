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
//? if >=26.1 {
import net.minecraft.world.item.component.TooltipDisplay;
//? }
import net.minecraft.world.level.Level;
import net.vg.sagittary.mixin.BundleContentsMutableAccessor;
import net.vg.sagittary.compat.trinkets.TrinketsCompat;
import net.vg.sagittary.registry.ObjectRegistry;
import net.vg.sagittary.component.QuiverSelection;
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
    /** Retained for compatibility with the old tooltip renderer; Ranger capacity. */
    public static final int MAX_CAPACITY = QuiverTier.RANGER.capacity();
    // Weight per arrow - 1/256 so that 256 arrows = weight 1.0 (full) in tooltip display
    private static final Fraction ARROW_WEIGHT = Fraction.getFraction(1, MAX_CAPACITY);
    public static final int BAR_COLOR = 0x8B4513; // Brown color for quiver

    private final QuiverTier tier;

    public QuiverItem(Properties properties, QuiverTier tier) {
        super(quiverProperties(properties));
        this.tier = tier;
    }

    public QuiverTier getTier() { return tier; }

    private static Properties quiverProperties(Properties properties) {
        properties.stacksTo(1).component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
        //? if >=26.1 {
        return properties.enchantable(10);
        //? } else {
        /*return properties;
        *///? }
    }

    //? if <26.1 {
    /*@Override
    public int getEnchantmentValue() { return 10; }
    *///? }

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
            List<ItemStack> items = copyItems(contents);
            int selected = getSelectedIndex(quiver);
            if (selected >= 0) {
                ItemStack removed = items.remove(selected);
                ItemStack result = slot.safeInsert(removed);
                if (!result.isEmpty()) items.add(selected, result);
                updateContents(quiver, items, selected);
                this.playRemoveSound(player);
            }
        } else if (isValidArrow(stackInSlot)) {
            // Add arrow to quiver using direct insertion (bypasses weight limit)
            int currentCount = getTotalArrowCount(contents);
            int spaceLeft = canAcceptArrow(contents, stackInSlot, quiver) ? getCapacity(quiver) - currentCount : 0;
            int toInsert = Math.min(stackInSlot.getCount(), spaceLeft);

            if (toInsert > 0) {
                ItemStack toAdd = stackInSlot.split(toInsert);
                addArrowsToContents(quiver, toAdd);
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
            List<ItemStack> items = copyItems(contents);
            int selected = getSelectedIndex(quiver);
            if (selected >= 0) {
                ItemStack removed = items.remove(selected);
                this.playRemoveSound(player);
                access.set(removed);
                updateContents(quiver, items, selected);
            }
        } else if (isValidArrow(other)) {
            // Add arrow to quiver using direct insertion (bypasses weight limit)
            int currentCount = getTotalArrowCount(contents);
            int spaceLeft = canAcceptArrow(contents, other, quiver) ? getCapacity(quiver) - currentCount : 0;
            int toInsert = Math.min(other.getCount(), spaceLeft);

            if (toInsert > 0) {
                ItemStack toAdd = other.copyWithCount(toInsert);
                other.shrink(toInsert);
                addArrowsToContents(quiver, toAdd);
                this.playInsertSound(player);
            }
        }

        return true;
    }

    /**
     * Add arrows to contents, bypassing weight limits. Merges with existing stacks of same type,
     * respecting max stack size (64). Creates new stacks if needed.
     */
    private static void addArrowsToContents(ItemStack quiver, ItemStack toAdd) {
        List<ItemStack> items = copyItems(quiver.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY));

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
        updateContents(quiver, items, 0);
    }

    /**
     * Build BundleContents from a list of items, bypassing weight limits.
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

        // Visual mirror only. Gameplay never reads vanilla's selected-item field.
        //? if >=26.1 {
        if (selectedIndex >= 0 && selectedIndex < internalItems.size()) {
            accessor.sagittary$setSelectedItem(selectedIndex);
        }
        //? }

        return mutable.toImmutable();
    }

    private static List<ItemStack> copyItems(BundleContents contents) {
        return new ArrayList<>(contents.itemCopyStream().toList());
    }

    /** Preserve the selected type through insertion/reordering, or repair it after depletion. */
    private static void updateContents(ItemStack quiver, List<ItemStack> items, int fallbackIndex) {
        ItemStack selected = peekArrow(quiver);
        items.removeIf(ItemStack::isEmpty);
        int index = indexOf(items, selected);
        if (index < 0 && !items.isEmpty()) index = Mth.clamp(fallbackIndex, 0, items.size() - 1);
        writeSelection(quiver, items, index);
    }

    private static int indexOf(List<ItemStack> items, ItemStack selected) {
        if (!selected.isEmpty()) {
            for (int i = 0; i < items.size(); i++) {
                if (isValidArrow(items.get(i)) && ItemStack.isSameItemSameComponents(items.get(i), selected)) return i;
            }
        }
        return -1;
    }

    private static void writeSelection(ItemStack quiver, List<ItemStack> items, int index) {
        if (index >= 0) quiver.set(ObjectRegistry.QUIVER_SELECTION.get(), new QuiverSelection(items.get(index)));
        else quiver.remove(ObjectRegistry.QUIVER_SELECTION.get());
        quiver.set(DataComponents.BUNDLE_CONTENTS, buildContentsFromList(items, index));
    }

    @Override
    //? if >=26.1 {
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
    //? } else {
    /*public net.minecraft.world.InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
    *///? }
        ItemStack quiver = player.getItemInHand(hand);
        BundleContents contents = quiver.get(DataComponents.BUNDLE_CONTENTS);

        if (contents != null && !contents.isEmpty()) {
            for (ItemStack removed : copyItems(contents)) {
                if (!player.addItem(removed)) {
                    player.drop(removed, true);
                }
            }

            updateContents(quiver, new ArrayList<>(), -1);
            this.playDropSound(player);

            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.awardStat(Stats.ITEM_USED.get(this));
            }

            return net.vg.sagittary.util.Util.useResult(InteractionResult.SUCCESS, quiver);
        }

        return net.vg.sagittary.util.Util.useResult(InteractionResult.FAIL, quiver);
    }

    /**
     * Check if the item is a valid arrow that can be stored in the quiver
     */
    public static boolean isValidArrow(ItemStack stack) {
        return stack.getItem() instanceof ArrowItem;
    }

    public static int getCapacity(ItemStack quiver) {
        return quiver.getItem() instanceof QuiverItem item ? item.tier.capacity() : 0;
    }

    public static int getTypeLimit(ItemStack quiver) {
        return quiver.getItem() instanceof QuiverItem item ? item.tier.typeLimit() : 0;
    }

    public static int getDistinctArrowTypes(BundleContents contents) {
        if (contents == null) return 0;
        List<ItemStack> types = new ArrayList<>();
        contents.itemCopyStream().forEach(stack -> {
            if (types.stream().noneMatch(existing -> ItemStack.isSameItemSameComponents(existing, stack))) {
                types.add(stack);
            }
        });
        return types.size();
    }

    /** Existing over-capacity 1.0.1 quivers are intentionally not modified. */
    public static boolean canAcceptArrow(BundleContents contents, ItemStack arrow, ItemStack quiver) {
        if (!isValidArrow(arrow) || getTotalArrowCount(contents) >= getCapacity(quiver)) return false;
        if (contents == null || !(quiver.getItem() instanceof QuiverItem item) || !item.tier.hasTypeLimit()) return true;
        boolean alreadyPresent = contents.itemCopyStream()
                .anyMatch(existing -> ItemStack.isSameItemSameComponents(existing, arrow));
        return alreadyPresent || getDistinctArrowTypes(contents) < getTypeLimit(quiver);
    }

    /** Stores as many arrows as the tier rules allow and returns the unaccepted remainder. */
    public static ItemStack storeArrows(ItemStack quiver, ItemStack arrows) {
        if (!(quiver.getItem() instanceof QuiverItem) || !isValidArrow(arrows)) return arrows;
        BundleContents contents = quiver.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
        if (!canAcceptArrow(contents, arrows, quiver)) return arrows;
        int count = Math.min(arrows.getCount(), getCapacity(quiver) - getTotalArrowCount(contents));
        if (count <= 0) return arrows;
        addArrowsToContents(quiver, arrows.copyWithCount(count));
        return arrows.copyWithCount(arrows.getCount() - count);
    }

    /**
     * Find the first quiver in the player's inventory that has arrows.
     * Returns the quiver ItemStack or ItemStack.EMPTY if none found.
     */
    public static ItemStack findQuiverWithArrows(Player player) {
        ItemStack equipped = TrinketsCompat.getEquippedBackQuiver(player);
        if (!equipped.isEmpty()) {
            BundleContents contents = equipped.get(DataComponents.BUNDLE_CONTENTS);
            if (contents != null && !contents.isEmpty()) return equipped;
        }

        // Prefer hotbar quivers before the remainder of the inventory.
        for (int i = 0; i < Math.min(9, player.getInventory().getContainerSize()); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() instanceof QuiverItem && hasArrows(stack)) return stack;
        }
        for (int i = 9; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() instanceof QuiverItem && hasArrows(stack)) return stack;
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack findActiveQuiver(Player player) {
        ItemStack equipped = TrinketsCompat.getEquippedBackQuiver(player);
        if (!equipped.isEmpty()) return equipped;
        for (int i = 0; i < Math.min(9, player.getInventory().getContainerSize()); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() instanceof QuiverItem) {
                return stack;
            }
        }
        for (int i = 9; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() instanceof QuiverItem) return stack;
        }
        return ItemStack.EMPTY;
    }

    private static boolean hasArrows(ItemStack stack) {
        BundleContents contents = stack.get(DataComponents.BUNDLE_CONTENTS);
        return contents != null && !contents.isEmpty();
    }

    /**
     * Get the selected arrow from a quiver without removing it (for checking).
     * Uses Sagittary's persistent selected type, never vanilla bundle selection.
     */
    public static ItemStack peekArrow(ItemStack quiver) {
        if (!(quiver.getItem() instanceof QuiverItem)) {
            return ItemStack.EMPTY;
        }
        BundleContents contents = quiver.get(DataComponents.BUNDLE_CONTENTS);
        if (contents == null || contents.isEmpty()) {
            return ItemStack.EMPTY;
        }

        List<ItemStack> items = contents.itemCopyStream().toList();
        int index = getSelectedIndex(quiver);
        return index < 0 ? ItemStack.EMPTY : items.get(index);
    }

    public static void selectRandomArrow(ItemStack quiver, net.minecraft.util.RandomSource random) {
        BundleContents contents = quiver.get(DataComponents.BUNDLE_CONTENTS);
        if (contents == null || contents.isEmpty()) return;
        List<ItemStack> items = copyItems(contents);
        writeSelection(quiver, items, random.nextInt(items.size()));
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
     * Selection and consumption use the same Sagittary state.
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
        int selectedIndex = getSelectedIndex(quiver);
        if (selectedIndex < 0) return ItemStack.EMPTY;
        int removeIndex = (selectedIndex >= 0 && selectedIndex < items.size()) ? selectedIndex : 0;

        // Get the stack and create a copy of 1 arrow
        ItemStack targetStack = items.get(removeIndex);
        ItemStack removed = targetStack.copyWithCount(1);

        // Shrink the stack or remove it entirely
        if (targetStack.getCount() > 1) {
            targetStack.shrink(1);
        } else {
            items.remove(removeIndex);
        }

        // Rebuild the contents using direct method (preserves order and selection)
        updateContents(quiver, items, removeIndex);

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
        return Mth.clamp(Math.round(13.0F * (float) totalArrows / getCapacity(stack)), 0, 13);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return BAR_COLOR;
    }

    @Override
    //? if >=26.1 {
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> consumer, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, consumer, flag);
    //? } else {
    /*public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, lines, flag);
        Consumer<Component> consumer = lines::add;
    *///? }
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
            String typeText = tier.hasTypeLimit() ? ", Types: " + getDistinctArrowTypes(contents) + "/" + tier.typeLimit() : ", Types: unlimited";
            consumer.accept(Component.literal("Arrows: " + totalArrows + "/" + tier.capacity() + typeText)
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

        int selected = getSelectedIndex(quiver);
        if (selected < 0) return;
        int next = selected;
        // Multiple full stacks of one arrow type are one user-visible choice.
        do {
            next = Math.floorMod(next + (forward ? 1 : -1), items.size());
        } while (next != selected && ItemStack.isSameItemSameComponents(items.get(selected), items.get(next)));
        writeSelection(quiver, items, next);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        BundleContents contents = stack.get(DataComponents.BUNDLE_CONTENTS);
        if (contents == null || contents.isEmpty()) {
            return Optional.empty();
        }

        // Use custom QuiverTooltip with correct capacity (256)
        return Optional.of(QuiverTooltip.fromContents(contents, getSelectedIndex(stack), tier.capacity()));
    }

    public static int getSelectedIndex(ItemStack quiver) {
        BundleContents contents = quiver.get(DataComponents.BUNDLE_CONTENTS);
        if (contents == null || contents.isEmpty()) return -1;
        List<ItemStack> items = contents.itemCopyStream().toList();
        QuiverSelection selection = quiver.get(ObjectRegistry.QUIVER_SELECTION.get());
        ItemStack selected = selection == null ? ItemStack.EMPTY : selection.arrow();
        int index = indexOf(items, selected);
        if (index >= 0) return index;
        // Old stacks without the component, or externally edited contents, have
        // a deterministic fallback. The next server mutation persists the repair.
        for (int i = 0; i < items.size(); i++) if (isValidArrow(items.get(i))) return i;
        return -1;
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
