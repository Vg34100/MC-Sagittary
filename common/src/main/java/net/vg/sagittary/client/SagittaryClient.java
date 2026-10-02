package net.vg.sagittary.client;

import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.client.gui.ClientTooltipComponentRegistry;
//? if >=26.1 {
import dev.architectury.registry.client.gui.MenuScreenRegistry;
//? } else {
/*import dev.architectury.registry.menu.MenuRegistry;
*///? }
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientRawInputEvent;
import dev.architectury.event.EventResult;
import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.inventory.Slot;
import net.vg.sagittary.mixin.client.ContainerScreenAccessor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.vg.sagittary.entity.ComponentArrowEntity;
import net.vg.sagittary.registry.ObjectRegistry;
import net.vg.sagittary.item.QuiverItem;
import net.vg.sagittary.network.CycleQuiverPayload;
import net.vg.sagittary.network.TopazPulsePayload;
import net.vg.sagittary.screen.FletchingTableScreen;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;

import java.util.List;

public class SagittaryClient {
    public static final KeyMapping QUIVER_CYCLE = new KeyMapping(
            "key.sagittary.quiver_cycle", InputConstants.Type.KEYSYM, InputConstants.KEY_V,
            //? if >=26.1 {
            new KeyMapping.Category(Identifier.fromNamespaceAndPath("sagittary", "general")));
            //? } else {
            /*"key.categories.sagittary");
            *///? }
    private static boolean quiverControlsInitialized;

    public static void init() {
        initEntityRenderers();
        initScreens();
        initItemRenderers();
        initTooltips();
        initQuiverControls();
        initNetworking();
    }

    public static void initNetworking() {
        NetworkManager.registerReceiver(NetworkManager.s2c(), TopazPulsePayload.TYPE, TopazPulsePayload.STREAM_CODEC,
                (payload, context) -> context.queue(() -> TopazPulseRenderer.addPulse(payload.targets())));
    }

    /** Shared Fabric/NeoForge quick-select input and HUD registration. */
    public static void initQuiverControls() {
        if (quiverControlsInitialized) return;
        quiverControlsInitialized = true;
        // NeoForge's Architectury raw-input bridge does not cancel vanilla hotbar
        // scrolling. NeoForge registers a native cancellable event instead.
        if (!Platform.isNeoForge()) {
            ClientRawInputEvent.MOUSE_SCROLLED.register((minecraft, horizontalAmount, verticalAmount) ->
                    handleQuiverScroll(minecraft, verticalAmount) ? EventResult.interruptTrue() : EventResult.pass());
        }
        ClientGuiEvent.RENDER_HUD.register(SagittaryClient::renderQuiverSelector);
    }

    /** @return true when the scroll was consumed by the quiver selector. */
    public static boolean handleQuiverScroll(Minecraft minecraft, double verticalAmount) {
        if (!QUIVER_CYCLE.isDown() || minecraft.player == null || verticalAmount == 0.0) return false;
        // Inventory hover owns scrolling while a screen is open; never send two requests.
        //? if >=26.2 {
        /*if (minecraft.gui.screen() != null) return false;
        *///? } else {
        if (minecraft.screen != null) return false;
        //? }
        if (QuiverItem.findActiveQuiver(minecraft.player).isEmpty()) return false;
        // Match the normal hotbar: scrolling down advances to the next slot.
        NetworkManager.sendToServer(new CycleQuiverPayload(verticalAmount < 0.0));
        return true;
    }

    /** Shared by legacy/current inventories, including Creative's client-only menu. */
    public static boolean handleInventoryQuiverScroll(Screen screen, double verticalAmount) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || verticalAmount == 0.0
                || !(screen instanceof AbstractContainerScreen<?> container)
                || !container.getMenu().getCarried().isEmpty()) return false;
        Slot hovered = ((ContainerScreenAccessor) container).sagittary$getHoveredSlot();
        if (hovered == null || !(hovered.getItem().getItem() instanceof QuiverItem)) return false;
        var menu = container.getMenu();
        int index = hovered.index;
        if (screen instanceof CreativeModeInventoryScreen || menu != minecraft.player.containerMenu) {
            // Match the backing stack by identity, not the creative wrapper's
            // index or item equality (two quivers can have identical contents).
            menu = minecraft.player.inventoryMenu;
            index = -1;
            for (Slot slot : menu.slots) {
                if (slot.getItem() == hovered.getItem()) { index = slot.index; break; }
            }
            if (index < 0) return false; // Catalog/trash slots are not player inventory.
        }
        NetworkManager.sendToServer(new CycleQuiverPayload(menu.containerId, index, verticalAmount < 0.0));
        return true;
    }

    private static void renderQuiverSelector(GuiGraphicsExtractor graphics, net.minecraft.client.DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        //? if >=26.2 {
        /*if (!QUIVER_CYCLE.isDown() || minecraft.player == null || minecraft.gui.hud.isHidden()) return;
        *///? } else {
        if (!QUIVER_CYCLE.isDown() || minecraft.player == null || minecraft.options.hideGui) return;
        //? }
        ItemStack quiver = QuiverItem.findActiveQuiver(minecraft.player);
        BundleContents contents = quiver.get(DataComponents.BUNDLE_CONTENTS);
        if (contents == null || contents.isEmpty()) return;

        List<ItemStack> arrows = contents.itemCopyStream().toList();
        int selected = QuiverItem.getSelectedIndex(quiver);
        if (selected < 0 || selected >= arrows.size()) selected = 0;
        int x = graphics.guiWidth() / 2 + 92;
        int y = graphics.guiHeight() - 23;
        for (int slot = 0; slot < 3; slot++) {
            int index = Math.floorMod(selected + slot - 1, arrows.size());
            int slotX = x + slot * 20;
            int background = slot == 1 ? 0xE05B4A27 : 0xD0302820;
            graphics.fill(slotX, y, slotX + 20, y + 20, background);
            // A one-pixel inset avoids GuiGraphicsExtractor's outline overdraw lines.
            graphics.fill(slotX + 1, y + 1, slotX + 19, y + 2, 0xFF8B7A5B);
            graphics.fill(slotX + 1, y + 18, slotX + 19, y + 19, 0xFF8B7A5B);
            graphics.fill(slotX + 1, y + 1, slotX + 2, y + 19, 0xFF8B7A5B);
            graphics.fill(slotX + 18, y + 1, slotX + 19, y + 19, 0xFF8B7A5B);
            //? if >=26.1 {
            graphics.item(arrows.get(index), slotX + 2, y + 2);
            graphics.itemDecorations(minecraft.font, arrows.get(index), slotX + 2, y + 2);
            //? } else {
            /*graphics.renderItem(arrows.get(index), slotX + 2, y + 2);
            graphics.renderItemDecorations(minecraft.font, arrows.get(index), slotX + 2, y + 2);
            *///? }
        }
    }

    public static void initTooltips() {
        ClientTooltipComponentRegistry.register(QuiverTooltip.class, QuiverTooltipRenderer::new);
    }
    
    public static void initItemRenderers() {
        //? if <26.1 {
        /*LegacyItemProperties.register();
        *///? }
    }
    public static void initEntityRenderers() {
        // REMOVED: Amethyst arrow is now part of the component system
        // EntityRendererRegistry.register(ObjectRegistry.AMETHYST_ARROW_ENTITY, AmethystArrowRenderer::new);
        EntityRendererRegistry.register(ObjectRegistry.COMPONENT_ARROW_ENTITY, ComponentArrowRenderer::new);
    }
    
    public static void initScreens() {
        //? if >=26.1 {
        MenuScreenRegistry.registerScreenFactory(ObjectRegistry.FLETCHING_TABLE_MENU_TYPE.get(), FletchingTableScreen::new);
        //? } else {
        /*MenuRegistry.registerScreenFactory(ObjectRegistry.FLETCHING_TABLE_MENU_TYPE.get(), FletchingTableScreen::new);
        *///? }
    }
    
}
