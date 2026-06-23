package net.vg.sagittary.component.effects;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.vg.sagittary.entity.ComponentArrowEntity;

/**
 * Interface for component effects that can be applied to arrows.
 * Each component material (tip, shaft, fletching) can have its own implementation.
 * Effects are designed to stack - multiple components can have effects that all apply.
 */
public interface ComponentEffect {
    
    /**
     * Called when the arrow hits a living entity.
     * This is where damage effects, status effects, and special behaviors should be applied.
     * 
     * @param entityHitResult The hit result containing the target entity
     * @param arrow The arrow entity that hit the target
     */
    default void onEntityHit(EntityHitResult entityHitResult, ComponentArrowEntity arrow) {
        // Default: no effect
    }
    
    /**
     * Called when the arrow hits a block.
     * This is where block interaction effects should be applied.
     * 
     * @param blockHitResult The hit result containing block information
     * @param arrow The arrow entity that hit the block
     */
    default void onBlockHit(BlockHitResult blockHitResult, ComponentArrowEntity arrow) {
        // Default: no effect
    }
    
    /**
     * Called every tick while the arrow is in flight.
     * This is where particle effects, flight modifications, and continuous effects should be applied.
     * 
     * @param arrow The arrow entity being ticked
     */
    default void onTick(ComponentArrowEntity arrow) {
        // Default: no effect
    }
    
    /**
     * Called when the arrow is first created to apply initial modifiers.
     * This is where speed, damage, and accuracy modifications should be applied.
     * 
     * @param arrow The arrow entity being modified
     */
    default void applyInitialModifiers(ComponentArrowEntity arrow) {
        // Default: no effect
    }
    
    /**
     * Called to determine the pickup behavior of the arrow.
     * Return true if this component should prevent pickup (e.g., paper fletching destruction).
     * 
     * @param arrow The arrow entity
     * @return true if pickup should be prevented, false otherwise
     */
    default boolean shouldPreventPickup(ComponentArrowEntity arrow) {
        return false; // Default: allow pickup
    }
    
    /**
     * Called to get the gravity modifier for this component.
     * Return a multiplier for the default gravity (1.0 = normal, 0.6 = 40% less gravity, 1.5 = 50% more gravity).
     * 
     * @param arrow The arrow entity
     * @return gravity multiplier (1.0 = normal gravity)
     */
    default double getGravityModifier(ComponentArrowEntity arrow) {
        return 1.0; // Default: normal gravity
    }
    
    /**
     * Called to get the speed modifier for this component.
     * Return a multiplier for the arrow speed (1.0 = normal, 1.9 = 90% faster, 0.95 = 5% slower).
     *
     * @param arrow The arrow entity
     * @return speed multiplier (1.0 = normal speed)
     */
    default double getSpeedModifier(ComponentArrowEntity arrow) {
        return 1.0; // Default: normal speed
    }

    /**
     * Called after hitting an entity to determine if the arrow should continue flying.
     * Used for piercing (diamond) and bouncing (slime) effects.
     *
     * @param arrow The arrow entity
     * @return true if the arrow should continue after hitting an entity, false to stop normally
     */
    default boolean shouldContinueAfterEntityHit(ComponentArrowEntity arrow) {
        return false; // Default: stop after hitting entity
    }
}