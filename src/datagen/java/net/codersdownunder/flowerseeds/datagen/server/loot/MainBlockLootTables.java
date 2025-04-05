package net.codersdownunder.flowerseeds.datagen.server.loot;

import net.codersdownunder.flowerseeds.init.BlockInit;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.Set;

public class MainBlockLootTables extends BlockLootSubProvider {

    public MainBlockLootTables() {

        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {

        add(BlockInit.DANDELION_SEED.get(), FlowerLootTableBuilder(BlockInit.DANDELION_SEED.get(), Items.DANDELION));
        add(BlockInit.ALLIUM_SEED.get(), FlowerLootTableBuilder(BlockInit.ALLIUM_SEED.get(), Items.ALLIUM));
        add(BlockInit.AZURE_BLUET_SEED.get(), FlowerLootTableBuilder(BlockInit.AZURE_BLUET_SEED.get(), Items.AZURE_BLUET));
        add(BlockInit.CORNFLOWER_SEED.get(), FlowerLootTableBuilder(BlockInit.CORNFLOWER_SEED.get(), Items.CORNFLOWER));
        add(BlockInit.BLUE_ORCHID_SEED.get(), FlowerLootTableBuilder(BlockInit.BLUE_ORCHID_SEED.get(), Items.BLUE_ORCHID));
        add(BlockInit.LILY_OF_THE_VALLEY_SEED.get(), FlowerLootTableBuilder(BlockInit.LILY_OF_THE_VALLEY_SEED.get(), Items.LILY_OF_THE_VALLEY));
        add(BlockInit.ORANGE_TULIP_SEED.get(), FlowerLootTableBuilder(BlockInit.ORANGE_TULIP_SEED.get(), Items.ORANGE_TULIP));
        add(BlockInit.PINK_TULIP_SEED.get(), FlowerLootTableBuilder(BlockInit.PINK_TULIP_SEED.get(), Items.PINK_TULIP));
        add(BlockInit.POPPY_SEED.get(), FlowerLootTableBuilder(BlockInit.POPPY_SEED.get(), Items.POPPY));
        add(BlockInit.RED_TULIP_SEED.get(), FlowerLootTableBuilder(BlockInit.RED_TULIP_SEED.get(), Items.RED_TULIP));
        add(BlockInit.OXEYE_DAISY_SEED.get(), FlowerLootTableBuilder(BlockInit.OXEYE_DAISY_SEED.get(), Items.OXEYE_DAISY));
        add(BlockInit.WHITE_TULIP_SEED.get(), FlowerLootTableBuilder(BlockInit.WHITE_TULIP_SEED.get(), Items.WHITE_TULIP));
        add(BlockInit.WITHER_ROSE_SEED.get(), FlowerLootTableBuilder(BlockInit.WITHER_ROSE_SEED.get(), Items.WITHER_ROSE));
    }

    protected LootTable.Builder FlowerLootTableBuilder(Block pCropBlock, Item pGrownCropItem) {

        LootItemCondition.Builder lootitemcondition$builder = LootItemBlockStatePropertyCondition
                .hasBlockStateProperties(pCropBlock).setProperties(StatePropertiesPredicate.Builder
                        .properties().hasProperty(CropBlock.AGE, 7)).or(
                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(pCropBlock)
                                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CropBlock.AGE, 6)));
        return FlowerSeedsCropDrops(pCropBlock, pGrownCropItem, pCropBlock.asItem(), lootitemcondition$builder);
    }

    protected LootTable.Builder FlowerSeedsCropDrops(Block pCropBlock, Item pGrownCropItem, Item pSeedsItem, LootItemCondition.Builder pDropGrownCropCondition) {

        return applyExplosionDecay(pCropBlock, LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem(pGrownCropItem).setWeight(2).when(pDropGrownCropCondition).otherwise(LootItem.lootTableItem(pSeedsItem)))).withPool(LootPool.lootPool().when(pDropGrownCropCondition).add(LootItem.lootTableItem(pSeedsItem).apply(ApplyBonusCount.addBonusBinomialDistributionCount(Enchantments.BLOCK_FORTUNE, 0.5714286F, 3)))));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {

        return BlockInit.BLOCKS.getEntries() // Get all registered entries
                .stream() // Stream the wrapped objects
                .map(Holder::value) // Get the object if available
                ::iterator; // Create the iterable
    }

}
