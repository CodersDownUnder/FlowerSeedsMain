package net.codersdownunder.flowerseeds.datagen.server.tags;

import net.codersdownunder.flowerseeds2.FlowerSeeds2;
import net.codersdownunder.flowerseeds2.init.CropRegistries;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class FlowerSeeds2ItemTagsProvider extends ItemTagsProvider {
    // Get parameters from one of the `GatherDataEvent`s.
    public FlowerSeeds2ItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> p_275322_) {
        super(output, lookupProvider, p_275322_, FlowerSeeds2.MODID);
    }

    // Add your tag entries here.
    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        // Create a tag builder for our tag. This could also be e.g. a vanilla or NeoForge tag.
        tagSet(CropRegistries.DANDELION_CROP.get().asItem(),
                CropRegistries.WITHER_ROSE_CROP.get().asItem(),
                CropRegistries.DANDELION_CROP.get().asItem(),
                CropRegistries.WHITE_TULIP_CROP.get().asItem(),
                CropRegistries.OXEYE_DAISY_CROP.get().asItem(),
                CropRegistries.RED_TULIP_CROP.get().asItem(),
                CropRegistries.POPPY_CROP.get().asItem(),
                CropRegistries.PINK_TULIP_CROP.get().asItem(),
                CropRegistries.ORANGE_TULIP_CROP.get().asItem(),
                CropRegistries.LILY_OF_THE_VALLEY_CROP.get().asItem(),
                CropRegistries.BLUE_ORCHID_CROP.get().asItem(),
                CropRegistries.CORNFLOWER_CROP.get().asItem(),
                CropRegistries.AZURE_BLUET_CROP.get().asItem(),
                CropRegistries.ALLIUM_CROP.get().asItem());
    }

    public void tagSet(Item... items) {

        tag(ItemTags.VILLAGER_PLANTABLE_SEEDS)
                .add(items);

        tag(Tags.Items.SEEDS)
                .add(items);

        tag(Tags.Items.CROPS)
                .add(items);

        tag(ItemTags.SMALL_FLOWERS)
                .add(items);

    }
}