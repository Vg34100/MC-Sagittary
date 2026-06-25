package net.vg.sagittary.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.level.Level;
import net.minecraft.core.component.DataComponents;

import java.util.ArrayList;
import java.util.List;

/**
 * Repeater Crossbow - Magazine-fed crossbow that loads 5 arrows and fires them rapidly.
 * - Charge: Loads up to 5 arrows (consumes from inventory)
 * - Shoot: Fires 1 arrow per click with minimal delay
 * - After all arrows are fired, must reload
 */
public class RepeaterCrossbowItem extends CrossbowItem {
    public static final int MAGAZINE_SIZE = 5;
    private boolean loadSoundPlayed = false;

    public RepeaterCrossbowItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getDefaultProjectileRange() {
        return 8;
    }

    @Override
    public void onUseTick(Level level, LivingEntity shooter, ItemStack stack, int remainingTicks) {
        if (level.isClientSide()) {
            return;
        }

        int useDuration = this.getUseDuration(stack, shooter) - remainingTicks;
        float chargeProgress = (float) useDuration / (float) getChargeDuration(stack, shooter);

        // Play charging sounds at appropriate times
        if (chargeProgress < 0.2F) {
            loadSoundPlayed = false;
        }

        // When fully charged and not already loaded, load the magazine
        if (chargeProgress >= 1.0F && !isCharged(stack)) {
            if (tryLoadMagazine(shooter, stack)) {
                // Play load complete sound
                level.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(),
                        SoundEvents.CROSSBOW_LOADING_END, SoundSource.PLAYERS, 1.0F, 1.0F);
                loadSoundPlayed = true;
            }
        } else if (chargeProgress >= 0.5F && !loadSoundPlayed) {
            // Play mid-load sound
            level.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(),
                    SoundEvents.CROSSBOW_LOADING_MIDDLE, SoundSource.PLAYERS, 1.0F, 1.0F);
        } else if (chargeProgress >= 0.2F && chargeProgress < 0.5F) {
            // Play start sound once
            if (!loadSoundPlayed) {
                level.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(),
                        SoundEvents.CROSSBOW_LOADING_START, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        }
    }

    /**
     * Load up to MAGAZINE_SIZE arrows into the crossbow.
     */
    private boolean tryLoadMagazine(LivingEntity shooter, ItemStack crossbow) {
        List<ItemStack> projectilesToLoad = new ArrayList<>();
        boolean hasInfiniteMaterials = shooter instanceof Player player && player.hasInfiniteMaterials();

        for (int i = 0; i < MAGAZINE_SIZE; i++) {
            ItemStack projectile = shooter.getProjectile(crossbow);
            if (projectile.isEmpty()) {
                break;
            }

            // Get one arrow from the stack
            ItemStack singleArrow = projectile.copyWithCount(1);
            projectilesToLoad.add(singleArrow);

            // Consume the arrow (unless in creative)
            if (!hasInfiniteMaterials) {
                projectile.shrink(1);
            }
        }

        if (!projectilesToLoad.isEmpty()) {
            crossbow.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.ofNonEmpty(projectilesToLoad));
            return true;
        }
        return false;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (isCharged(stack)) {
            // Fire one arrow from the magazine
            performShooting(level, player, hand, stack, getShootingPower(stack), 1.0F, null);
            return InteractionResult.CONSUME;
        } else {
            // Check if player has arrows to load
            if (!player.getProjectile(stack).isEmpty()) {
                loadSoundPlayed = false;
                player.startUsingItem(hand);
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.FAIL;
    }

    private static float getShootingPower(ItemStack stack) {
        ChargedProjectiles charged = stack.get(DataComponents.CHARGED_PROJECTILES);
        if (charged != null && charged.contains(Items.FIREWORK_ROCKET)) {
            return 1.6F;
        }
        return 3.15F;
    }

    @Override
    public void performShooting(Level level, LivingEntity shooter, InteractionHand hand, ItemStack crossbow, float velocity, float inaccuracy, LivingEntity target) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        ChargedProjectiles chargedProjectiles = crossbow.get(DataComponents.CHARGED_PROJECTILES);
        if (chargedProjectiles == null || chargedProjectiles.isEmpty()) {
            return;
        }

        // Get all loaded projectiles
        List<ItemStack> projectiles = new ArrayList<>(chargedProjectiles.itemCopies());
        if (projectiles.isEmpty()) {
            return;
        }

        // Take the first projectile and shoot it
        ItemStack projectileToShoot = projectiles.remove(0);

        // Update the crossbow with remaining projectiles
        if (projectiles.isEmpty()) {
            crossbow.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
        } else {
            crossbow.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.ofNonEmpty(projectiles));
        }

        // Create and shoot the projectile
        boolean isPlayer = shooter instanceof Player;
        Projectile projectile = this.createProjectile(serverLevel, shooter, crossbow, projectileToShoot, !isPlayer);

        if (projectile != null) {
            this.shootProjectile(shooter, projectile, 0, velocity, inaccuracy, 0, target);
            serverLevel.addFreshEntity(projectile);
        }

        // Play sound
        level.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(),
                SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);

        // Stats and advancement
        if (shooter instanceof ServerPlayer serverPlayer) {
            serverPlayer.awardStat(Stats.ITEM_USED.get(this));
        }
    }

    /**
     * Get the number of arrows remaining in the magazine.
     */
    public static int getRemainingShots(ItemStack stack) {
        ChargedProjectiles charged = stack.get(DataComponents.CHARGED_PROJECTILES);
        if (charged == null || charged.isEmpty()) {
            return 0;
        }
        return charged.itemCopies().size();
    }
}
