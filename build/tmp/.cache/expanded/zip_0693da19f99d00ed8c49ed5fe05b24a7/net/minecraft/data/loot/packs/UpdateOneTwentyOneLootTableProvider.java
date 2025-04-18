package net.minecraft.data.loot.packs;

import java.util.List;
import java.util.Set;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class UpdateOneTwentyOneLootTableProvider {
    public static LootTableProvider create(PackOutput p_307605_) {
        return new LootTableProvider(
            p_307605_,
            Set.of(),
            List.of(
                new LootTableProvider.SubProviderEntry(UpdateOneTwentyOneBlockLoot::new, LootContextParamSets.BLOCK),
                new LootTableProvider.SubProviderEntry(UpdateOneTwentyOneChestLoot::new, LootContextParamSets.CHEST)
            )
        );
    }
}
