package net.vg.sagittary.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;

import java.util.ArrayList;
import java.util.List;

/** Client-only gold outlines for targets selected by a Topaz component arrow. */
public final class TopazPulseRenderer {
    private static final List<Pulse> PULSES = new ArrayList<>();

    private TopazPulseRenderer() {}

    public static void addPulse(List<BlockPos> targets) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null && !targets.isEmpty()) PULSES.add(new Pulse(List.copyOf(targets), minecraft.level.getGameTime() + 50));
    }

    public static void render() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            PULSES.clear();
            return;
        }
        long now = minecraft.level.getGameTime();
        PULSES.removeIf(pulse -> pulse.expiresAt <= now);
        for (Pulse pulse : PULSES) {
            int remaining = (int) (pulse.expiresAt - now);
            int alpha = Math.max(40, Math.min(220, remaining * 5));
            GizmoStyle style = GizmoStyle.stroke((alpha << 24) | 0xFFD23F);
            for (BlockPos target : pulse.targets) Gizmos.cuboid(target, style).setAlwaysOnTop();
        }
    }

    private record Pulse(List<BlockPos> targets, long expiresAt) {}
}
