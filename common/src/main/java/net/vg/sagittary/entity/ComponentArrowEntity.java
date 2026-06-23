package net.vg.sagittary.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.vg.sagittary.component.ArrowComponent;
import net.vg.sagittary.component.effects.ComponentEffect;
import net.vg.sagittary.item.ComponentArrowItem;
import net.vg.sagittary.registry.ObjectRegistry;

public class ComponentArrowEntity extends AbstractArrow {
    private static final EntityDataAccessor<String> TIP_COMPONENT = SynchedEntityData.defineId(ComponentArrowEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> SHAFT_COMPONENT = SynchedEntityData.defineId(ComponentArrowEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> FLETCHING_COMPONENT = SynchedEntityData.defineId(ComponentArrowEntity.class, EntityDataSerializers.STRING);

    private ArrowComponent tipComponent = ArrowComponent.FLINT_TIP;
    private ArrowComponent shaftComponent = ArrowComponent.STICK_SHAFT;
    private ArrowComponent fletchingComponent = ArrowComponent.FEATHER_FLETCHING;
    private ItemStack originalPickupStack = ItemStack.EMPTY;

    // Effect instances for each component
    private ComponentEffect tipEffect = ArrowComponent.FLINT_TIP.createEffect();
    private ComponentEffect shaftEffect = ArrowComponent.STICK_SHAFT.createEffect();
    private ComponentEffect fletchingEffect = ArrowComponent.FEATHER_FLETCHING.createEffect();

    public ComponentArrowEntity(EntityType<? extends ComponentArrowEntity> entityType, Level level) {
        super(entityType, level);
    }

    // Constructor for dispensers (no LivingEntity shooter)
    public ComponentArrowEntity(Level level, double x, double y, double z, ItemStack pickupItemStack, ItemStack firedFromWeapon) {
        super(ObjectRegistry.COMPONENT_ARROW_ENTITY.get(), x, y, z, level, pickupItemStack, firedFromWeapon);

        // Store the original ItemStack for component loading
        this.originalPickupStack = pickupItemStack.copy();

        // Force reload components from the correct ItemStack
        if (pickupItemStack.getItem() instanceof ComponentArrowItem) {
            loadComponentsFromStack(pickupItemStack);
        }
    }

    public ComponentArrowEntity(Level level, LivingEntity shooter, ItemStack pickupItemStack, ItemStack firedFromWeapon) {
        super(ObjectRegistry.COMPONENT_ARROW_ENTITY.get(), shooter, level, pickupItemStack, firedFromWeapon);
        this.originalPickupStack = pickupItemStack.copy();
        if (pickupItemStack.getItem() instanceof ComponentArrowItem) {
            loadComponentsFromStack(pickupItemStack);
        }
    }

    @Override
    protected void setPickupItemStack(ItemStack itemStack) {
        super.setPickupItemStack(itemStack);
        if (itemStack.getItem() instanceof ComponentArrowItem) {
            loadComponentsFromStack(itemStack);
        }
    }

    /**
     * Helper method to load components and effects from an ItemStack
     */
    private void loadComponentsFromStack(ItemStack itemStack) {
        this.tipComponent = ComponentArrowItem.getTipFromStack(itemStack);
        this.shaftComponent = ComponentArrowItem.getShaftFromStack(itemStack);
        this.fletchingComponent = ComponentArrowItem.getFletchingFromStack(itemStack);

        // Create effect instances
        this.tipEffect = this.tipComponent.createEffect();
        this.shaftEffect = this.shaftComponent.createEffect();
        this.fletchingEffect = this.fletchingComponent.createEffect();

        // Sync to client - store in local vars first to ensure proper ordering
        String tipName = this.tipComponent.getMaterialName();
        String shaftName = this.shaftComponent.getMaterialName();
        String fletchingName = this.fletchingComponent.getMaterialName();

        this.entityData.set(TIP_COMPONENT, tipName);
        this.entityData.set(SHAFT_COMPONENT, shaftName);
        this.entityData.set(FLETCHING_COMPONENT, fletchingName);

        // Force a read-back to ensure entityData is flushed/committed
        // (This appears to be necessary for proper client sync)
        this.entityData.get(TIP_COMPONENT);
        this.entityData.get(SHAFT_COMPONENT);
        this.entityData.get(FLETCHING_COMPONENT);

        // Apply initial modifiers from all effects
        this.tipEffect.applyInitialModifiers(this);
        this.shaftEffect.applyInitialModifiers(this);
        this.fletchingEffect.applyInitialModifiers(this);
    }

    private void ensureComponentStateLoaded() {
        if (this.level().isClientSide()) {
            // On client: sync effects from entity data (onSyncedDataUpdated may not fire for initial spawn)
            String syncedTip = this.entityData.get(TIP_COMPONENT);
            String syncedShaft = this.entityData.get(SHAFT_COMPONENT);
            String syncedFletching = this.entityData.get(FLETCHING_COMPONENT);

            // Check if our current components match the synced data
            if (!this.tipComponent.getMaterialName().equals(syncedTip) ||
                !this.shaftComponent.getMaterialName().equals(syncedShaft) ||
                !this.fletchingComponent.getMaterialName().equals(syncedFletching)) {

                // Update components and effects to match synced data
                this.tipComponent = ArrowComponent.getByMaterialAndType(syncedTip, ArrowComponent.ComponentType.TIP);
                this.shaftComponent = ArrowComponent.getByMaterialAndType(syncedShaft, ArrowComponent.ComponentType.SHAFT);
                this.fletchingComponent = ArrowComponent.getByMaterialAndType(syncedFletching, ArrowComponent.ComponentType.FLETCHING);
                this.tipEffect = this.tipComponent.createEffect();
                this.shaftEffect = this.shaftComponent.createEffect();
                this.fletchingEffect = this.fletchingComponent.createEffect();
            }
            return;
        }

        // Server-side: reload from ItemStack if it has valid component data
        ItemStack stack = this.getPickupItemStackOrigin();
        if (stack != null
                && !stack.isEmpty()
                && stack.getItem() instanceof ComponentArrowItem
                && stack.hasNonDefault(ObjectRegistry.ARROW_PARTS.get())) {
            this.tipComponent = ComponentArrowItem.getTipFromStack(stack);
            this.shaftComponent = ComponentArrowItem.getShaftFromStack(stack);
            this.fletchingComponent = ComponentArrowItem.getFletchingFromStack(stack);
            this.tipEffect = this.tipComponent.createEffect();
            this.shaftEffect = this.shaftComponent.createEffect();
            this.fletchingEffect = this.fletchingComponent.createEffect();
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TIP_COMPONENT, ArrowComponent.FLINT_TIP.getMaterialName());
        builder.define(SHAFT_COMPONENT, ArrowComponent.STICK_SHAFT.getMaterialName());
        builder.define(FLETCHING_COMPONENT, ArrowComponent.FEATHER_FLETCHING.getMaterialName());
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);

        // Update client-side components when data syncs
        if (key.equals(TIP_COMPONENT) || key.equals(SHAFT_COMPONENT) || key.equals(FLETCHING_COMPONENT)) {
            String tipName = this.entityData.get(TIP_COMPONENT);
            String shaftName = this.entityData.get(SHAFT_COMPONENT);
            String fletchingName = this.entityData.get(FLETCHING_COMPONENT);

            this.tipComponent = ArrowComponent.getByMaterialAndType(tipName, ArrowComponent.ComponentType.TIP);
            this.shaftComponent = ArrowComponent.getByMaterialAndType(shaftName, ArrowComponent.ComponentType.SHAFT);
            this.fletchingComponent = ArrowComponent.getByMaterialAndType(fletchingName, ArrowComponent.ComponentType.FLETCHING);

            // Keep the client-side effect instances aligned with synced components.
            // Gravity, particles, and other per-tick behavior read from these effects.
            this.tipEffect = this.tipComponent.createEffect();
            this.shaftEffect = this.shaftComponent.createEffect();
            this.fletchingEffect = this.fletchingComponent.createEffect();
        }
    }

    private void applyComponentEffects() {
        // Apply damage and speed modifiers from all components
        float totalDamageModifier = tipComponent.getDamageModifier() * shaftComponent.getDamageModifier() * fletchingComponent.getDamageModifier();

        // Modify base damage (default is 2.0)
        this.setBaseDamage(2.0 * totalDamageModifier);

        // Apply initial modifiers from all effects (these handle speed and other properties)
        this.tipEffect.applyInitialModifiers(this);
        this.shaftEffect.applyInitialModifiers(this);
        this.fletchingEffect.applyInitialModifiers(this);
    }

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        ensureComponentStateLoaded();
        super.shoot(x, y, z, velocity, inaccuracy);

        // Apply speed modifiers from all effects
        double totalSpeedModifier = tipEffect.getSpeedModifier(this) *
                                   shaftEffect.getSpeedModifier(this) *
                                   fletchingEffect.getSpeedModifier(this);
        this.setDeltaMovement(this.getDeltaMovement().scale(totalSpeedModifier));
    }

    @Override
    public void tick() {
        // Skip all custom logic when arrow is in ground to not interfere with settling
        if (this.isInGround()) {
            super.tick();
            return;
        }

        ensureComponentStateLoaded();
        super.tick();

        // Only run effects when arrow is still in flight
        if (!this.isInGround()) {
            this.tipEffect.onTick(this);
            this.shaftEffect.onTick(this);
            this.fletchingEffect.onTick(this);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        ensureComponentStateLoaded();
        super.onHitEntity(entityHitResult);

        // Apply entity hit effects from all components
        this.tipEffect.onEntityHit(entityHitResult, this);
        this.shaftEffect.onEntityHit(entityHitResult, this);
        this.fletchingEffect.onEntityHit(entityHitResult, this);
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        // Call super first to let vanilla settling logic run
        super.onHitBlock(blockHitResult);

        // Apply block hit effects from all components (these should not interfere with settling)
        this.tipEffect.onBlockHit(blockHitResult, this);
        this.shaftEffect.onBlockHit(blockHitResult, this);
        this.fletchingEffect.onBlockHit(blockHitResult, this);
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        // During initialization, effects might not be ready yet
        if (this.tipEffect != null && this.shaftEffect != null && this.fletchingEffect != null) {
            // Check if any effect prevents pickup
            if (this.tipEffect.shouldPreventPickup(this) ||
                this.shaftEffect.shouldPreventPickup(this) ||
                this.fletchingEffect.shouldPreventPickup(this)) {
                return ItemStack.EMPTY;
            }
        }

        // If we have the original ItemStack, return it to preserve NBT
        if (originalPickupStack != null && !originalPickupStack.isEmpty() && originalPickupStack.getItem() instanceof ComponentArrowItem) {
            return originalPickupStack.copy();
        }

        // Fallback: Return component arrow with the same components
        // Handle case where components might not be initialized yet
        ArrowComponent tip = tipComponent != null ? tipComponent : ArrowComponent.FLINT_TIP;
        ArrowComponent shaft = shaftComponent != null ? shaftComponent : ArrowComponent.STICK_SHAFT;
        ArrowComponent fletching = fletchingComponent != null ? fletchingComponent : ArrowComponent.FEATHER_FLETCHING;

        return ComponentArrowItem.createComponentArrow(tip, shaft, fletching);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("TipComponent", this.tipComponent.getMaterialName());
        compound.putString("ShaftComponent", this.shaftComponent.getMaterialName());
        compound.putString("FletchingComponent", this.fletchingComponent.getMaterialName());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput compound) {
        super.readAdditionalSaveData(compound);

        compound.getString("TipComponent").ifPresent(tipName -> {
            this.tipComponent = ArrowComponent.getByMaterialAndType(tipName, ArrowComponent.ComponentType.TIP);
            this.entityData.set(TIP_COMPONENT, tipName);
        });

        compound.getString("ShaftComponent").ifPresent(shaftName -> {
            this.shaftComponent = ArrowComponent.getByMaterialAndType(shaftName, ArrowComponent.ComponentType.SHAFT);
            this.entityData.set(SHAFT_COMPONENT, shaftName);
        });

        compound.getString("FletchingComponent").ifPresent(fletchingName -> {
            this.fletchingComponent = ArrowComponent.getByMaterialAndType(fletchingName, ArrowComponent.ComponentType.FLETCHING);
            this.entityData.set(FLETCHING_COMPONENT, fletchingName);
        });

        // Recreate effect instances after loading
        this.tipEffect = this.tipComponent.createEffect();
        this.shaftEffect = this.shaftComponent.createEffect();
        this.fletchingEffect = this.fletchingComponent.createEffect();

        // Reapply effects after loading
        applyComponentEffects();
    }

    // Getters for components (useful for rendering or other systems)
    public ArrowComponent getTipComponent() { return tipComponent; }
    public ArrowComponent getShaftComponent() { return shaftComponent; }
    public ArrowComponent getFletchingComponent() { return fletchingComponent; }

    // Public accessor for protected isInGround() method (for effects to use)
    public boolean isArrowInGround() {
        return this.isInGround();
    }

    // Public wrapper for SlimeTipEffect bouncing
    public void setArrowInGround(boolean inGround) {
        this.setInGround(inGround);
    }

    @Override
    protected double getDefaultGravity() {
        // Skip gravity modifiers when in ground - use vanilla gravity for proper settling
        if (this.isInGround()) {
            return super.getDefaultGravity();
        }

        ensureComponentStateLoaded();
        double baseGravity = super.getDefaultGravity();
        double totalGravityModifier = tipEffect.getGravityModifier(this) *
                                     shaftEffect.getGravityModifier(this) *
                                     fletchingEffect.getGravityModifier(this);
        return baseGravity * totalGravityModifier;
    }
}
