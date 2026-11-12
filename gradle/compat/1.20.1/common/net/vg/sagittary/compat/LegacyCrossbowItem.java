package net.vg.sagittary.compat;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.vg.sagittary.item.QuiverItem;

import java.util.ArrayList;
import java.util.List;

/** Vanilla's NBT charge format, with Sagittary's one-round / five-round magazines. */
public class LegacyCrossbowItem extends CrossbowItem {
    private final int magazineSize;
    private final int range;
    private boolean startSound;
    private boolean middleSound;

    protected LegacyCrossbowItem(Properties properties, int magazineSize, int range) {
        super(properties);
        this.magazineSize = magazineSize;
        this.range = range;
    }

    public static List<ItemStack> projectiles(ItemStack stack) {
        List<ItemStack> result = new ArrayList<>();
        if (stack.hasTag()) {
            ListTag data = stack.getTag().getList("ChargedProjectiles", Tag.TAG_COMPOUND);
            for (int i = 0; i < data.size(); i++) {
                ItemStack arrow = ItemStack.of(data.getCompound(i));
                if (!arrow.isEmpty()) result.add(arrow);
            }
        }
        return result;
    }

    private static void write(ItemStack crossbow, List<ItemStack> arrows) {
        ListTag data = new ListTag();
        for (ItemStack arrow : arrows) data.add(arrow.save(new CompoundTag()));
        crossbow.getOrCreateTag().put("ChargedProjectiles", data);
        setCharged(crossbow, !arrows.isEmpty());
    }

    @Override public int getDefaultProjectileRange() { return range; }

    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack crossbow = player.getItemInHand(hand);
        if (!projectiles(crossbow).isEmpty()) {
            if (!level.isClientSide) shootNext(level, player, hand, crossbow);
            return InteractionResultHolder.consume(crossbow);
        }
        if (QuiverItem.getArrowFromInventory(player, crossbow).isEmpty()) return InteractionResultHolder.fail(crossbow);
        startSound = middleSound = false;
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(crossbow);
    }

    @Override public void onUseTick(Level level, LivingEntity shooter, ItemStack crossbow, int remainingTicks) {
        if (level.isClientSide || !(shooter instanceof Player player)) return;
        float progress = (float) (getUseDuration(crossbow) - remainingTicks) / getChargeDuration(crossbow);
        if (progress >= 1 && !isCharged(crossbow)) {
            List<ItemStack> arrows = new ArrayList<>();
            for (int i = 0; i < magazineSize; i++) {
                ItemStack arrow = QuiverItem.getArrowFromInventory(player, crossbow);
                if (arrow.isEmpty()) break;
                arrows.add(arrow.copyWithCount(1));
                if (!player.getAbilities().instabuild) QuiverItem.consumeArrowFromInventory(player, crossbow);
            }
            if (!arrows.isEmpty()) {
                write(crossbow, arrows);
                level.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.CROSSBOW_LOADING_END, SoundSource.PLAYERS, 1, 1);
            }
        } else if (progress >= .5f && !middleSound) {
            middleSound = true;
            level.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.CROSSBOW_LOADING_MIDDLE, SoundSource.PLAYERS, 1, 1);
        } else if (progress >= .2f && !startSound) {
            startSound = true;
            level.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.CROSSBOW_LOADING_START, SoundSource.PLAYERS, 1, 1);
        }
    }

    // Charging happens in onUseTick; vanilla releaseUsing must not consume a second magazine.
    @Override public void releaseUsing(ItemStack stack, Level level, LivingEntity shooter, int timeLeft) {}

    private void shootNext(Level level, Player player, InteractionHand hand, ItemStack crossbow) {
        List<ItemStack> arrows = projectiles(crossbow);
        ItemStack ammo = arrows.remove(0);
        ArrowItem item = ammo.getItem() instanceof ArrowItem arrowItem ? arrowItem : (ArrowItem) Items.ARROW;
        int shots = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MULTISHOT, crossbow) > 0 ? 3 : 1;
        for (int i = 0; i < shots; i++) {
            boolean rocket = ammo.is(Items.FIREWORK_ROCKET);
            Projectile projectile;
            if (rocket) {
                projectile = new FireworkRocketEntity(level, ammo.copyWithCount(1), player, player.getX(), player.getEyeY() - .15, player.getZ(), true);
            } else {
                AbstractArrow arrow = item.createArrow(level, ammo.copyWithCount(1), player);
                arrow.setShotFromCrossbow(true);
                arrow.setCritArrow(true);
                arrow.setSoundEvent(SoundEvents.CROSSBOW_HIT);
                int piercing = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PIERCING, crossbow);
                if (piercing > 0) arrow.setPierceLevel((byte) piercing);
                arrow.pickup = player.getAbilities().instabuild || i > 0 ? AbstractArrow.Pickup.CREATIVE_ONLY : AbstractArrow.Pickup.ALLOWED;
                projectile = arrow;
            }
            float angle = i == 1 ? -10 : i == 2 ? 10 : 0;
            var up = player.getUpVector(1);
            var rotation = new org.joml.Quaternionf().setAngleAxis(angle * .017453292f, up.x, up.y, up.z);
            var direction = player.getViewVector(1).toVector3f().rotate(rotation);
            projectile.shoot(direction.x(), direction.y(), direction.z(), rocket ? 1.6f : 3.15f, 1);
            if (magazineSize == 1) crossbow.hurtAndBreak(rocket ? 3 : 1, player, owner -> owner.broadcastBreakEvent(hand));
            level.addFreshEntity(projectile);
        }
        write(crossbow, arrows);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1, 1);
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.awardStat(Stats.ITEM_USED.get(this));
            if (magazineSize == 1) net.minecraft.advancements.CriteriaTriggers.SHOT_CROSSBOW.trigger(serverPlayer, crossbow);
        }
    }
}
