package net.codersdownunder.flowerseeds.datagen.server.loot;

import net.codersdownunder.flowerseeds2.blocks.GenericFlowerCropBlock;
import net.codersdownunder.flowerseeds2.init.CropRegistries;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class FlowerSeeds2LootProvider extends LootTableProvider {
    public FlowerSeeds2LootProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, Set.of(), List.of(
                new SubProviderEntry(FlowerSeeds2Blocks::new, LootContextParamSets.BLOCK)
        ), lookupProvider);
    }

    @Override
    protected void validate(@NotNull WritableRegistry<LootTable> writableregistry, @NotNull ValidationContext validationcontext, @NotNull ProblemReporter.Collector problemreporter$collector) {
        super.validate(writableregistry, validationcontext, problemreporter$collector);
    }

    private static class FlowerSeeds2Blocks extends BlockLootSubProvider {

        protected FlowerSeeds2Blocks(HolderLookup.Provider provider) {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
        }

        @Override
        protected void generate() {


            add(CropRegistries.DANDELION_CROP.get(), (block) -> createCropDrops(CropRegistries.DANDELION_CROP.get(), Items.DANDELION));
            add(CropRegistries.ALLIUM_CROP.get(), (block) -> createCropDrops(CropRegistries.ALLIUM_CROP.get(), Items.ALLIUM));
            add(CropRegistries.AZURE_BLUET_CROP.get(), (block) -> createCropDrops(CropRegistries.AZURE_BLUET_CROP.get(), Items.AZURE_BLUET));
            add(CropRegistries.CORNFLOWER_CROP.get(), (block) -> createCropDrops(CropRegistries.CORNFLOWER_CROP.get(), Items.CORNFLOWER));
            add(CropRegistries.BLUE_ORCHID_CROP.get(), (block) -> createCropDrops(CropRegistries.BLUE_ORCHID_CROP.get(), Items.BLUE_ORCHID));
            add(CropRegistries.LILY_OF_THE_VALLEY_CROP.get(), (block) -> createCropDrops(CropRegistries.LILY_OF_THE_VALLEY_CROP.get(), Items.LILY_OF_THE_VALLEY));
            add(CropRegistries.ORANGE_TULIP_CROP.get(), (block) -> createCropDrops(CropRegistries.ORANGE_TULIP_CROP.get(), Items.ORANGE_TULIP));
            add(CropRegistries.PINK_TULIP_CROP.get(), (block) -> createCropDrops(CropRegistries.PINK_TULIP_CROP.get(), Items.PINK_TULIP));
            add(CropRegistries.POPPY_CROP.get(), (block) -> createCropDrops(CropRegistries.POPPY_CROP.get(), Items.POPPY));
            add(CropRegistries.RED_TULIP_CROP.get(), (block) -> createCropDrops(CropRegistries.RED_TULIP_CROP.get(), Items.RED_TULIP));
            add(CropRegistries.OXEYE_DAISY_CROP.get(), (block) -> createCropDrops(CropRegistries.OXEYE_DAISY_CROP.get(), Items.OXEYE_DAISY));
            add(CropRegistries.WHITE_TULIP_CROP.get(), (block) -> createCropDrops(CropRegistries.WHITE_TULIP_CROP.get(), Items.WHITE_TULIP));
            add(CropRegistries.WITHER_ROSE_CROP.get(), (block) -> createCropDrops(CropRegistries.WITHER_ROSE_CROP.get(), Items.WITHER_ROSE));
        }


        private LootTable.Builder createCropDrops(Block cropBlock, Item cropItem) {
            LootItemCondition.Builder cropConditionBuilder = LootItemBlockStatePropertyCondition
                    .hasBlockStateProperties(cropBlock)
                    .setProperties(StatePropertiesPredicate.Builder.properties()
                    .hasProperty(GenericFlowerCropBlock.AGE, GenericFlowerCropBlock.MAX_AGE));

            return createCropDrops(cropBlock, cropItem, cropBlock.asItem(), cropConditionBuilder);
        }



        @NotNull
        @Override
        protected Iterable<Block> getKnownBlocks() {
            return (Iterable<Block>) CropRegistries.BLOCKS.getEntries().stream().map(holder -> (Block) holder.get())::iterator;
        }
    }
}
