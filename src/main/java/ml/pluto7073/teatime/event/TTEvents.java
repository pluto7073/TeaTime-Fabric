package ml.pluto7073.teatime.event;

import ml.pluto7073.teatime.item.TTItems;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class TTEvents {

    public static void init() {
        RollTeaLeaves.rollLeavesEvent();
        modifyLootTables();
    }

    private static void modifyLootTables() {
        LootTableEvents.MODIFY.register((id, tableBuilder, source, provider) -> {
            if (ResourceLocation.withDefaultNamespace("chests/village/village_plains_house").equals(id.location())) {
                LootPool.Builder pool = LootPool.lootPool()
                        .with(LootItem.lootTableItem(TTItems.TEA_SEEDS)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 3))).build())
                        .setRolls(UniformGenerator.between(0, 1));

                tableBuilder.withPool(pool);
            }
        });
    }

}
