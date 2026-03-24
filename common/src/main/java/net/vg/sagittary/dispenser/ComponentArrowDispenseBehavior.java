package net.vg.sagittary.dispenser;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.vg.sagittary.entity.ComponentArrowEntity;
import net.vg.sagittary.item.ComponentArrowItem;

public class ComponentArrowDispenseBehavior extends ProjectileDispenseBehavior {

    public ComponentArrowDispenseBehavior(ComponentArrowItem item) {
        super(item);
    }
    
    @Override
    public ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
        ServerLevel serverLevel = blockSource.level();
        Direction direction = blockSource.state().getValue(DispenserBlock.FACING);
        Position position = DispenserBlock.getDispensePosition(blockSource);
        
        // Create the arrow entity with proper component data preservation using dispenser constructor
        // Use a crossbow as the weapon since dispensers act like crossbows
        ItemStack dispenserWeapon = new ItemStack(net.minecraft.world.item.Items.CROSSBOW);
        ComponentArrowEntity arrow = new ComponentArrowEntity(serverLevel, position.x(), position.y(), position.z(), itemStack, dispenserWeapon);
        
        // Position and shoot the arrow
        arrow.setPos(position.x(), position.y(), position.z());
        arrow.shoot(direction.getStepX(), direction.getStepY(), direction.getStepZ(), 1.1F, 6.0F);
        serverLevel.addFreshEntity(arrow);
        
        itemStack.shrink(1);
        return itemStack;
    }
}