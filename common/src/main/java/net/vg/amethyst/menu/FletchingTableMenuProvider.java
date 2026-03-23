package net.vg.amethyst.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

public class FletchingTableMenuProvider implements MenuProvider {
    private final BlockPos pos;
    
    public FletchingTableMenuProvider(BlockPos pos) {
        this.pos = pos;
    }
    
    @Override
    public Component getDisplayName() {
        return Component.translatable("container.fletching");
    }
    
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new FletchingTableMenu(containerId, inventory, pos);
    }
}