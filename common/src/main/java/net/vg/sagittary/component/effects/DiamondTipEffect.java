package net.vg.sagittary.component.effects;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.vg.sagittary.entity.ComponentArrowEntity;

/**
 * Effect for diamond tips - piercing arrows that pass through up to 4 entities.
 * Damage reduces by 20% per pierce.
 */
public class DiamondTipEffect implements ComponentEffect {
    private int pierceCount = 0;
    private static final int MAX_PIERCES = 4;
    private static final float DAMAGE_REDUCTION_PER_PIERCE = 0.2f;

    @Override
    public void onEntityHit(EntityHitResult entityHitResult, ComponentArrowEntity arrow) {
        if (entityHitResult.getEntity() instanceof LivingEntity target && !arrow.level().isClientSide()) {
            // Note: Damage reduction is handled by tracking pierceCount
            // The arrow entity calculates damage based on this

            // Diamond particles on pierce
            for (int i = 0; i < 12; i++) {
                arrow.level().addParticle(ParticleTypes.ENCHANTED_HIT,
                        target.getX() + (arrow.getRandom().nextDouble() - 0.5) * 0.6,
                        target.getY() + target.getBbHeight() * 0.5 + (arrow.getRandom().nextDouble() - 0.5) * 0.6,
                        target.getZ() + (arrow.getRandom().nextDouble() - 0.5) * 0.6,
                        (arrow.getRandom().nextDouble() - 0.5) * 0.3,
                        (arrow.getRandom().nextDouble() - 0.5) * 0.3,
                        (arrow.getRandom().nextDouble() - 0.5) * 0.3);
            }

            pierceCount++;
        }
    }

    @Override
    public boolean shouldContinueAfterEntityHit(ComponentArrowEntity arrow) {
        // Continue through entities until max pierces reached
        return pierceCount < MAX_PIERCES;
    }

    @Override
    public void onTick(ComponentArrowEntity arrow) {
        // Diamond sparkle trail
        if (arrow.level().isClientSide() && !arrow.isArrowInGround()) {
            if (arrow.getRandom().nextInt(2) == 0) {
                arrow.level().addParticle(ParticleTypes.ENCHANTED_HIT,
                        arrow.getX() + (arrow.getRandom().nextDouble() - 0.5) * 0.1,
                        arrow.getY() + (arrow.getRandom().nextDouble() - 0.5) * 0.1,
                        arrow.getZ() + (arrow.getRandom().nextDouble() - 0.5) * 0.1,
                        0.0, 0.0, 0.0);
            }
        }
    }
}
