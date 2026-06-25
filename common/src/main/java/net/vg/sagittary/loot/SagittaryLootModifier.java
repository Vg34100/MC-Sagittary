package net.vg.sagittary.loot;

import dev.architectury.event.events.common.LootEvent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.vg.sagittary.registry.ObjectRegistry;

public class SagittaryLootModifier {

    public static void init() {
        LootEvent.MODIFY_LOOT_TABLE.register((key, context, builtin) -> {
            // Compound Bow in Trial Chambers reward chests
            if (isTrialChambersReward(key)) {
                context.addPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(ObjectRegistry.COMPOUND_BOW_ITEM.get())
                                .setWeight(1))
                        .when(LootItemRandomChanceCondition.randomChance(0.15f))); // 15% chance
            }

            // Repeater Crossbow in Ancient City chests
            if (isAncientCity(key)) {
                context.addPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(ObjectRegistry.REPEATER_CROSSBOW_ITEM.get())
                                .setWeight(1))
                        .when(LootItemRandomChanceCondition.randomChance(0.10f))); // 10% chance
            }
        });
    }

    private static boolean isTrialChambersReward(ResourceKey<LootTable> key) {
        return key.equals(BuiltInLootTables.TRIAL_CHAMBERS_REWARD) ||
               key.equals(BuiltInLootTables.TRIAL_CHAMBERS_REWARD_OMINOUS);
    }

    private static boolean isAncientCity(ResourceKey<LootTable> key) {
        return key.equals(BuiltInLootTables.ANCIENT_CITY);
    }
}
