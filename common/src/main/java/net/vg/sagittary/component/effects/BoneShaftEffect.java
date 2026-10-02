package net.vg.sagittary.component.effects;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.vg.sagittary.entity.ComponentArrowEntity;

//? if >=26.2 {
/*import static net.minecraft.world.entity.EntityTypes.*;
*///? } else {
import static net.minecraft.world.entity.EntityType.*;
//? }

/**
 * Effect for bone shafts - Smite effect (extra damage to undead).
 * Deals bonus damage to undead mobs.
 */
public class BoneShaftEffect implements ComponentEffect {
    private static final float SMITE_BONUS_DAMAGE = 3.0f;

    private static boolean isUndead(LivingEntity entity) {
        EntityType<?> type = entity.getType();
        return type == ZOMBIE || type == SKELETON || type == WITHER_SKELETON ||
               type == STRAY || type == DROWNED || type == HUSK || type == PHANTOM ||
               type == WITHER || type == ZOGLIN || type == ZOMBIE_VILLAGER ||
               type == ZOMBIFIED_PIGLIN || type == SKELETON_HORSE || type == ZOMBIE_HORSE;
    }

    @Override
    public void onEntityHit(EntityHitResult entityHitResult, ComponentArrowEntity arrow) {
        if (entityHitResult.getEntity() instanceof LivingEntity target && !arrow.level().isClientSide()) {
            // Check if target is undead
            if (isUndead(target)) {
                // Deal bonus smite damage
                target.hurt(arrow.damageSources().magic(), SMITE_BONUS_DAMAGE);

                // Play smite sound - distinct sound to confirm undead hit
                arrow.level().playSound(null, target.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP,
                        SoundSource.PLAYERS, 0.8f, 1.2f);

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
