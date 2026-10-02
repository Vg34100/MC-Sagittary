package net.vg.sagittary.compat;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.architectury.registry.level.entity.trade.TradeRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

/** 1.21 has no data-driven villager trades. Keep offer values in the canonical JSON. */
public final class LegacyVillagerTrades {
    private LegacyVillagerTrades() {}

    public static void register() {
        try (var input = Objects.requireNonNull(LegacyVillagerTrades.class.getResourceAsStream(
                "/data/sagittary/villager_trade/hunter_quiver_upgrade.json"));
             var reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
            var trade = JsonParser.parseReader(reader).getAsJsonObject();
            TradeRegistry.registerVillagerTrade(VillagerProfession.FLETCHER, 5, (entity, random) -> {
                var wants = trade.getAsJsonObject("wants");
                var additional = trade.getAsJsonObject("additional_wants");
                var gives = trade.getAsJsonObject("gives");
                return new MerchantOffer(new ItemCost(item(wants), wants.get("count").getAsInt()),
                        Optional.of(new ItemCost(item(additional), additional.get("count").getAsInt())),
                        new ItemStack(item(gives), gives.get("count").getAsInt()),
                        trade.get("max_uses").getAsInt(), trade.get("xp").getAsInt(),
                        trade.get("reputation_discount").getAsFloat());
            });
        } catch (java.io.IOException exception) {
            throw new IllegalStateException("Cannot load Sagittary's legacy fletcher trade", exception);
        }
    }

    private static Item item(JsonObject cost) {
        return BuiltInRegistries.ITEM.get(ResourceLocation.parse(cost.get("id").getAsString()));
    }
}
