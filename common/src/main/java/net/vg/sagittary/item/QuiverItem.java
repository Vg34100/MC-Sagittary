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
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * A quiver item that stores arrows like a bundle.
 * Only accepts arrow items (ArrowItem and subclasses).
 * Capacity: 256 arrows (4 stacks of 64)
 */
public class QuiverItem extends Item {
    private static final int MAX_WEIGHT = 256; // 4 stacks of arrows
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
        BundleContents.Mutable mutable = new BundleContents.Mutable(contents);

        if (stackInSlot.isEmpty()) {
            // Remove item from quiver and place in slot
            ItemStack removed = mutable.removeOne();
            if (removed != null) {
                ItemStack result = slot.safeInsert(removed);
                mutable.tryInsert(result);
                this.playRemoveSound(player);
            }
        } else if (isValidArrow(stackInSlot)) {
            // Add arrow to quiver
            int inserted = mutable.tryTransfer(slot, player);
            if (inserted > 0) {
                this.playInsertSound(player);
            }
        }

        quiver.set(DataComponents.BUNDLE_CONTENTS, mutable.toImmutable());
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

        BundleContents.Mutable mutable = new BundleContents.Mutable(contents);

        if (other.isEmpty()) {
            // Remove item from quiver
            ItemStack removed = mutable.removeOne();
            if (removed != null) {
                this.playRemoveSound(player);
                access.set(removed);
            }
        } else if (isValidArrow(other)) {
            // Add arrow to quiver
            int inserted = mutable.tryInsert(other);
            if (inserted > 0) {
                this.playInsertSound(player);
            }
        }

        quiver.set(DataComponents.BUNDLE_CONTENTS, mutable.toImmutable());
        return true;
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
     * Get the first arrow from a quiver without removing it (for checking).
     */
    public static ItemStack peekArrow(ItemStack quiver) {
        if (!(quiver.getItem() instanceof QuiverItem)) {
            return ItemStack.EMPTY;
        }
        BundleContents contents = quiver.get(DataComponents.BUNDLE_CONTENTS);
        if (contents == null || contents.isEmpty()) {
            return ItemStack.EMPTY;
        }
        // Use Mutable to peek - remove, copy, then put back
        BundleContents.Mutable mutable = new BundleContents.Mutable(contents);
        ItemStack removed = mutable.removeOne();
        if (removed != null && !removed.isEmpty()) {
            ItemStack copy = removed.copy();
            mutable.tryInsert(removed); // Put it back
            quiver.set(DataComponents.BUNDLE_CONTENTS, mutable.toImmutable());
            return copy;
        }
        return ItemStack.EMPTY;
    }

    /**
     * Remove and return one arrow from a quiver.
     */
    public static ItemStack removeOneArrow(ItemStack quiver) {
        if (!(quiver.getItem() instanceof QuiverItem)) {
            return ItemStack.EMPTY;
        }
        BundleContents contents = quiver.get(DataComponents.BUNDLE_CONTENTS);
        if (contents == null || contents.isEmpty()) {
            return ItemStack.EMPTY;
        }
        BundleContents.Mutable mutable = new BundleContents.Mutable(contents);
        ItemStack removed = mutable.removeOne();
        quiver.set(DataComponents.BUNDLE_CONTENTS, mutable.toImmutable());
        return removed != null ? removed : ItemStack.EMPTY;
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
        return Mth.clamp(Math.round(13.0F * (float) contents.size() / MAX_WEIGHT), 0, 13);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return BAR_COLOR;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> consumer, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, consumer, flag);
        BundleContents contents = stack.get(DataComponents.BUNDLE_CONTENTS);
        if (contents != null) {
            int count = contents.size();
            consumer.accept(Component.translatable("item.sagittary.quiver.arrows", count, MAX_WEIGHT)
                    .withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        BundleContents contents = stack.get(DataComponents.BUNDLE_CONTENTS);
        return contents != null && !contents.isEmpty() ? Optional.of(new BundleTooltip(contents)) : Optional.empty();
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
