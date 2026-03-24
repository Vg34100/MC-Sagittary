package net.vg.sagittary.component.effects;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.vg.sagittary.entity.ComponentArrowEntity;

/**
 * Effect for phantom membrane fletching - enables gliding behavior and creates ghostly particles.
 */
public class PhantomMembraneFletchingEffect implements ComponentEffect {
    
    @Override
    public void onTick(ComponentArrowEntity arrow) {
        if (!arrow.isArrowInGround()) {
            // Gravity reduction is now handled via getGravityModifier()
            
            // Phantom membrane arrows get ghostly trail
            if (arrow.level().isClientSide && arrow.getRandom().nextInt(3) == 0) {
                arrow.level().addParticle(ParticleTypes.SOUL,
                    arrow.getX() + (arrow.getRandom().nextDouble() - 0.5) * 0.3,
                    arrow.getY() + (arrow.getRandom().nextDouble() - 0.5) * 0.3,
                    arrow.getZ() + (arrow.getRandom().nextDouble() - 0.5) * 0.3,
                    0.0, 0.0, 0.0);
            }
        }
    }
    
    @Override
    public void applyInitialModifiers(ComponentArrowEntity arrow) {
        // Speed and gravity modifications are now handled via the modifier methods
        // This method can be used for other phantom membrane-specific effects if needed
    }
    
    @Override
    public double getGravityModifier(ComponentArrowEntity arrow) {
        return 0.6; // 40% less gravity for smooth gliding effect
    }
    
    @Override
    public double getSpeedModifier(ComponentArrowEntity arrow) {
        return 0.95; // 5% slower for more controlled flight
    }
}