package net.codersdownunder.flowerseeds.data.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class FlowerSeedsItemTagsProvider extends IntrinsicHolderTagsProvider<Item> {

    private final CompletableFuture<TagsProvider.TagLookup<Block>> blockTags;
    private final Map<TagKey<Block>, TagKey<Item>> tagsToCopy = new HashMap<>();

    public FlowerSeedsItemTagsProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, CompletableFuture<TagsProvider.TagLookup<Block>> pBlockTags, String modId, @org.jetbrains.annotations.Nullable ExistingFileHelper existingFileHelper) {

        super(pOutput, Registries.ITEM, pLookupProvider, (p_255790_) -> {
            return p_255790_.builtInRegistryHolder().key();
        }, modId, existingFileHelper);
        this.blockTags = pBlockTags;
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
