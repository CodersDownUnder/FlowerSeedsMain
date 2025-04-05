package net.codersdownunder.flowerseeds.datagen.server.tags;

import net.codersdownunder.flowerseeds.FlowerSeeds;
import net.codersdownunder.flowerseeds.data.tags.FlowerSeedsBlockTagsProvider;
import net.codersdownunder.flowerseeds.init.BlockInit;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class MainBlockTagProvider extends FlowerSeedsBlockTagsProvider {

    public MainBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {

        super(output, lookupProvider, FlowerSeeds.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {

        tagSet(BlockInit.DANDELION_SEED.get(),
                BlockInit.WITHER_ROSE_SEED.get(),
                BlockInit.DANDELION_SEED.get(),
                BlockInit.WHITE_TULIP_SEED.get(),
                BlockInit.OXEYE_DAISY_SEED.get(),
                BlockInit.RED_TULIP_SEED.get(),
                BlockInit.POPPY_SEED.get(),
                BlockInit.PINK_TULIP_SEED.get(),
                BlockInit.ORANGE_TULIP_SEED.get(),
                BlockInit.LILY_OF_THE_VALLEY_SEED.get(),
                BlockInit.BLUE_ORCHID_SEED.get(),
                BlockInit.CORNFLOWER_SEED.get(),
                BlockInit.AZURE_BLUET_SEED.get(),
                BlockInit.ALLIUM_SEED.get());
    }

}
