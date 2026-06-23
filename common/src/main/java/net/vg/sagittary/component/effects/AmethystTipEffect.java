package net.vg.sagittary.component.effects;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.vg.sagittary.entity.ComponentArrowEntity;

/**
 * Effect for amethyst tips - applies slowness effect and creates purple particle effects.
 */
public class AmethystTipEffect implements ComponentEffect {
    
    @Override
    public void onEntityHit(EntityHitResult entityHitResult, ComponentArrowEntity arrow) {
        if (entityHitResult.getEntity() instanceof LivingEntity target) {
            // Apply slowness effect like original amethyst arrow
            target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 20, 255));
            
            // Add particle burst on hit
            if (!arrow.level().isClientSide()) {
                for (int i = 0; i < 10; i++) {
                    arrow.level().addParticle(ParticleTypes.END_ROD,
                        target.getX() + (arrow.getRandom().nextDouble() - 0.5) * 0.8,
                        target.getY() + target.getBbHeight() * 0.5 + (arrow.getRandom().nextDouble() - 0.5) * 0.8,
                        target.getZ() + (arrow.getRandom().nextDouble() - 0.5) * 0.8,
                        (arrow.getRandom().nextDouble() - 0.5) * 0.2,
                        (arrow.getRandom().nextDouble() - 0.5) * 0.2,
                        (arrow.getRandom().nextDouble() - 0.5) * 0.2);
                }
            }
        }
    }
    
    @Override
    public void onTick(ComponentArrowEntity arrow) {
        // Amethyst arrows get purple particles like the original amethyst arrow
        if (arrow.level().isClientSide() && !arrow.onGround()) {
            arrow.level().addParticle(ParticleTypes.END_ROD, 
                arrow.getX(), arrow.getY(), arrow.getZ(), 
                0.0, 0.0, 0.0);
            
            if (arrow.getRandom().nextInt(3) == 0) {
                arrow.level().addParticle(ParticleTypes.ENCHANT,
                    arrow.getX() + (arrow.getRandom().nextDouble() - 0.5) * 0.3,
                    arrow.getY() + (arrow.getRandom().nextDouble() - 0.5) * 0.3,
                    arrow.getZ() + (arrow.getRandom().nextDouble() - 0.5) * 0.3,
                    0.0, 0.0, 0.0);
            }
        }
    }
    
    @Override
    public void applyInitialModifiers(ComponentArrowEntity arrow) {
        // Amethyst tips have 1.2x damage modifier (handled by ArrowComponent)
        // Speed modifier of +0.1 
        arrow.setDeltaMovement(arrow.getDeltaMovement().scale(1.0 + 0.1));
    }
}
