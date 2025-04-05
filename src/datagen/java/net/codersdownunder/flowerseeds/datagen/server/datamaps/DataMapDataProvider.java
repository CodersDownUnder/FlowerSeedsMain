package net.codersdownunder.flowerseeds.datagen.server.datamaps;

import net.codersdownunder.flowerseeds.init.BlockInit;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.datamaps.builtin.Compostable;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

import java.util.concurrent.CompletableFuture;

public class DataMapDataProvider extends DataMapProvider {

    public DataMapDataProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {

        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather() {

        var compostables = builder(NeoForgeDataMaps.COMPOSTABLES);

        for (DeferredHolder<Block, ? extends Block> block : BlockInit.BLOCKS.getEntries()) {
            compostables.add(block.get().asItem().builtInRegistryHolder(), new Compostable(0.3f), false);
        }

        compostables.build();

    }

}
