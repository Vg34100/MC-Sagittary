package net.vg.sagittary.advancement;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.vg.sagittary.component.ArrowParts;
import net.vg.sagittary.registry.ObjectRegistry;

/** Persistent unique-arrow discovery is stored in a hidden advancement, while the visible Arsenal has 16 milestones. */
public final class ArrowDiscoveryAdvancements {
    private static final Identifier TRACKING = Identifier.fromNamespaceAndPath("sagittary", "arsenal_tracking");
    private static final Identifier ARSENAL = Identifier.fromNamespaceAndPath("sagittary", "arsenal");

    private ArrowDiscoveryAdvancements() {}

    public static void recordCraft(ServerPlayer player, ItemStack result) {
        if (!result.is(ObjectRegistry.COMPONENT_ARROW_ITEM.get())) return;
        ArrowParts parts = result.get(ObjectRegistry.ARROW_PARTS.get());
        if (parts == null) return;
        AdvancementHolder tracking = player.level().getServer().getAdvancements().get(TRACKING);
        AdvancementHolder arsenal = player.level().getServer().getAdvancements().get(ARSENAL);
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
