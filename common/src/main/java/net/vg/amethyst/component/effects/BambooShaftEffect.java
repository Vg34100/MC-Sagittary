package net.vg.amethyst.component.effects;

import net.vg.amethyst.entity.ComponentArrowEntity;

/**
 * Effect for bamboo shafts - increases speed but reduces accuracy.
 */
public class BambooShaftEffect implements ComponentEffect {
    
    @Override
    public void applyInitialModifiers(ComponentArrowEntity arrow) {
        // Add slight randomness to velocity for reduced accuracy
        double accuracyReduction = 0.05; // 5% accuracy reduction
        arrow.setDeltaMovement(
            arrow.getDeltaMovement().add(
                (arrow.getRandom().nextDouble() - 0.5) * accuracyReduction,
                (arrow.getRandom().nextDouble() - 0.5) * accuracyReduction,
                (arrow.getRandom().nextDouble() - 0.5) * accuracyReduction
            )
        );
    }
    
    @Override
    public double getSpeedModifier(ComponentArrowEntity arrow) {
        return 1.9; // 90% faster
    }
}