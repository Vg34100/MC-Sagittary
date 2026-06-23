package net.vg.sagittary.component.effects;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.vg.sagittary.entity.ComponentArrowEntity;

/**
 * Effect for iron tips - heavier arrows with armor piercing.
 * Has more gravity (drops faster) but ignores 30% of armor.
 */
public class IronTipEffect implements ComponentEffect {
    private static final float ARMOR_PIERCE_PERCENT = 0.3f;

    @Override
    public void onEntityHit(EntityHitResult entityHitResult, ComponentArrowEntity arrow) {
        if (entityHitResult.getEntity() instanceof LivingEntity target && !arrow.level().isClientSide()) {
            // Iron tips bypass some armor by dealing additional magic damage
            // Fixed bonus damage that ignores armor
            float bonusDamage = 1.5f; // Fixed armor-piercing bonus

            // Apply additional damage that bypasses armor (using MAGIC damage type)
            target.hurt(arrow.damageSources().magic(), bonusDamage);

            // Metal impact particles
            for (int i = 0; i < 8; i++) {
                arrow.level().addParticle(ParticleTypes.CRIT,
                        target.getX() + (arrow.getRandom().nextDouble() - 0.5) * 0.6,
                        target.getY() + target.getBbHeight() * 0.5 + (arrow.getRandom().nextDouble() - 0.5) * 0.6,
                        target.getZ() + (arrow.getRandom().nextDouble() - 0.5) * 0.6,
                        (arrow.getRandom().nextDouble() - 0.5) * 0.3,
                        (arrow.getRandom().nextDouble() - 0.5) * 0.3,
                        (arrow.getRandom().nextDouble() - 0.5) * 0.3);
            }
        }
    }

    @Override
    public double getGravityModifier(ComponentArrowEntity arrow) {
        // Iron is heavier - 40% more gravity
        return 1.4;
    }

    @Override
    public void onTick(ComponentArrowEntity arrow) {
        // Subtle metallic sparkle particles
        if (arrow.level().isClientSide() && !arrow.isArrowInGround() && arrow.getRandom().nextInt(5) == 0) {
            arrow.level().addParticle(ParticleTypes.CRIT,
                    arrow.getX(), arrow.getY(), arrow.getZ(),
                    0.0, 0.0, 0.0);
        }
    }
}
