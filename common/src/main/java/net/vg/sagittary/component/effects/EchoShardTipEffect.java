package net.vg.sagittary.component.effects;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.vg.sagittary.entity.ComponentArrowEntity;

import java.util.List;

/**
 * Effect for echo shard tips - AOE damage burst on hit (entity OR block).
 * No glowing, no homing. Just raw sonic damage in an area.
 */
public class EchoShardTipEffect implements ComponentEffect {
    private static final float AOE_DAMAGE = 3.0f;
    private static final double AOE_RADIUS = 4.0;

    @Override
    public void onEntityHit(EntityHitResult entityHitResult, ComponentArrowEntity arrow) {
        if (!arrow.level().isClientSide()) {
            performAOE(arrow, entityHitResult.getEntity());
        }
    }

    @Override
    public void onBlockHit(BlockHitResult blockHitResult, ComponentArrowEntity arrow) {
        if (!arrow.level().isClientSide()) {
            performAOE(arrow, null);
        }
    }

    private void performAOE(ComponentArrowEntity arrow, net.minecraft.world.entity.Entity excludeEntity) {
        // AOE damage to all nearby entities
        AABB aoe = new AABB(
                arrow.getX() - AOE_RADIUS, arrow.getY() - AOE_RADIUS, arrow.getZ() - AOE_RADIUS,
                arrow.getX() + AOE_RADIUS, arrow.getY() + AOE_RADIUS, arrow.getZ() + AOE_RADIUS);

        List<LivingEntity> nearby = arrow.level().getEntitiesOfClass(LivingEntity.class, aoe,
                e -> e != excludeEntity && e != arrow.getOwner());

        for (LivingEntity target : nearby) {
            // Distance-based damage falloff
            double distance = target.distanceTo(arrow);
            float damage = (float) (AOE_DAMAGE * (1.0 - (distance / AOE_RADIUS)));
            if (damage > 0) {
                target.hurt(arrow.damageSources().sonicBoom(arrow), damage);
            }
        }

        // Sonic boom particle burst
        for (int i = 0; i < 30; i++) {
            double theta = arrow.getRandom().nextDouble() * Math.PI * 2;
            double phi = arrow.getRandom().nextDouble() * Math.PI;
            double r = AOE_RADIUS * arrow.getRandom().nextDouble();
            double x = r * Math.sin(phi) * Math.cos(theta);
            double y = r * Math.cos(phi);
            double z = r * Math.sin(phi) * Math.sin(theta);

            arrow.level().addParticle(ParticleTypes.SCULK_CHARGE_POP,
                    arrow.getX() + x, arrow.getY() + y, arrow.getZ() + z,
                    x * 0.05, y * 0.05, z * 0.05);
        }

        // Central sonic boom effect
        arrow.level().addParticle(ParticleTypes.SONIC_BOOM,
                arrow.getX(), arrow.getY(), arrow.getZ(),
                0.0, 0.0, 0.0);
    }

    @Override
    public void onTick(ComponentArrowEntity arrow) {
        // Deep blue/purple particle trail
        if (arrow.level().isClientSide() && !arrow.isArrowInGround()) {
            if (arrow.getRandom().nextInt(2) == 0) {
                arrow.level().addParticle(ParticleTypes.SCULK_SOUL,
                        arrow.getX() + (arrow.getRandom().nextDouble() - 0.5) * 0.2,
                        arrow.getY() + (arrow.getRandom().nextDouble() - 0.5) * 0.2,
                        arrow.getZ() + (arrow.getRandom().nextDouble() - 0.5) * 0.2,
                        0.0, 0.0, 0.0);
            }
        }
    }
}
