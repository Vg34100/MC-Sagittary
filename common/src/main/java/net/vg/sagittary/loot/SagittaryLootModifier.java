package net.vg.sagittary.loot;

import dev.architectury.event.events.common.LootEvent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
//? if <26.3 {
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
//? }
import net.vg.sagittary.registry.ObjectRegistry;

public class SagittaryLootModifier {

    public static void init() {
        //? if >=26.3 || <1.21 {
        /*LootEvent.MODIFY_LOOT_TABLE.register((registries, key, context, builtin) -> {
        *///? } else {
        LootEvent.MODIFY_LOOT_TABLE.register((key, context, builtin) -> {
        //? }
            // Compound Bow in Trial Chambers reward chests
            if (isTrialChambersReward(key)) {
                context.addPool(LootPool.lootPool()
                        .setRolls(oneRoll())
                        .add(LootItem.lootTableItem(ObjectRegistry.COMPOUND_BOW_ITEM.get())
                                .setWeight(1))
                        .when(LootItemRandomChanceCondition.randomChance(0.15f))); // 15% chance

                //? if >=1.21 {
                float rangerTemplateChance = key.equals(BuiltInLootTables.TRIAL_CHAMBERS_REWARD_OMINOUS) ? 0.20f : 0.10f;
                //? } else {
                /*float rangerTemplateChance = 0.10f;
                *///? }
                context.addPool(LootPool.lootPool()
                        .setRolls(oneRoll())
                        .add(LootItem.lootTableItem(ObjectRegistry.RANGER_QUIVER_UPGRADE.get())
                                .setWeight(1))
                        .when(LootItemRandomChanceCondition.randomChance(rangerTemplateChance)));
            }

            // Repeater Crossbow in Ancient City chests
            if (isAncientCity(key)) {
                context.addPool(LootPool.lootPool()
                        .setRolls(oneRoll())
                        .add(LootItem.lootTableItem(ObjectRegistry.REPEATER_CROSSBOW_ITEM.get())
                                .setWeight(1))
                        .when(LootItemRandomChanceCondition.randomChance(0.10f))); // 10% chance
            }
        });
    }

    //? if >=26.3 {
    /*private static net.minecraft.core.Holder<net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider> oneRoll() {
        return net.minecraft.core.Holder.direct(new net.minecraft.world.level.storage.loot.providers.number.ints.ConstantValue(1));
    }
    *///? } else {
    private static ConstantValue oneRoll() { return ConstantValue.exactly(1); }
    //? }

    private static boolean isTrialChambersReward(Object key) {
        //? if >=1.21 {
        return key.equals(BuiltInLootTables.TRIAL_CHAMBERS_REWARD) ||
               key.equals(BuiltInLootTables.TRIAL_CHAMBERS_REWARD_OMINOUS);
        //? } else {
        /*// Trial Chambers do not exist yet. Preserve survival access to the
        // Compound Bow and Ranger template through the existing deep-city loot.
        return isAncientCity(key);
        *///? }
    }

    private static boolean isAncientCity(Object key) {
        return key.equals(BuiltInLootTables.ANCIENT_CITY);
    }
}
