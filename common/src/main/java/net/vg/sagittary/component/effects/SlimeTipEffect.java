package net.vg.sagittary.component.effects;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.vg.sagittary.entity.ComponentArrowEntity;

/**
 * Effect for slime tips - arrows bounce off blocks AND entities.
 * Maximum 3-5 bounces before the arrow sticks normally.
 */
public class SlimeTipEffect implements ComponentEffect {
    private int bounceCount = 0;
    private static final int MAX_BOUNCES = 4; // 3-5 range, using 4

    @Override
    public void onBlockHit(BlockHitResult blockHitResult, ComponentArrowEntity arrow) {
        if (bounceCount < MAX_BOUNCES && !arrow.level().isClientSide()) {
            performBounce(arrow, new Vec3(
                    blockHitResult.getDirection().getStepX(),
                    blockHitResult.getDirection().getStepY(),
                    blockHitResult.getDirection().getStepZ()));
        }
    }

    @Override
    public void onEntityHit(EntityHitResult entityHitResult, ComponentArrowEntity arrow) {
        if (bounceCount < MAX_BOUNCES && !arrow.level().isClientSide()) {
            Entity hitEntity = entityHitResult.getEntity();

            // Calculate bounce direction away from entity center
            Vec3 toArrow = arrow.position().subtract(hitEntity.position()).normalize();
            // Use the direction from entity to arrow as the "normal"
            Vec3 normal = new Vec3(toArrow.x, Math.max(0.3, toArrow.y), toArrow.z).normalize();

            performBounce(arrow, normal);

            // Reduce damage on bounce but still deal some
            // The arrow continues instead of stopping
        }
    }

    private void performBounce(ComponentArrowEntity arrow, Vec3 normal) {
        Vec3 motion = arrow.getDeltaMovement();

        // Reflect the motion off the surface with some energy loss
        Vec3 reflected = motion.subtract(normal.scale(2 * motion.dot(normal))).scale(0.6);

        // Ensure minimum upward velocity so it doesn't get stuck
        if (reflected.y < 0.1 && normal.y > 0) {
            reflected = new Vec3(reflected.x, 0.15, reflected.z);
        }

        // Apply the new velocity
        arrow.setDeltaMovement(reflected);

        // Mark arrow as not in ground so it continues flying
        arrow.setArrowInGround(false);

        // Play bounce sound
        arrow.level().playSound(null, arrow.blockPosition(), SoundEvents.SLIME_BLOCK_FALL,
                SoundSource.NEUTRAL, 0.8f, 1.0f + arrow.getRandom().nextFloat() * 0.4f);

        // Spawn particles
        for (int i = 0; i < 8; i++) {
            arrow.level().addParticle(ParticleTypes.ITEM_SLIME,
                    arrow.getX(), arrow.getY(), arrow.getZ(),
                    (arrow.getRandom().nextDouble() - 0.5) * 0.3,
                    arrow.getRandom().nextDouble() * 0.2,
                    (arrow.getRandom().nextDouble() - 0.5) * 0.3);
        }

        bounceCount++;
    }

    @Override
    public boolean shouldContinueAfterEntityHit(ComponentArrowEntity arrow) {
        // Allow arrow to continue after hitting entity (for bouncing)
        return bounceCount < MAX_BOUNCES;
    }

    @Override
    public void onTick(ComponentArrowEntity arrow) {
        // Green slime trail particles
        if (arrow.level().isClientSide() && !arrow.isArrowInGround() && arrow.getRandom().nextInt(3) == 0) {
            arrow.level().addParticle(ParticleTypes.ITEM_SLIME,
                    arrow.getX(), arrow.getY(), arrow.getZ(),
                    0.0, 0.0, 0.0);
        }
    }
}
