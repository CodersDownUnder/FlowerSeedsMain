package net.codersdownunder.flowerseeds.datagen.server.datamaps;

import net.codersdownunder.flowerseeds2.init.CropRegistries;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.datamaps.builtin.Compostable;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

import java.util.concurrent.CompletableFuture;

public class FlowerSeeds2DataMapProvider extends DataMapProvider {
    public FlowerSeeds2DataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }
    
    @Override
    protected void gather(HolderLookup.Provider provider) {
        // We create a builder for the EXAMPLE_DATA data map and add our entries using #add.
        var compostables = builder(NeoForgeDataMaps.COMPOSTABLES);

        for (DeferredHolder<Block, ? extends Block> block : CropRegistries.BLOCKS.getEntries()) {
            compostables.add(block.get().asItem().builtInRegistryHolder(), new Compostable(0.3f), false);
        }

        compostables.build();
    }
}