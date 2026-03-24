package net.vg.sagittary.component.effects;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.EntityHitResult;
import net.vg.sagittary.entity.ComponentArrowEntity;

/**
 * Effect for flint tips - basic arrow tip with minimal effects.
 * Provides some debug particles for visual feedback.
 */
public class FlintTipEffect implements ComponentEffect {
    
    @Override
    public void onTick(ComponentArrowEntity arrow) {
        // Add occasional crit particles for visual debugging
        if (arrow.level().isClientSide && !arrow.onGround() && arrow.getRandom().nextInt(5) == 0) {
            arrow.level().addParticle(ParticleTypes.CRIT,
                arrow.getX(), arrow.getY(), arrow.getZ(), 
                0.0, 0.0, 0.0);
        }
    }
}