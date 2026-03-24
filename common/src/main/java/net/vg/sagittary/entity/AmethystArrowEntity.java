package net.vg.sagittary.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.vg.sagittary.registry.ObjectRegistry;
import org.jetbrains.annotations.Nullable;

public class AmethystArrowEntity extends AbstractArrow {

    public AmethystArrowEntity(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    public AmethystArrowEntity(Level level, LivingEntity shooter, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
        super(ObjectRegistry.AMETHYST_ARROW_ENTITY.get(), shooter, level, pickupItemStack, firedFromWeapon);
    }

    public AmethystArrowEntity(Level level, double x, double y, double z, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
        super(ObjectRegistry.AMETHYST_ARROW_ENTITY.get(), x, y, z, level, pickupItemStack, firedFromWeapon);
    }

    @Override
    public void tick() {
        super.tick();
        
        if (this.level().isClientSide && !this.inGround) {
            double d0 = this.getX() + (this.random.nextDouble() - 0.5) * 0.5;
            double d1 = this.getY() + (this.random.nextDouble() - 0.5) * 0.5;
            double d2 = this.getZ() + (this.random.nextDouble() - 0.5) * 0.5;
            this.level().addParticle(ParticleTypes.END_ROD, d0, d1, d2, 0.0, 0.0, 0.0);
            
            if (this.random.nextFloat() < 0.3f) {
                this.level().addParticle(ParticleTypes.ENCHANT, d0, d1, d2, 0.0, 0.1, 0.0);
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        
        if (result.getEntity() instanceof LivingEntity livingEntity) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 255, false, false, false));
            
            if (this.level().isClientSide) {
                for (int i = 0; i < 10; i++) {
                    double d0 = livingEntity.getX() + (this.random.nextDouble() - 0.5) * 2.0;
                    double d1 = livingEntity.getY() + this.random.nextDouble() * 2.0;
                    double d2 = livingEntity.getZ() + (this.random.nextDouble() - 0.5) * 2.0;
                    this.level().addParticle(ParticleTypes.END_ROD, d0, d1, d2, 0.0, 0.0, 0.0);
                }
            }
        }
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(ObjectRegistry.AMETHYST_ARROW_ITEM.get());
    }
}
