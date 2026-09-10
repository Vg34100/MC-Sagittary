package net.vg.sagittary.compat.trinkets;

import dev.architectury.platform.Platform;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.vg.sagittary.item.QuiverItem;

import java.lang.reflect.Method;

/** Optional, reflection-backed Trinkets Updated bridge. Keeps Sagittary loadable without Trinkets. */
public final class TrinketsCompat {
    private TrinketsCompat() {}

    /**
     * Adds Trinkets Updated's EQUIPMENT component when that optional mod is present.
     * Reflection keeps the dedicated server and installations without Trinkets loadable.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Item.Properties makeBackEquippable(Item.Properties properties) {
        if (!Platform.isModLoaded("trinkets_updated")) return properties;
        try {
            Class<?> components = Class.forName("eu.pb4.trinkets.api.component.TrinketDataComponents");
            Object equipmentType = components.getField("EQUIPMENT").get(null);
            Class<?> equippable = Class.forName("eu.pb4.trinkets.api.component.TrinketEquippable");
            Object defaults = equippable.getField("DEFAULT").get(null);
            String chestBack = (String) Class.forName("eu.pb4.trinkets.api.DefaultTrinketSlots")
                    .getField("CHEST_BACK").get(null);
            Object configured = equippable.getMethod("withSlots", String[].class)
                    .invoke(defaults, (Object) new String[]{chestBack});
            return properties.component((net.minecraft.core.component.DataComponentType) equipmentType, configured);
        } catch (ReflectiveOperationException ignored) {
            return properties;
        }
    }

    public static ItemStack getEquippedBackQuiver(Player player) {
        if (!Platform.isModLoaded("trinkets_updated")) return ItemStack.EMPTY;
        try {
            Class<?> api = Class.forName("eu.pb4.trinkets.api.TrinketsApi");
            Method getAttachment = api.getMethod("getAttachment", net.minecraft.world.entity.LivingEntity.class);
            Object attachment = getAttachment.invoke(null, player);
            if (attachment == null) return ItemStack.EMPTY;
            Method getSlotAccess = attachment.getClass().getMethod("getSlotAccess", String.class, int.class);
            Object access = getSlotAccess.invoke(attachment, "chest/back", 0);
            if (access == null) return ItemStack.EMPTY;
            Method get = access.getClass().getMethod("get");
            Object stack = get.invoke(access);
            return stack instanceof ItemStack itemStack && itemStack.getItem() instanceof QuiverItem ? itemStack : ItemStack.EMPTY;
        } catch (ReflectiveOperationException ignored) {
            return ItemStack.EMPTY;
        }
    }
}
