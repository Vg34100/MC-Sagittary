package net.vg.sagittary.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

/**
 * Iron Bow - A more durable bow with slightly increased damage.
 * Durability: 576 (1.5x regular bow's 384)
 * Damage bonus: +10% arrow velocity
 * Supports quiver integration.
 */
public class IronBowItem extends BowItem {

    public IronBowItem(Properties properties) {
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

        if (level instanceof ServerLevel serverLevel) {
            ArrowItem arrowItem = arrowStack.getItem() instanceof ArrowItem ai ? ai : (ArrowItem) Items.ARROW;
            // Pass single-item copy to prevent pickup duplication
            ItemStack singleArrow = arrowStack.copyWithCount(1);
            AbstractArrow arrow = arrowItem.createArrow(serverLevel, singleArrow, player, stack);

            // 10% faster arrow velocity for iron bow
            arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, power * 3.3F, 1.0F);

            if (power == 1.0F) {
                arrow.setCritArrow(true);
            }

            // Check infinity enchantment - processAmmoUse returns 0 if infinity applies
            int ammoToConsume = EnchantmentHelper.processAmmoUse(serverLevel, stack, singleArrow, 1);
            boolean hasInfinity = player.hasInfiniteMaterials() || ammoToConsume == 0;

            // Consume arrow from quiver or inventory
            if (!hasInfinity) {
                QuiverItem.consumeArrowFromInventory(player, stack);
                arrow.pickup = AbstractArrow.Pickup.ALLOWED;
            } else {
                arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            }

            serverLevel.addFreshEntity(arrow);

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

    @Override
    public int getDefaultProjectileRange() {
        return 18; // Slightly higher than regular bow (15)
    }
}
