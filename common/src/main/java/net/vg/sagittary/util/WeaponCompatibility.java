package net.vg.sagittary.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

/** Small shared bow API boundaries; bow behavior stays in the item classes. */
public final class WeaponCompatibility {
    private WeaponCompatibility() {}

    public static int useDuration(ItemStack stack, LivingEntity shooter) {
        //? if >=1.21 {
        return stack.getUseDuration(shooter);
        //? } else {
        /*return stack.getUseDuration();
        *///? }
    }

    public static AbstractArrow createArrow(ArrowItem item, ServerLevel level, ItemStack ammo, Player shooter, ItemStack bow) {
        //? if >=1.21 {
        return item.createArrow(level, ammo, shooter, bow);
        //? } else {
        /*return item.createArrow(level, ammo, shooter);
        *///? }
    }

    public static boolean infiniteAmmo(ServerLevel level, ItemStack bow, ItemStack ammo, Player player) {
        if (player.getAbilities().instabuild) return true;
        //? if >=1.21 {
        return EnchantmentHelper.processAmmoUse(level, bow, ammo, 1) == 0;
        //? } else {
        /*return ammo.is(Items.ARROW) && EnchantmentHelper.getItemEnchantmentLevel(
                net.minecraft.world.item.enchantment.Enchantments.INFINITY_ARROWS, bow) > 0;
        *///? }
    }

    public static void damage(ItemStack weapon, Player player, EquipmentSlot slot) {
        //? if >=1.21 {
        weapon.hurtAndBreak(1, player, slot);
        //? } else {
        /*weapon.hurtAndBreak(1, player, owner -> owner.broadcastBreakEvent(slot));
        *///? }
    }
}
