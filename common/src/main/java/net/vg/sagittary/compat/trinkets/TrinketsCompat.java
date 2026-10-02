package net.vg.sagittary.compat.trinkets;

import dev.architectury.platform.Platform;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.vg.sagittary.item.QuiverItem;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Optional bridges: eu.pb4 Updated on 26.x; dev.emi original on legacy Fabric. */
public final class TrinketsCompat {
    private TrinketsCompat() {}
    private static final Logger LOGGER = LoggerFactory.getLogger("SagittaryTrinkets");
    private static boolean warned;
    private static Method getComponent;
    private static Method getInventory;

    /**
     * Adds Trinkets Updated's EQUIPMENT component when that optional mod is present.
     * Reflection keeps the dedicated server and installations without Trinkets loadable.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Item.Properties makeBackEquippable(Item.Properties properties) {
        // Original Trinkets uses the chest/back item tag, not this component.
        //? if >=26.1 {
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
        } catch (ReflectiveOperationException | LinkageError | RuntimeException failure) {
            warn("trinkets_updated", failure);
        }
        //? }
        return properties;
    }

    public static ItemStack getEquippedBackQuiver(Player player) {
        //? if >=26.1 {
        if (!Platform.isModLoaded("trinkets_updated")) return ItemStack.EMPTY;
        try {
            if (getComponent == null || getInventory == null) {
                getComponent = Class.forName("eu.pb4.trinkets.api.TrinketsApi").getMethod("getAttachment", LivingEntity.class);
                // Use the public interface, not an implementation's accessibility.
                getInventory = Class.forName("eu.pb4.trinkets.api.TrinketAttachment").getMethod("getInventory", String.class);
            }
            Object attachment = getComponent.invoke(null, player);
            if (attachment == null) return ItemStack.EMPTY;
            return findQuiver((Container) getInventory.invoke(attachment, "chest/back"));
        } catch (ReflectiveOperationException | LinkageError | RuntimeException failure) {
            warn("trinkets_updated", failure);
        }
        //? } else {
        /*if (!Platform.isFabric() || !Platform.isModLoaded("trinkets")) return ItemStack.EMPTY;
        try {
            if (getComponent == null || getInventory == null) {
                getComponent = Class.forName("dev.emi.trinkets.api.TrinketsApi").getMethod("getTrinketComponent", LivingEntity.class);
                getInventory = Class.forName("dev.emi.trinkets.api.TrinketComponent").getMethod("getInventory");
            }
            Optional<?> component = (Optional<?>) getComponent.invoke(null, player);
            if (component.isEmpty()) return ItemStack.EMPTY;
            Map<?, ?> groups = (Map<?, ?>) getInventory.invoke(component.get());
            Object chest = groups.get("chest");
            return chest instanceof Map<?, ?> slots ? findQuiver((Container) slots.get("back")) : ItemStack.EMPTY;
        } catch (ReflectiveOperationException | LinkageError | RuntimeException failure) {
            warn("trinkets", failure);
        }
        *///? }
        return ItemStack.EMPTY;
    }

    private static ItemStack findQuiver(Container inventory) {
        if (inventory != null) {
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                ItemStack stack = inventory.getItem(i);
                if (stack.getItem() instanceof QuiverItem) return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    private static void warn(String mod, Throwable failure) {
        if (warned) return;
        warned = true;
        LOGGER.warn("{} is installed but Sagittary's optional quiver bridge could not access its API. "
                + "Accessory support is unavailable; inventory quivers still work. Check compatible mod versions.", mod, failure);
    }
}
