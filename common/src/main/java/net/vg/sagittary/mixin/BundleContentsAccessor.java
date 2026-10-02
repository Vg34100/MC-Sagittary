package net.vg.sagittary.mixin;

import com.mojang.serialization.DataResult;
import net.minecraft.world.item.component.BundleContents;
import org.apache.commons.lang3.math.Fraction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Supplier;

@Mixin(BundleContents.class)
public interface BundleContentsAccessor {
    @Accessor("weight")
    //? if >=26.1 {
    Supplier<DataResult<Fraction>> sagittary$getWeight();
    //? } else {
    /*Fraction sagittary$getWeight();
    *///? }

    @Mutable
    @Accessor("weight")
    //? if >=26.1 {
    void sagittary$setWeight(Supplier<DataResult<Fraction>> weight);
    //? } else {
    /*void sagittary$setWeight(Fraction weight);
    *///? }

    //? if >=26.1 {
    @Accessor("selectedItem")
    int sagittary$getSelectedItem();

    @Mutable
    @Accessor("selectedItem")
    void sagittary$setSelectedItem(int selectedItem);
    //? }
}
