package net.vg.sagittary.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.level.Level;
import net.minecraft.core.component.DataComponents;

import java.util.List;

/**
 * Iron Crossbow - A more durable crossbow with increased projectile range.
 * Durability: 652 (1.5x regular crossbow's 465)
 * Range bonus: Increased projectile range
 * Supports quiver integration.
 */
public class IronCrossbowItem extends CrossbowItem {
    private boolean loadSoundPlayed = false;

    public IronCrossbowItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getDefaultProjectileRange() {
        return 12; // Higher than regular crossbow (8)
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (isCharged(stack)) {
            // Fire the loaded arrow
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
        if (charged != null && charged.contains(net.minecraft.world.item.Items.FIREWORK_ROCKET)) {
            return 1.6F;
        }
        return 3.15F;
    }

    @Override
    public void onUseTick(Level level, LivingEntity shooter, ItemStack stack, int remainingTicks) {
        if (level.isClientSide() || !(shooter instanceof Player player)) {
            return;
        }

        int useDuration = this.getUseDuration(stack, shooter) - remainingTicks;
        float chargeProgress = (float) useDuration / (float) getChargeDuration(stack, shooter);

        // Play charging sounds
        if (chargeProgress < 0.2F) {
            loadSoundPlayed = false;
        }

        // When fully charged and not already loaded, load an arrow
        if (chargeProgress >= 1.0F && !isCharged(stack)) {
            if (tryLoadProjectile(player, stack)) {
                level.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(),
                        SoundEvents.CROSSBOW_LOADING_END, SoundSource.PLAYERS, 1.0F, 1.0F);
                loadSoundPlayed = true;
            }
        } else if (chargeProgress >= 0.5F && !loadSoundPlayed) {
            level.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(),
                    SoundEvents.CROSSBOW_LOADING_MIDDLE, SoundSource.PLAYERS, 1.0F, 1.0F);
        } else if (chargeProgress >= 0.2F && chargeProgress < 0.5F && !loadSoundPlayed) {
            level.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(),
                    SoundEvents.CROSSBOW_LOADING_START, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }

    /**
     * Load a single arrow from quiver or inventory.
     */
    private boolean tryLoadProjectile(Player player, ItemStack crossbow) {
        boolean hasInfiniteMaterials = player.hasInfiniteMaterials();

        // Check quiver first, then inventory
        ItemStack projectile = QuiverItem.getArrowFromInventory(player, crossbow);
        if (projectile.isEmpty()) {
            return false;
        }

        // Get one arrow
        ItemStack singleArrow = projectile.copyWithCount(1);

        // Consume the arrow (unless in creative)
        if (!hasInfiniteMaterials) {
            QuiverItem.consumeArrowFromInventory(player, crossbow);
        }

        crossbow.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.ofNonEmpty(List.of(singleArrow)));
        return true;
    }
}
