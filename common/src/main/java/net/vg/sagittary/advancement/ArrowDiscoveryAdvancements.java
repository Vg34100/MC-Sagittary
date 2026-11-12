package net.vg.sagittary.advancement;

import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.vg.sagittary.component.ArrowParts;
import net.vg.sagittary.registry.ObjectRegistry;

/** Persistent unique-arrow discovery is stored in a hidden advancement, while the visible Arsenal has 16 milestones. */
public final class ArrowDiscoveryAdvancements {
    private static final Identifier TRACKING = net.vg.sagittary.util.ModIds.of("arsenal_tracking");
    private static final Identifier ARSENAL = net.vg.sagittary.util.ModIds.of("arsenal");

    private ArrowDiscoveryAdvancements() {}

    public static void recordCraft(ServerPlayer player, ItemStack result) {
        if (!result.is(ObjectRegistry.COMPONENT_ARROW_ITEM.get())) return;
        if (!net.vg.sagittary.item.ComponentArrowItem.hasArrowParts(result)) return;
        ArrowParts parts = net.vg.sagittary.item.ComponentArrowItem.getArrowPartsFromStack(result);
        //? if >=1.21 {
        var tracking = player.level().getServer().getAdvancements().get(TRACKING);
        var arsenal = player.level().getServer().getAdvancements().get(ARSENAL);
        //? } else {
        /*var tracking = player.level().getServer().getAdvancements().getAdvancement(TRACKING);
        var arsenal = player.level().getServer().getAdvancements().getAdvancement(ARSENAL);
        *///? }
        if (tracking == null || arsenal == null) return;
        String key = parts.tip() + "_" + parts.shaft() + "_" + parts.fletching();
        if (!player.getAdvancements().award(tracking, key)) return;
        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(tracking);
        int discovered = 0;
        for (String ignored : progress.getCompletedCriteria()) discovered++;
        for (int milestone = 1; milestone <= Math.min(discovered, 16); milestone++) {
            player.getAdvancements().award(arsenal, "unique_" + milestone);
        }
    }
}
