package net.vg.sagittary.component.effects;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.vg.sagittary.entity.ComponentArrowEntity;

/**
 * Effect for glowstone tips - applies Glowing effect like spectral arrows.
 * Hit entities glow through walls for 10 seconds.
 */
public class GlowstoneTipEffect implements ComponentEffect {

    private static final int GLOWING_DURATION = 200; // 10 seconds (20 ticks/second)

    @Override
    public void onEntityHit(EntityHitResult entityHitResult, ComponentArrowEntity arrow) {
        if (!arrow.level().isClientSide()) {
            Entity hitEntity = entityHitResult.getEntity();
            if (hitEntity instanceof LivingEntity living) {
                // Apply Glowing effect like spectral arrows
                living.addEffect(new MobEffectInstance(MobEffects.GLOWING, GLOWING_DURATION, 0));
            }
        }
    }

    @Override
    public void onTick(ComponentArrowEntity arrow) {
        // Glowing yellow particles
        if (arrow.level().isClientSide() && !arrow.isArrowInGround()) {
            arrow.level().addParticle(ParticleTypes.END_ROD,
                    arrow.getX(), arrow.getY(), arrow.getZ(),
                    0.0, 0.0, 0.0);

            if (arrow.getRandom().nextInt(2) == 0) {
                arrow.level().addParticle(ParticleTypes.GLOW,
                        arrow.getX() + (arrow.getRandom().nextDouble() - 0.5) * 0.3,
                        arrow.getY() + (arrow.getRandom().nextDouble() - 0.5) * 0.3,
                        arrow.getZ() + (arrow.getRandom().nextDouble() - 0.5) * 0.3,
                        0.0, 0.0, 0.0);
            }
        }
    }
}
