package net.vg.sagittary.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.vg.sagittary.client.QuiverTooltip;
import net.vg.sagittary.compat.trinkets.TrinketsCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** 1.20.1 has no item components. Keep complete arrow stacks and selection in NBT. */
public class QuiverItem extends Item {
    public static final int MAX_CAPACITY = QuiverTier.RANGER.capacity();
    public static final int BAR_COLOR = 0x8B4513;
    private static final String SELECTED = "sagittary:quiver_selection";
    private final QuiverTier tier;

    public QuiverItem(Properties properties, QuiverTier tier) {
        super(properties.stacksTo(1));
        this.tier = tier;
    }

    public QuiverTier getTier() { return tier; }
    @Override public int getEnchantmentValue() { return 10; }
    public static boolean isValidArrow(ItemStack stack) { return stack.getItem() instanceof ArrowItem; }
    public static int getCapacity(ItemStack stack) { return stack.getItem() instanceof QuiverItem q ? q.tier.capacity() : 0; }
    public static int getTypeLimit(ItemStack stack) { return stack.getItem() instanceof QuiverItem q ? q.tier.typeLimit() : 0; }

    /** New copies, just like the component-era quiver. Never expose persisted mutable state. */
    public static List<ItemStack> getArrowStacks(ItemStack quiver) {
        List<ItemStack> items = new ArrayList<>();
        if (quiver.hasTag()) {
            ListTag data = quiver.getTag().getList("Items", Tag.TAG_COMPOUND);
            for (int i = 0; i < data.size(); i++) {
                ItemStack stack = ItemStack.of(data.getCompound(i));
                if (!stack.isEmpty()) items.add(stack);
            }
        }
        return items;
    }

    private static int indexOf(List<ItemStack> items, ItemStack arrow) {
        if (!arrow.isEmpty()) for (int i = 0; i < items.size(); i++) {
            if (isValidArrow(items.get(i)) && ItemStack.isSameItemSameTags(items.get(i), arrow)) return i;
        }
        return -1;
    }

    private static void write(ItemStack quiver, List<ItemStack> items, int index) {
        ListTag data = new ListTag();
        for (ItemStack item : items) if (!item.isEmpty()) data.add(item.save(new CompoundTag()));
        quiver.getOrCreateTag().put("Items", data);
        if (index >= 0 && index < items.size()) {
            quiver.getOrCreateTag().put(SELECTED, items.get(index).copyWithCount(1).save(new CompoundTag()));
        } else quiver.removeTagKey(SELECTED);
    }

    private static void update(ItemStack quiver, List<ItemStack> items, int fallback) {
        ItemStack selected = peekArrow(quiver);
        items.removeIf(ItemStack::isEmpty);
        int index = indexOf(items, selected);
        if (index < 0 && !items.isEmpty()) index = Mth.clamp(fallback, 0, items.size() - 1);
        write(quiver, items, index);
    }

    public static int getSelectedIndex(ItemStack quiver) {
        List<ItemStack> items = getArrowStacks(quiver);
        CompoundTag selected = quiver.getTagElement(SELECTED);
        int index = indexOf(items, selected == null ? ItemStack.EMPTY : ItemStack.of(selected));
        if (index >= 0) return index;
        for (int i = 0; i < items.size(); i++) if (isValidArrow(items.get(i))) return i;
        return -1;
    }

    public static ItemStack peekArrow(ItemStack quiver) {
        if (!(quiver.getItem() instanceof QuiverItem)) return ItemStack.EMPTY;
        int index = getSelectedIndex(quiver);
        return index < 0 ? ItemStack.EMPTY : getArrowStacks(quiver).get(index);
    }

    public static void cycleSelectedArrow(ItemStack quiver, boolean forward) {
        List<ItemStack> items = getArrowStacks(quiver);
        int selected = getSelectedIndex(quiver);
        if (selected < 0) return;
        int next = selected;
        do next = Math.floorMod(next + (forward ? 1 : -1), items.size());
        while (next != selected && ItemStack.isSameItemSameTags(items.get(selected), items.get(next)));
        write(quiver, items, next);
    }

    public static void selectRandomArrow(ItemStack quiver, RandomSource random) {
        List<ItemStack> items = getArrowStacks(quiver);
        if (!items.isEmpty()) write(quiver, items, random.nextInt(items.size()));
    }

    public static int getTotalArrowCount(List<ItemStack> items) { return items.stream().mapToInt(ItemStack::getCount).sum(); }
    public static int getDistinctArrowTypes(List<ItemStack> items) {
        List<ItemStack> types = new ArrayList<>();
        for (ItemStack item : items) if (indexOf(types, item) < 0) types.add(item);
        return types.size();
    }

    /** Preserve all existing contents, including older over-capacity stacks. */
    public static ItemStack storeArrows(ItemStack quiver, ItemStack arrows) {
        if (!(quiver.getItem() instanceof QuiverItem) || !isValidArrow(arrows)) return arrows;
        List<ItemStack> items = getArrowStacks(quiver);
        if (indexOf(items, arrows) < 0 && getDistinctArrowTypes(items) >= getTypeLimit(quiver)) return arrows;
        int accepted = Math.min(arrows.getCount(), Math.max(0, getCapacity(quiver) - getTotalArrowCount(items)));
        int remaining = accepted;
        for (ItemStack item : items) {
            if (ItemStack.isSameItemSameTags(item, arrows)) {
                int added = Math.min(remaining, Math.max(0, item.getMaxStackSize() - item.getCount()));
                item.grow(added);
                remaining -= added;
            }
        }
        while (remaining > 0) {
            int count = Math.min(remaining, arrows.getMaxStackSize());
            items.add(arrows.copyWithCount(count));
            remaining -= count;
        }
        if (accepted > 0) update(quiver, items, 0);
        return arrows.copyWithCount(arrows.getCount() - accepted);
    }

    public static ItemStack removeOneArrow(ItemStack quiver) {
        List<ItemStack> items = getArrowStacks(quiver);
        int index = getSelectedIndex(quiver);
        if (index < 0) return ItemStack.EMPTY;
        ItemStack result = items.get(index).split(1);
        update(quiver, items, index);
        return result;
    }

    @Override public boolean overrideStackedOnOther(ItemStack quiver, Slot slot, ClickAction action, Player player) {
        if (action != ClickAction.SECONDARY) return false;
        if (slot.getItem().isEmpty()) {
            List<ItemStack> items = getArrowStacks(quiver);
            int index = getSelectedIndex(quiver);
            if (index >= 0) {
                ItemStack removed = items.remove(index);
                ItemStack remainder = slot.safeInsert(removed);
                if (!remainder.isEmpty()) items.add(index, remainder);
                update(quiver, items, index);
                player.playSound(SoundEvents.BUNDLE_REMOVE_ONE);
            }
        } else if (isValidArrow(slot.getItem())) {
            ItemStack stack = slot.getItem();
            ItemStack remainder = storeArrows(quiver, stack);
            stack.setCount(remainder.getCount());
            slot.setChanged();
            player.playSound(SoundEvents.BUNDLE_INSERT);
        }
        return true;
    }

    @Override public boolean overrideOtherStackedOnMe(ItemStack quiver, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (action != ClickAction.SECONDARY || !slot.allowModification(player)) return false;
        if (other.isEmpty()) {
            List<ItemStack> items = getArrowStacks(quiver);
            int index = getSelectedIndex(quiver);
            if (index >= 0) {
                access.set(items.remove(index));
                update(quiver, items, index);
                player.playSound(SoundEvents.BUNDLE_REMOVE_ONE);
            }
        } else if (isValidArrow(other)) {
            ItemStack remainder = storeArrows(quiver, other);
            other.setCount(remainder.getCount());
            player.playSound(SoundEvents.BUNDLE_INSERT);
        }
        return true;
    }

    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack quiver = player.getItemInHand(hand);
        List<ItemStack> items = getArrowStacks(quiver);
        if (items.isEmpty()) return InteractionResultHolder.fail(quiver);
        if (!level.isClientSide) {
            for (ItemStack item : items) if (!player.addItem(item)) player.drop(item, true);
            write(quiver, List.of(), -1);
            player.awardStat(Stats.ITEM_USED.get(this));
        }
        player.playSound(SoundEvents.BUNDLE_DROP_CONTENTS);
        return InteractionResultHolder.sidedSuccess(quiver, level.isClientSide);
    }

    public static ItemStack findActiveQuiver(Player player) { return find(player, false); }
    public static ItemStack findQuiverWithArrows(Player player) { return find(player, true); }
    private static ItemStack find(Player player, boolean needsArrows) {
        ItemStack equipped = TrinketsCompat.getEquippedBackQuiver(player);
        if (!equipped.isEmpty() && (!needsArrows || !peekArrow(equipped).isEmpty())) return equipped;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() instanceof QuiverItem && (!needsArrows || !peekArrow(stack).isEmpty())) return stack;
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack getArrowFromInventory(Player player, ItemStack weapon) {
        ItemStack quiver = findQuiverWithArrows(player);
        return quiver.isEmpty() ? player.getProjectile(weapon) : peekArrow(quiver);
    }
    public static boolean consumeArrowFromInventory(Player player, ItemStack weapon) {
        if (player.getAbilities().instabuild) return true;
        ItemStack quiver = findQuiverWithArrows(player);
        if (!quiver.isEmpty()) return !removeOneArrow(quiver).isEmpty();
        ItemStack arrow = player.getProjectile(weapon);
        if (arrow.isEmpty()) return false;
        arrow.shrink(1);
        if (arrow.isEmpty()) player.getInventory().removeItem(arrow);
        return true;
    }

    @Override public boolean isBarVisible(ItemStack stack) { return !getArrowStacks(stack).isEmpty(); }
    @Override public int getBarWidth(ItemStack stack) { return Mth.clamp(Math.round(13f * getTotalArrowCount(getArrowStacks(stack)) / tier.capacity()), 0, 13); }
    @Override public int getBarColor(ItemStack stack) { return BAR_COLOR; }
    @Override public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        List<ItemStack> items = getArrowStacks(stack);
        return items.isEmpty() ? Optional.empty() : Optional.of(new QuiverTooltip(items, getSelectedIndex(stack), (float) getTotalArrowCount(items) / tier.capacity()));
    }
    @Override public void appendHoverText(ItemStack stack, Level level, List<Component> lines, TooltipFlag flag) {
        ItemStack selected = peekArrow(stack);
        if (selected.isEmpty()) return;
        lines.add(Component.literal("Equipped: ").withStyle(ChatFormatting.GRAY).append(selected.getHoverName().copy().withStyle(ChatFormatting.WHITE)));
        List<ItemStack> items = getArrowStacks(stack);
        String types = tier.hasTypeLimit() ? ", Types: " + getDistinctArrowTypes(items) + "/" + tier.typeLimit() : ", Types: unlimited";
        lines.add(Component.literal("Arrows: " + getTotalArrowCount(items) + "/" + tier.capacity() + types).withStyle(ChatFormatting.DARK_GRAY));
    }
}
