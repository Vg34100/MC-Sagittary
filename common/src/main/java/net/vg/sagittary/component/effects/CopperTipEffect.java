package net.vg.sagittary.component.effects;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.phys.EntityHitResult;
import net.vg.sagittary.entity.ComponentArrowEntity;

//? if >=26.2 {
/*import static net.minecraft.world.entity.EntityTypes.LIGHTNING_BOLT;
*///? } else {
import static net.minecraft.world.entity.EntityType.LIGHTNING_BOLT;
//? }

/**
 * Effect for copper tips - has a chance to summon lightning and creates electric particles.
 */
public class CopperTipEffect implements ComponentEffect {
    
    @Override
    public void onEntityHit(EntityHitResult entityHitResult, ComponentArrowEntity arrow) {
        if (!(arrow.level() instanceof ServerLevel serverLevel)) return;

        Entity target = entityHitResult.getEntity();
        if (!(target instanceof LivingEntity)) return;

        // 10% chance
        if (arrow.getRandom().nextFloat() < 0.1f) {
            LightningBolt lightning = LIGHTNING_BOLT.create(
                    serverLevel,
                    null,
                    target.blockPosition(),
                    //? if >=26.1 {
                    EntitySpawnReason.EVENT,
                    //? } else {
                    /*MobSpawnType.EVENT,
                    *///? }
                    false,
                    false
            );

            if (lightning != null) {
                lightning.setPos(target.getX(), target.getY(), target.getZ());
                serverLevel.addFreshEntity(lightning);
            }
        }
    }
    
    @Override
    public void onTick(ComponentArrowEntity arrow) {
        // Copper arrows get electric sparks
        if (arrow.level().isClientSide() && !arrow.onGround() && arrow.getRandom().nextInt(4) == 0) {
            arrow.level().addParticle(ParticleTypes.ELECTRIC_SPARK,
                arrow.getX() + (arrow.getRandom().nextDouble() - 0.5) * 0.2,
                arrow.getY() + (arrow.getRandom().nextDouble() - 0.5) * 0.2,
                arrow.getZ() + (arrow.getRandom().nextDouble() - 0.5) * 0.2,
                0.0, 0.0, 0.0);
        }
    }
}
