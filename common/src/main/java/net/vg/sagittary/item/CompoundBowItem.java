package net.vg.sagittary.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Compound Bow - Shoots 3 arrows in a spread pattern.
 * Consumes 3 arrows per shot (or 1 with Infinity).
 * Higher durability and slightly slower draw speed.
 */
public class CompoundBowItem extends BowItem {
    private static final float SPREAD_ANGLE = 10.0f; // Degrees between arrows

    public CompoundBowItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player)) {
            return false;
        }

        // Check for arrows - quiver first, then inventory
        ItemStack arrowStack = QuiverItem.getArrowFromInventory(player, stack);
        if (arrowStack.isEmpty()) {
            return false;
        }

        int useDuration = this.getUseDuration(stack, entity) - timeLeft;
        float power = getPowerForTime(useDuration);
        if (power < 0.1F) {
            return false;
        }

        boolean hasInfinity = player.hasInfiniteMaterials();
        int arrowsToShoot = Math.min(3, hasInfinity ? 3 : arrowStack.getCount());

        if (arrowsToShoot <= 0) {
            return false;
        }

        if (level instanceof ServerLevel serverLevel) {
            ArrowItem arrowItem = arrowStack.getItem() instanceof ArrowItem ai ? ai : (ArrowItem) Items.ARROW;

            // Shoot center arrow
            shootArrow(serverLevel, player, stack, arrowStack, arrowItem, power, 0, hasInfinity);

            // Shoot side arrows if we have enough
            if (arrowsToShoot >= 2) {
                shootArrow(serverLevel, player, stack, arrowStack, arrowItem, power * 0.9f, -SPREAD_ANGLE, hasInfinity);
            }
            if (arrowsToShoot >= 3) {
                shootArrow(serverLevel, player, stack, arrowStack, arrowItem, power * 0.9f, SPREAD_ANGLE, hasInfinity);
            }

            // Consume arrows from quiver or inventory
            if (!hasInfinity) {
                for (int i = 0; i < arrowsToShoot; i++) {
                    QuiverItem.consumeArrowFromInventory(player, stack);
                }
            }

            // Damage the bow
            EquipmentSlot slot = entity.getUsedItemHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
            stack.hurtAndBreak(1, player, slot);
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS,
                1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + power * 0.5F);

        player.awardStat(Stats.ITEM_USED.get(this));
        return true;
    }

    private void shootArrow(ServerLevel level, Player player, ItemStack bow, ItemStack arrowStack,
                           ArrowItem arrowItem, float power, float angleOffset, boolean hasInfinity) {
        // Pass single-item copy to prevent pickup duplication
        ItemStack singleArrow = arrowStack.copyWithCount(1);
        AbstractArrow arrow = arrowItem.createArrow(level, singleArrow, player, bow);

        arrow.shootFromRotation(player, player.getXRot(), player.getYRot() + angleOffset,
                0.0F, power * 3.0F, 1.0F);

        if (power == 1.0F) {
            arrow.setCritArrow(true);
        }

        if (hasInfinity || angleOffset != 0) {
            // Side arrows and infinity arrows can't be picked up
            arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
        }

        level.addFreshEntity(arrow);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000; // Same as regular bow
    }

    @Override
    public int getDefaultProjectileRange() {
        return 15;
    }
}
