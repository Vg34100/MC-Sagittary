package net.vg.sagittary.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import java.util.ArrayList;
import java.util.List;

/** Gold outlines rendered immediately before the Gizmos API existed. */
public final class TopazPulseRenderer {
    private static final List<Pulse> PULSES = new ArrayList<>();
    private TopazPulseRenderer() {}

    public static void addPulse(List<BlockPos> targets) {
        var level = Minecraft.getInstance().level;
        if (level != null && !targets.isEmpty()) PULSES.add(new Pulse(List.copyOf(targets), level.getGameTime() + 50));
    }

    public static void render(PoseStack poses, Vec3 camera) {
        var level = Minecraft.getInstance().level;
        if (level == null) { PULSES.clear(); return; }
        long now = level.getGameTime();
        PULSES.removeIf(pulse -> pulse.expiresAt <= now);
        if (PULSES.isEmpty()) return;
        poses.pushPose();
        poses.translate(-camera.x, -camera.y, -camera.z);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
        try {
            var vertices = Tesselator.getInstance().begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);
            for (Pulse pulse : PULSES) {
                float alpha = Math.max(40, Math.min(220, (pulse.expiresAt - now) * 5)) / 255.0F;
                for (BlockPos pos : pulse.targets) {
                    LevelRenderer.renderLineBox(poses, vertices, pos.getX(), pos.getY(), pos.getZ(),
                            pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1, 1, 210 / 255.0F, 63 / 255.0F, alpha);
                }
            }
            BufferUploader.drawWithShader(vertices.buildOrThrow());
        } finally {
            RenderSystem.enableDepthTest();
            RenderSystem.disableBlend();
            poses.popPose();
        }
    }

    private record Pulse(List<BlockPos> targets, long expiresAt) {}
}
