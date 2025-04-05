package net.codersdownunder.flowerseeds.data.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public abstract class FlowerSeedsBlockTagsProvider extends IntrinsicHolderTagsProvider<Block> {

    public FlowerSeedsBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {

        super(output, Registries.BLOCK, lookupProvider, block -> block.builtInRegistryHolder().key(), modId, existingFileHelper);
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
