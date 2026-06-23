package net.vg.sagittary.component.effects;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.vg.sagittary.entity.ComponentArrowEntity;

/**
 * Effect for bone shafts - Smite effect (extra damage to undead).
 * Deals bonus damage to undead mobs.
 */
public class BoneShaftEffect implements ComponentEffect {
    private static final float SMITE_BONUS_DAMAGE = 3.0f;

    private static boolean isUndead(LivingEntity entity) {
        EntityType<?> type = entity.getType();
        return type == EntityType.ZOMBIE ||
               type == EntityType.SKELETON ||
               type == EntityType.WITHER_SKELETON ||
               type == EntityType.STRAY ||
               type == EntityType.DROWNED ||
               type == EntityType.HUSK ||
               type == EntityType.PHANTOM ||
               type == EntityType.WITHER ||
               type == EntityType.ZOGLIN ||
               type == EntityType.ZOMBIE_VILLAGER ||
               type == EntityType.ZOMBIFIED_PIGLIN ||
               type == EntityType.SKELETON_HORSE ||
               type == EntityType.ZOMBIE_HORSE;
    }

    @Override
    public void onEntityHit(EntityHitResult entityHitResult, ComponentArrowEntity arrow) {
        if (entityHitResult.getEntity() instanceof LivingEntity target && !arrow.level().isClientSide()) {
            // Check if target is undead
            if (isUndead(target)) {
                // Deal bonus smite damage
                target.hurt(arrow.damageSources().magic(), SMITE_BONUS_DAMAGE);

                // Holy/smite particles
                for (int i = 0; i < 15; i++) {
                    arrow.level().addParticle(ParticleTypes.ENCHANTED_HIT,
                            target.getX() + (arrow.getRandom().nextDouble() - 0.5) * 0.8,
                            target.getY() + target.getBbHeight() * 0.5 + (arrow.getRandom().nextDouble() - 0.5) * 0.8,
                            target.getZ() + (arrow.getRandom().nextDouble() - 0.5) * 0.8,
                            0.0, 0.1, 0.0);
                }

                // Bone crack particles
                for (int i = 0; i < 8; i++) {
                    arrow.level().addParticle(ParticleTypes.CRIT,
                            target.getX() + (arrow.getRandom().nextDouble() - 0.5) * 0.6,
                            target.getY() + target.getBbHeight() * 0.5,
                            target.getZ() + (arrow.getRandom().nextDouble() - 0.5) * 0.6,
                            (arrow.getRandom().nextDouble() - 0.5) * 0.2,
                            0.1,
                            (arrow.getRandom().nextDouble() - 0.5) * 0.2);
                }
            } else {
                // Regular bone chip particles on hit for non-undead
                for (int i = 0; i < 8; i++) {
                    arrow.level().addParticle(ParticleTypes.ASH,
                            target.getX() + (arrow.getRandom().nextDouble() - 0.5) * 0.5,
                            target.getY() + target.getBbHeight() * 0.5,
                            target.getZ() + (arrow.getRandom().nextDouble() - 0.5) * 0.5,
                            (arrow.getRandom().nextDouble() - 0.5) * 0.1,
                            arrow.getRandom().nextDouble() * 0.1,
                            (arrow.getRandom().nextDouble() - 0.5) * 0.1);
                }
            }
        }
    }

    @Override
    public void onTick(ComponentArrowEntity arrow) {
        // Subtle bone dust particles
        if (arrow.level().isClientSide() && !arrow.isArrowInGround() && arrow.getRandom().nextInt(6) == 0) {
            arrow.level().addParticle(ParticleTypes.ASH,
                    arrow.getX(), arrow.getY(), arrow.getZ(),
                    0.0, 0.0, 0.0);
        }
    }
}
