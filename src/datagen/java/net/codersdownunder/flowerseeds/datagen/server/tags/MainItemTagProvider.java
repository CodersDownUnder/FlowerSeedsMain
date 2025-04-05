package net.codersdownunder.flowerseeds.datagen.server.tags;

import net.codersdownunder.flowerseeds.FlowerSeeds;
import net.codersdownunder.flowerseeds.data.tags.FlowerSeedsItemTagsProvider;
import net.codersdownunder.flowerseeds.init.BlockInit;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class MainItemTagProvider extends FlowerSeedsItemTagsProvider {

    public MainItemTagProvider(PackOutput p_275343_, CompletableFuture<HolderLookup.Provider> p_275729_,
            CompletableFuture<TagLookup<Block>> p_275322_, @Nullable ExistingFileHelper existingFileHelper) {

        super(p_275343_, p_275729_, p_275322_, FlowerSeeds.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {

        tagSet(BlockInit.DANDELION_SEED.get().asItem(),
                BlockInit.WITHER_ROSE_SEED.get().asItem(),
                BlockInit.DANDELION_SEED.get().asItem(),
                BlockInit.WHITE_TULIP_SEED.get().asItem(),
                BlockInit.OXEYE_DAISY_SEED.get().asItem(),
                BlockInit.RED_TULIP_SEED.get().asItem(),
                BlockInit.POPPY_SEED.get().asItem(),
                BlockInit.PINK_TULIP_SEED.get().asItem(),
                BlockInit.ORANGE_TULIP_SEED.get().asItem(),
                BlockInit.LILY_OF_THE_VALLEY_SEED.get().asItem(),
                BlockInit.BLUE_ORCHID_SEED.get().asItem(),
                BlockInit.CORNFLOWER_SEED.get().asItem(),
                BlockInit.AZURE_BLUET_SEED.get().asItem(),
                BlockInit.ALLIUM_SEED.get().asItem());
    }

}
