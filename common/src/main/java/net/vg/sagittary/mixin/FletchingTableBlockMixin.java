package net.vg.sagittary.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.vg.sagittary.menu.FletchingTableMenuProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//? if >=26.1 {
@Mixin(BlockBehaviour.class)
//? } else {
/*@Mixin(net.minecraft.world.level.block.FletchingTableBlock.class)
*///? }
public class FletchingTableBlockMixin {
    
    @Inject(method = "useWithoutItem", at = @At("HEAD"), cancellable = true)
    private void onUseWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (!blockState.is(Blocks.FLETCHING_TABLE)) {
            return;
        }
        if (!level.isClientSide()) {
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.openMenu(new FletchingTableMenuProvider(blockPos));
            }
        }
        cir.setReturnValue(InteractionResult.SUCCESS);
    }
}
