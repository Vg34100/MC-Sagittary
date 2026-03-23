package net.vg.amethyst.component.effects;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.vg.amethyst.entity.ComponentArrowEntity;

/**
 * Effect for blaze rod shafts - sets targets on fire and leaves fire trail.
 */
public class BlazeRodShaftEffect implements ComponentEffect {
    
    @Override
    public void onEntityHit(EntityHitResult entityHitResult, ComponentArrowEntity arrow) {
        if (entityHitResult.getEntity() instanceof LivingEntity target) {
            // Set target on fire
            target.setRemainingFireTicks(100); // 5 seconds of fire
            if (!arrow.level().isClientSide) {
                System.out.println("Applied fire effect from blaze rod shaft!");
            }
        }
    }
    
    @Override
    public void onTick(ComponentArrowEntity arrow) {
        // Blaze rod arrows leave fire trail
        if (arrow.level().isClientSide && !arrow.onGround() && arrow.getRandom().nextInt(2) == 0) {
            arrow.level().addParticle(ParticleTypes.FLAME,
                arrow.getX() + (arrow.getRandom().nextDouble() - 0.5) * 0.2,
                arrow.getY() + (arrow.getRandom().nextDouble() - 0.5) * 0.2,
                arrow.getZ() + (arrow.getRandom().nextDouble() - 0.5) * 0.2,
                0.0, 0.0, 0.0);
        }
    }
    
    @Override
    public void applyInitialModifiers(ComponentArrowEntity arrow) {
        // Blaze rod shaft: 1.1x damage modifier (handled by ArrowComponent)
        // No speed modifier
    }
}