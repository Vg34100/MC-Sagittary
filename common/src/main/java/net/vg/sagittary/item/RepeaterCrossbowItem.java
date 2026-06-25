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
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.core.component.DataComponents;
import org.joml.Vector3f;

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
        if (!(shooter instanceof Player player)) {
            return false;
        }

        List<ItemStack> projectilesToLoad = new ArrayList<>();
        boolean hasInfiniteMaterials = player.hasInfiniteMaterials();

        for (int i = 0; i < MAGAZINE_SIZE; i++) {
            // Check quiver first, then inventory
            ItemStack projectile = QuiverItem.getArrowFromInventory(player, crossbow);
            if (projectile.isEmpty()) {
                break;
            }

            // Get one arrow
            ItemStack singleArrow = projectile.copyWithCount(1);
            projectilesToLoad.add(singleArrow);

            // Consume the arrow (unless in creative)
            if (!hasInfiniteMaterials) {
                QuiverItem.consumeArrowFromInventory(player, crossbow);
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
            // Check if player has arrows to load (quiver or inventory)
            if (!QuiverItem.getArrowFromInventory(player, stack).isEmpty()) {
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

        // Get projectile count from multishot enchantment (1 base + enchantment bonus)
        int projectileCount = EnchantmentHelper.processProjectileCount(serverLevel, crossbow, shooter, 1);
        // Get spread angle from multishot enchantment
        float spreadAngle = EnchantmentHelper.processProjectileSpread(serverLevel, crossbow, shooter, 0.0F);

        boolean isPlayer = shooter instanceof Player;
        int shotsFired = 0;

        // Take only ONE projectile from the magazine - multishot creates copies
        ItemStack projectileToShoot = projectiles.remove(0);

        for (int i = 0; i < projectileCount; i++) {
            // Calculate angle offset for spread
            float angleOffset = 0.0F;
            if (projectileCount > 1 && spreadAngle > 0) {
                // Distribute shots evenly across the spread angle
                angleOffset = spreadAngle * ((float) i / (projectileCount - 1) - 0.5F);
            }

            // Create and shoot the projectile
            // Only first projectile (center) can be picked up, others are copies from multishot
            boolean isCopy = !isPlayer || i > 0;
            Projectile projectile = this.createProjectile(serverLevel, shooter, crossbow, projectileToShoot.copy(), isCopy);

            if (projectile != null) {
                // Explicitly set pickup status - only center arrow (i == 0) for players can be picked up
                if (projectile instanceof AbstractArrow arrow) {
                    if (isCopy) {
                        arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                    } else {
                        arrow.pickup = AbstractArrow.Pickup.ALLOWED;
                    }
                }

                this.shootProjectile(shooter, projectile, i, velocity, inaccuracy, angleOffset, target);
                serverLevel.addFreshEntity(projectile);
                shotsFired++;
            }
        }

        // Update the crossbow with remaining projectiles
        if (projectiles.isEmpty()) {
            crossbow.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
        } else {
            crossbow.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.ofNonEmpty(projectiles));
        }

        // Play sound
        if (shotsFired > 0) {
            level.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(),
                    SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);
        }

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
