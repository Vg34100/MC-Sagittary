package net.vg.sagittary.component.effects;

import net.minecraft.world.phys.BlockHitResult;
import net.vg.sagittary.entity.ComponentArrowEntity;

/**
 * Effect for paper fletching - increases speed but arrow is destroyed when hitting blocks.
 */
public class PaperFletchingEffect implements ComponentEffect {
    
    @Override
    public void onBlockHit(BlockHitResult blockHitResult, ComponentArrowEntity arrow) {
        // Paper fletching arrows are destroyed when hitting blocks/ground
        if (!arrow.level().isClientSide()) {
            System.out.println("Paper fletching arrow destroyed on block hit!");
        }
        arrow.discard();
    }
    
    @Override
    public void applyInitialModifiers(ComponentArrowEntity arrow) {
        // Paper fletching: +10% speed
        float speedModifier = 0.1f;
        arrow.setDeltaMovement(arrow.getDeltaMovement().scale(1.0 + speedModifier));
    }
    
    @Override
    public boolean shouldPreventPickup(ComponentArrowEntity arrow) {
        // Paper fletching arrows can't be picked up if they hit blocks (they're destroyed)
        return arrow.onGround();
    }
}
