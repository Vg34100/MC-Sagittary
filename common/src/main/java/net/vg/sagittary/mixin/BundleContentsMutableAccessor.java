package net.vg.sagittary.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import org.apache.commons.lang3.math.Fraction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(BundleContents.Mutable.class)
public interface BundleContentsMutableAccessor {
    @Accessor("items")
    List<ItemStack> sagittary$getItems();

    @Accessor("weight")
    Fraction sagittary$getWeight();

    @Accessor("weight")
    void sagittary$setWeight(Fraction weight);

    //? if >=26.1 {
    @Accessor("selectedItem")
    int sagittary$getSelectedItem();

    @Accessor("selectedItem")
    void sagittary$setSelectedItem(int index);
    //? }
}
