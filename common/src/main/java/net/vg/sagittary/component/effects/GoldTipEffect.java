package net.vg.sagittary.component.effects;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.vg.sagittary.entity.ComponentArrowEntity;

/**
 * Effect for gold tips - increased critical hit chance.
 * Has a 35% chance to deal critical damage (1.5x) on hit.
 * Also provides a 15% speed bonus (from ArrowComponent).
 */
public class GoldTipEffect implements ComponentEffect {

    @Override
    public void onEntityHit(EntityHitResult entityHitResult, ComponentArrowEntity arrow) {
        if (entityHitResult.getEntity() instanceof LivingEntity target && !arrow.level().isClientSide()) {
            // 35% chance for critical hit
            if (arrow.getRandom().nextFloat() < 0.35f) {
                // Deal additional critical damage (50% of base 2.0 = 1.0 extra)
                float critDamage = 1.0f;
                target.hurt(arrow.damageSources().arrow(arrow, arrow.getOwner()), critDamage);

                // Critical hit particle burst
                for (int i = 0; i < 15; i++) {
                    arrow.level().addParticle(ParticleTypes.ENCHANTED_HIT,
                            target.getX() + (arrow.getRandom().nextDouble() - 0.5) * 0.8,
                            target.getY() + target.getBbHeight() * 0.5 + (arrow.getRandom().nextDouble() - 0.5) * 0.8,
                            target.getZ() + (arrow.getRandom().nextDouble() - 0.5) * 0.8,
                            (arrow.getRandom().nextDouble() - 0.5) * 0.4,
                            (arrow.getRandom().nextDouble() - 0.5) * 0.4,
                            (arrow.getRandom().nextDouble() - 0.5) * 0.4);
                }

                // Additional golden sparkles for crit
                for (int i = 0; i < 10; i++) {
                    arrow.level().addParticle(ParticleTypes.WAX_ON,
                            target.getX() + (arrow.getRandom().nextDouble() - 0.5) * 1.0,
                            target.getY() + target.getBbHeight() * 0.5 + (arrow.getRandom().nextDouble() - 0.5) * 1.0,
                            target.getZ() + (arrow.getRandom().nextDouble() - 0.5) * 1.0,
                            0.0, 0.1, 0.0);
                }
            }
        }
    }

    @Override
    public void onTick(ComponentArrowEntity arrow) {
        // Golden sparkle trail
        if (arrow.level().isClientSide() && !arrow.isArrowInGround()) {
            if (arrow.getRandom().nextInt(2) == 0) {
                arrow.level().addParticle(ParticleTypes.WAX_ON,
                        arrow.getX() + (arrow.getRandom().nextDouble() - 0.5) * 0.2,
                        arrow.getY() + (arrow.getRandom().nextDouble() - 0.5) * 0.2,
                        arrow.getZ() + (arrow.getRandom().nextDouble() - 0.5) * 0.2,
                        0.0, 0.0, 0.0);
            }
            if (arrow.getRandom().nextInt(4) == 0) {
                arrow.level().addParticle(ParticleTypes.ENCHANT,
                        arrow.getX(), arrow.getY(), arrow.getZ(),
                        0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    public double getSpeedModifier(ComponentArrowEntity arrow) {
        return 1.15; // 15% faster (gold is light and aerodynamic)
    }
}
