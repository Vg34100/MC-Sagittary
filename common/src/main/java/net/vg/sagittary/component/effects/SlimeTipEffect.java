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
 * Maximum 4 bounces before the arrow sticks normally.
 * Bounces like a bouncy ball - maintains energy and reflects properly.
 *
 * Note: Bounce count is stored in the entity, not this effect, because
 * effect instances may be recreated during the arrow's lifetime.
 */
public class SlimeTipEffect implements ComponentEffect {

    @Override
    public boolean handleBlockHitAndContinue(BlockHitResult blockHitResult, ComponentArrowEntity arrow) {
        // Only bounce if we haven't exceeded max bounces
        if (!arrow.canBounce()) {
            return false; // Let arrow settle normally
        }

        if (!arrow.level().isClientSide()) {
            Vec3 motion = arrow.getDeltaMovement();
            Vec3 normal = new Vec3(
                    blockHitResult.getDirection().getStepX(),
                    blockHitResult.getDirection().getStepY(),
                    blockHitResult.getDirection().getStepZ());

            // Reflect motion off the surface - bouncy ball physics
            // Use high energy retention (0.85) for bouncy feel
            double dot = motion.dot(normal);
            Vec3 reflected = motion.subtract(normal.scale(2 * dot)).scale(0.85);

            // Ensure minimum bounce velocity for satisfying bounces
            double minBounceSpeed = 0.4;
            if (reflected.length() < minBounceSpeed) {
                reflected = reflected.normalize().scale(minBounceSpeed);
            }

            // For floor hits, ensure good upward bounce
            if (normal.y > 0.5 && reflected.y < 0.3) {
                // Hitting floor - bounce up with at least 60% of horizontal speed as vertical
                double horizontalSpeed = Math.sqrt(reflected.x * reflected.x + reflected.z * reflected.z);
                reflected = new Vec3(reflected.x * 0.9, Math.max(0.35, horizontalSpeed * 0.6), reflected.z * 0.9);
            }

            // Apply the bounce velocity
            arrow.setDeltaMovement(reflected);

            // Move arrow slightly away from the block to prevent re-collision
            Vec3 pushOut = normal.scale(0.1);
            arrow.setPos(arrow.getX() + pushOut.x, arrow.getY() + pushOut.y, arrow.getZ() + pushOut.z);

            // Play bounce sound
            arrow.level().playSound(null, arrow.blockPosition(), SoundEvents.SLIME_BLOCK_FALL,
                    SoundSource.NEUTRAL, 0.8f, 1.0f + arrow.getRandom().nextFloat() * 0.4f);

            // Spawn particles
            spawnBounceParticles(arrow);

            arrow.incrementBounceCount();
        }

        return true; // Arrow continues flying (don't settle)
    }

    @Override
    public void onBlockHit(BlockHitResult blockHitResult, ComponentArrowEntity arrow) {
        // Additional effects after bounce (particles are handled in handleBlockHitAndContinue)
    }

    @Override
    public void onEntityHit(EntityHitResult entityHitResult, ComponentArrowEntity arrow) {
        // Bounce off entity if we have bounces left
        if (arrow.canBounce() && !arrow.level().isClientSide()) {
            Entity hitEntity = entityHitResult.getEntity();

            // Calculate bounce direction away from entity center
            Vec3 motion = arrow.getDeltaMovement();
            Vec3 toArrow = arrow.position().subtract(hitEntity.position()).normalize();

            // Bounce away from entity with upward bias
            Vec3 bounceDir = new Vec3(toArrow.x, Math.max(0.4, Math.abs(toArrow.y)), toArrow.z).normalize();

            // Apply bounce with good energy retention
            double speed = Math.max(motion.length() * 0.8, 0.5);
            arrow.setDeltaMovement(bounceDir.scale(speed));

            // Move away from entity to prevent re-collision
            arrow.setPos(
                    arrow.getX() + bounceDir.x * 0.3,
                    arrow.getY() + bounceDir.y * 0.3,
                    arrow.getZ() + bounceDir.z * 0.3);

            // Play bounce sound
            arrow.level().playSound(null, arrow.blockPosition(), SoundEvents.SLIME_BLOCK_FALL,
                    SoundSource.NEUTRAL, 0.8f, 1.2f + arrow.getRandom().nextFloat() * 0.3f);

            spawnBounceParticles(arrow);

            arrow.incrementBounceCount();
        }
    }

    private void spawnBounceParticles(ComponentArrowEntity arrow) {
        for (int i = 0; i < 8; i++) {
            arrow.level().addParticle(ParticleTypes.ITEM_SLIME,
                    arrow.getX(), arrow.getY(), arrow.getZ(),
                    (arrow.getRandom().nextDouble() - 0.5) * 0.3,
                    arrow.getRandom().nextDouble() * 0.2,
                    (arrow.getRandom().nextDouble() - 0.5) * 0.3);
        }
    }

    @Override
    public boolean shouldContinueAfterEntityHit(ComponentArrowEntity arrow) {
        // Allow arrow to continue after hitting entity (for bouncing)
        return arrow.canBounce();
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
