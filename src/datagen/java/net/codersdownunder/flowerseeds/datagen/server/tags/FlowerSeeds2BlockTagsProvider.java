package net.codersdownunder.flowerseeds.datagen.server.tags;

import net.codersdownunder.flowerseeds2.FlowerSeeds2;
import net.codersdownunder.flowerseeds2.init.CropRegistries;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class FlowerSeeds2BlockTagsProvider extends BlockTagsProvider {
    // Get parameters from one of the `GatherDataEvent`s.
    public FlowerSeeds2BlockTagsProvider(PackOutput output, ExistingFileHelper ext, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, FlowerSeeds2.MODID, ext);
    }

    // Add your tag entries here.
    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        // Create a tag builder for our tag. This could also be e.g. a vanilla or NeoForge tag.
        tagSet(CropRegistries.DANDELION_CROP.get(),
                CropRegistries.WITHER_ROSE_CROP.get(),
                CropRegistries.DANDELION_CROP.get(),
                CropRegistries.WHITE_TULIP_CROP.get(),
                CropRegistries.OXEYE_DAISY_CROP.get(),
                CropRegistries.RED_TULIP_CROP.get(),
                CropRegistries.POPPY_CROP.get(),
                CropRegistries.PINK_TULIP_CROP.get(),
                CropRegistries.ORANGE_TULIP_CROP.get(),
                CropRegistries.LILY_OF_THE_VALLEY_CROP.get(),
                CropRegistries.BLUE_ORCHID_CROP.get(),
                CropRegistries.CORNFLOWER_CROP.get(),
                CropRegistries.AZURE_BLUET_CROP.get(),
                CropRegistries.ALLIUM_CROP.get());
    }

    public void tagSet(Block... blocks) {

        tag(BlockTags.CROPS)
                .add(blocks);

        tag(BlockTags.BEE_GROWABLES)
                .add(blocks);

        tag(BlockTags.MAINTAINS_FARMLAND)
                .add(blocks);

        tag(BlockTags.MINEABLE_WITH_AXE)
                .add(blocks);

        tag(BlockTags.SMALL_FLOWERS)
                .add(blocks);
    }
}