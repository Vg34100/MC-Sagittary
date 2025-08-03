package net.vg.amethyst.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * Data component representing the three parts of a component arrow.
 * Used as a DataComponentType instead of raw NBT for better type safety and JSON model integration.
 */
public record ArrowParts(String tip, String shaft, String fletching) {
    
    public static final Codec<ArrowParts> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.STRING.fieldOf("tip").forGetter(ArrowParts::tip),
            Codec.STRING.fieldOf("shaft").forGetter(ArrowParts::shaft),
            Codec.STRING.fieldOf("fletching").forGetter(ArrowParts::fletching)
        ).apply(instance, ArrowParts::new)
    );
    
    public static final StreamCodec<RegistryFriendlyByteBuf, ArrowParts> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8, ArrowParts::tip,
        ByteBufCodecs.STRING_UTF8, ArrowParts::shaft,
        ByteBufCodecs.STRING_UTF8, ArrowParts::fletching,
        ArrowParts::new
    );
    
    /**
     * Creates ArrowParts from ArrowComponent instances
     */
    public static ArrowParts of(ArrowComponent tip, ArrowComponent shaft, ArrowComponent fletching) {
        return new ArrowParts(tip.getMaterialName(), shaft.getMaterialName(), fletching.getMaterialName());
    }
    
    /**
     * Gets the tip component, falling back to default if invalid
     */
    public ArrowComponent getTipComponent() {
        return ArrowComponent.getByMaterialAndType(tip, ArrowComponent.ComponentType.TIP);
    }
    
    /**
     * Gets the shaft component, falling back to default if invalid
     */
    public ArrowComponent getShaftComponent() {
        return ArrowComponent.getByMaterialAndType(shaft, ArrowComponent.ComponentType.SHAFT);
    }
    
    /**
     * Gets the fletching component, falling back to default if invalid
     */
    public ArrowComponent getFletchingComponent() {
        return ArrowComponent.getByMaterialAndType(fletching, ArrowComponent.ComponentType.FLETCHING);
    }
    
    /**
     * Default arrow parts configuration
     */
    public static final ArrowParts DEFAULT = new ArrowParts("flint", "stick", "feather");
}