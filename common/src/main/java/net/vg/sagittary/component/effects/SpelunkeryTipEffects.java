package net.vg.sagittary.component.effects;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.vg.sagittary.entity.ComponentArrowEntity;
import net.vg.sagittary.network.TopazPulsePayload;
import dev.architectury.networking.NetworkManager;

import java.util.ArrayList;
import java.util.List;

/** Optional Spelunkery material effects. They only become craftable when their source item exists. */
public final class SpelunkeryTipEffects {
    private SpelunkeryTipEffects() {}

    public static ComponentEffect ruby() { return new AreaEffect(2.5, target -> target.setRemainingFireTicks(100)); }
    public static ComponentEffect sapphire() { return new AreaEffect(2.5, target -> {
        target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 1));
        target.setTicksFrozen(Math.max(target.getTicksFrozen(), 100));
    }); }
    public static ComponentEffect bronze() { return new ComponentEffect() {
        @Override public void onEntityHit(EntityHitResult hit, ComponentArrowEntity arrow) {
            if (arrow.level().isClientSide()) return;
            for (LivingEntity target : arrow.level().getEntitiesOfClass(LivingEntity.class, hit.getEntity().getBoundingBox().inflate(2.5), LivingEntity::isAlive)) {
                Vec3 direction = target.position().subtract(arrow.position());
                if (direction.lengthSqr() < .01) direction = new Vec3(arrow.getDeltaMovement().x, 0, arrow.getDeltaMovement().z);
                direction = direction.normalize();
                target.push(direction.x * 1.15, .3, direction.z * 1.15);
            }
        }
    }; }
    public static ComponentEffect electrum() { return new AreaEffect(5.0, target -> {} ) {
        @Override public void onEntityHit(EntityHitResult hit, ComponentArrowEntity arrow) {
            if (arrow.level().isClientSide()) return;
            int chained = 0;
            for (LivingEntity target : arrow.level().getEntitiesOfClass(LivingEntity.class, hit.getEntity().getBoundingBox().inflate(5), entity -> entity != hit.getEntity() && entity.isAlive())) {
                target.hurt(arrow.damageSources().arrow(arrow, arrow.getOwner()), 2.0f);
                if (++chained == 2) break;
            }
        }
    }; }
    public static ComponentEffect invar() { return new ComponentEffect() {
        @Override public void onEntityHit(EntityHitResult hit, ComponentArrowEntity arrow) {
            if (hit.getEntity() instanceof LivingEntity target && !arrow.level().isClientSide()) target.hurt(arrow.damageSources().magic(), 3.0f);
        }
        @Override public double getGravityModifier(ComponentArrowEntity arrow) { return 1.65; }
    }; }
    public static ComponentEffect topaz() { return new ComponentEffect() {
        private static final TagKey<Block> TARGETS = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("spelunkery", "prospector_targets"));
        @Override public void onBlockHit(BlockHitResult hit, ComponentArrowEntity arrow) {
            if (arrow.level().isClientSide()) return;
            BlockPos origin = BlockPos.containing(hit.getLocation());
            List<BlockPos> targets = new ArrayList<>();
            for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-6, -4, -6), origin.offset(6, 4, 6))) {
                if (arrow.level().getBlockState(pos).is(TARGETS)) {
                    targets.add(pos.immutable());
                    if (targets.size() == 12) break;
                }
            }
            if (targets.isEmpty()) return;
            TopazPulsePayload payload = new TopazPulsePayload(targets);
            for (ServerPlayer player : arrow.level().getServer().getPlayerList().getPlayers()) {
                if (player.level() == arrow.level() && player.distanceToSqr(Vec3.atCenterOf(origin)) <= 32 * 32) {
                    NetworkManager.sendToPlayer(player, payload);
                }
            }
        }
    }; }

    private static class AreaEffect implements ComponentEffect {
        private final double radius;
        private final java.util.function.Consumer<LivingEntity> action;
        AreaEffect(double radius, java.util.function.Consumer<LivingEntity> action) { this.radius = radius; this.action = action; }
        @Override public void onEntityHit(EntityHitResult hit, ComponentArrowEntity arrow) {
            if (arrow.level().isClientSide()) return;
            for (LivingEntity target : arrow.level().getEntitiesOfClass(LivingEntity.class, hit.getEntity().getBoundingBox().inflate(radius), LivingEntity::isAlive)) action.accept(target);
        }
    }
}
