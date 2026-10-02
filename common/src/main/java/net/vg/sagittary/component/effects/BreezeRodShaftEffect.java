package net.vg.sagittary.component.effects;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.vg.sagittary.entity.ComponentArrowEntity;

import java.util.Comparator;
import java.util.List;

/**
 * Effect for breeze rod shafts - lightweight, faster, HOMES toward enemies, knockback on hit.
 */
public class BreezeRodShaftEffect implements ComponentEffect {
    private static final double HOMING_RANGE = 12.0;
    private static final double HOMING_STRENGTH = 0.08;
    private static final float KNOCKBACK_STRENGTH = 1.5f;

    @Override
    public double getGravityModifier(ComponentArrowEntity arrow) {
        // Lightweight - 40% less gravity
        return 0.6;
    }

    @Override
    public double getSpeedModifier(ComponentArrowEntity arrow) {
        // Faster arrows
        return 1.25;
    }

    @Override
    public void onEntityHit(EntityHitResult entityHitResult, ComponentArrowEntity arrow) {
        if (entityHitResult.getEntity() instanceof LivingEntity target && !arrow.level().isClientSide()) {
            // Apply knockback
            Vec3 knockbackDir = arrow.getDeltaMovement().normalize();
            //? if >=26.2 {
            /*target.knockback(KNOCKBACK_STRENGTH, -knockbackDir.x, -knockbackDir.z,
                    arrow.damageSources().arrow(arrow, arrow.getOwner()), 0.0F);
            *///? } else {
            target.knockback(KNOCKBACK_STRENGTH, -knockbackDir.x, -knockbackDir.z);
            //? }

            // Wind burst particles
            for (int i = 0; i < 15; i++) {
                arrow.level().addParticle(ParticleTypes.CLOUD,
                        target.getX() + (arrow.getRandom().nextDouble() - 0.5) * 1.0,
                        target.getY() + target.getBbHeight() * 0.5 + (arrow.getRandom().nextDouble() - 0.5) * 1.0,
                        target.getZ() + (arrow.getRandom().nextDouble() - 0.5) * 1.0,
                        knockbackDir.x * 0.3,
                        0.1,
                        knockbackDir.z * 0.3);
            }
        }
    }

    @Override
    public void onTick(ComponentArrowEntity arrow) {
        // Homing behavior - seek toward nearest living entity
        if (!arrow.isArrowInGround() && !arrow.level().isClientSide()) {
            AABB searchArea = new AABB(
                    arrow.getX() - HOMING_RANGE, arrow.getY() - HOMING_RANGE, arrow.getZ() - HOMING_RANGE,
                    arrow.getX() + HOMING_RANGE, arrow.getY() + HOMING_RANGE, arrow.getZ() + HOMING_RANGE);

            List<LivingEntity> nearby = arrow.level().getEntitiesOfClass(LivingEntity.class, searchArea,
                    e -> e != arrow.getOwner() && e.isAlive() && !e.isInvisible());

            if (!nearby.isEmpty()) {
                // Find closest entity
                Entity closest = nearby.stream()
                        .min(Comparator.comparingDouble(e -> e.distanceToSqr(arrow)))
                        .orElse(null);

                if (closest != null) {
                    // Calculate direction to target
                    Vec3 toTarget = closest.position().add(0, closest.getBbHeight() * 0.5, 0)
                            .subtract(arrow.position()).normalize();
                    Vec3 currentVel = arrow.getDeltaMovement();
                    double speed = currentVel.length();

                    if (speed > 0.1) { // Only home if moving fast enough
                        Vec3 currentDir = currentVel.normalize();

                        // Blend current direction with target direction
                        Vec3 newDirection = currentDir.add(toTarget.scale(HOMING_STRENGTH)).normalize();

                        // Apply new velocity maintaining speed
                        arrow.setDeltaMovement(newDirection.scale(speed));
                    }
                }
            }
        }

        // Wind/breeze particle trail
        if (arrow.level().isClientSide() && !arrow.isArrowInGround()) {
            if (arrow.getRandom().nextInt(2) == 0) {
                arrow.level().addParticle(ParticleTypes.CLOUD,
                        arrow.getX() + (arrow.getRandom().nextDouble() - 0.5) * 0.2,
                        arrow.getY() + (arrow.getRandom().nextDouble() - 0.5) * 0.2,
                        arrow.getZ() + (arrow.getRandom().nextDouble() - 0.5) * 0.2,
                        0.0, 0.02, 0.0);
            }
            if (arrow.getRandom().nextInt(4) == 0) {
                arrow.level().addParticle(ParticleTypes.POOF,
                        arrow.getX(), arrow.getY(), arrow.getZ(),
                        0.0, 0.0, 0.0);
            }
        }
    }
}
