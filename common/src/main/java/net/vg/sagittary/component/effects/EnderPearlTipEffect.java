package net.vg.sagittary.component.effects;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.vg.sagittary.entity.ComponentArrowEntity;

/**
 * Effect for ender pearl tips - teleports entities on impact.
 * When hitting an entity: teleports that entity to a random nearby location.
 * When hitting a block: teleports the shooter to that location.
 */
public class EnderPearlTipEffect implements ComponentEffect {

    @Override
    public void onEntityHit(EntityHitResult entityHitResult, ComponentArrowEntity arrow) {
        if (entityHitResult.getEntity() instanceof LivingEntity target && !arrow.level().isClientSide()) {
            // Teleport the target to a random nearby location (within 8 blocks)
            double offsetX = (arrow.getRandom().nextDouble() - 0.5) * 16;
            double offsetZ = (arrow.getRandom().nextDouble() - 0.5) * 16;

            Vec3 newPos = target.position().add(offsetX, 0, offsetZ);

            // Find safe Y position
            double safeY = findSafeY(arrow.level(), newPos.x, newPos.z, target.getY());

            // Teleport effects at old location
            spawnTeleportParticles(arrow.level(), target.getX(), target.getY(), target.getZ());
            arrow.level().playSound(null, target.blockPosition(), SoundEvents.ENDERMAN_TELEPORT,
                    SoundSource.NEUTRAL, 1.0f, 1.0f);

            // Perform teleport
            target.teleportTo(newPos.x, safeY, newPos.z);

            // Teleport effects at new location
            spawnTeleportParticles(arrow.level(), newPos.x, safeY, newPos.z);
            arrow.level().playSound(null, target.blockPosition(), SoundEvents.ENDERMAN_TELEPORT,
                    SoundSource.NEUTRAL, 1.0f, 1.0f);
        }
    }

    @Override
    public void onBlockHit(BlockHitResult blockHitResult, ComponentArrowEntity arrow) {
        Entity owner = arrow.getOwner();
        if (owner instanceof LivingEntity shooter && !arrow.level().isClientSide()) {
            Vec3 hitPos = blockHitResult.getLocation();

            // Teleport effects at old location
            spawnTeleportParticles(arrow.level(), shooter.getX(), shooter.getY(), shooter.getZ());
            arrow.level().playSound(null, shooter.blockPosition(), SoundEvents.ENDERMAN_TELEPORT,
                    SoundSource.NEUTRAL, 1.0f, 1.0f);

            // Teleport shooter to arrow's location
            shooter.teleportTo(hitPos.x, hitPos.y + 0.5, hitPos.z);

            // Teleport effects at new location
            spawnTeleportParticles(arrow.level(), hitPos.x, hitPos.y + 0.5, hitPos.z);
            arrow.level().playSound(null, shooter.blockPosition(), SoundEvents.ENDERMAN_TELEPORT,
                    SoundSource.NEUTRAL, 1.0f, 1.0f);

            // Remove the arrow after teleporting
            arrow.discard();
        }
    }

    @Override
    public void onTick(ComponentArrowEntity arrow) {
        // Purple ender particles
        if (arrow.level().isClientSide() && !arrow.isArrowInGround()) {
            arrow.level().addParticle(ParticleTypes.PORTAL,
                    arrow.getX() + (arrow.getRandom().nextDouble() - 0.5) * 0.3,
                    arrow.getY() + (arrow.getRandom().nextDouble() - 0.5) * 0.3,
                    arrow.getZ() + (arrow.getRandom().nextDouble() - 0.5) * 0.3,
                    (arrow.getRandom().nextDouble() - 0.5) * 0.5,
                    (arrow.getRandom().nextDouble() - 0.5) * 0.5,
                    (arrow.getRandom().nextDouble() - 0.5) * 0.5);

            if (arrow.getRandom().nextInt(3) == 0) {
                arrow.level().addParticle(ParticleTypes.REVERSE_PORTAL,
                        arrow.getX(), arrow.getY(), arrow.getZ(),
                        0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    public boolean shouldPreventPickup(ComponentArrowEntity arrow) {
        return true; // Ender pearl arrows are consumed on use
    }

    private void spawnTeleportParticles(net.minecraft.world.level.Level level, double x, double y, double z) {
        if (level instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 32; i++) {
                serverLevel.sendParticles(ParticleTypes.PORTAL,
                        x + (level.getRandom().nextDouble() - 0.5) * 1.5,
                        y + level.getRandom().nextDouble() * 2.0,
                        z + (level.getRandom().nextDouble() - 0.5) * 1.5,
                        1, 0.0, 0.0, 0.0, 0.0);
            }
        }
    }

    private double findSafeY(net.minecraft.world.level.Level level, double x, double z, double startY) {
        // Simple safe Y finder - start from the original Y and find ground
        for (int dy = 0; dy < 10; dy++) {
            if (!level.getBlockState(new net.minecraft.core.BlockPos((int) x, (int) (startY - dy), (int) z)).isAir() &&
                    level.getBlockState(new net.minecraft.core.BlockPos((int) x, (int) (startY - dy + 1), (int) z)).isAir() &&
                    level.getBlockState(new net.minecraft.core.BlockPos((int) x, (int) (startY - dy + 2), (int) z)).isAir()) {
                return startY - dy + 1;
            }
        }
        return startY;
    }
}
