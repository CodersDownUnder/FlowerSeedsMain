package net.codersdownunder.flowerseeds.data;

import net.codersdownunder.flowerseeds.FlowerSeeds;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.loaders.CompositeModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

public abstract class FlowerSeedsBlockStates extends BlockStateProvider {

    public FlowerSeedsBlockStates(PackOutput output, String modid, ExistingFileHelper exFileHelper) {

        super(output, modid, exFileHelper);
    }

    private final float vertical_offset = -0.06f;
    private final String MODID = FlowerSeeds.MODID;

    private BlockModelBuilder fullyGrown(String texture, String model, String flower) {

        return models().getBuilder(flower + "_seed_stage3")
                .texture("particle", mcLoc(texture))
                .texture("cross", mcLoc(texture))
                .customLoader(CompositeModelBuilder::begin)
                .child("flower0", models().withExistingParent(flower + "0_stage3", mcLoc(model))
                        .rootTransforms().translation(0.23f, vertical_offset, 0.23f).end().renderType("minecraft:cutout_mipped"))
                .child("flower1", models().withExistingParent(flower + "1_stage3", mcLoc(model))
                        .rootTransforms().translation(-0.23f, vertical_offset, 0.23f).end().renderType("minecraft:cutout_mipped"))
                .child("flower2", models().withExistingParent(flower + "2_stage3", mcLoc(model))
                        .rootTransforms().translation(-0.23f, vertical_offset, -0.23f).end().renderType("minecraft:cutout_mipped"))
                .child("flower3", models().withExistingParent(flower + "3_stage3", mcLoc(model))
                        .rootTransforms().translation(0.23f, vertical_offset, -0.23f).end().renderType("minecraft:cutout_mipped"))
                .end();
    }

    private BlockModelBuilder stage(String texture, Integer stage, String flower) {

        return models().getBuilder("dandelion_seed_stage" + stage)
                .texture("particle", mcLoc(texture))
                .customLoader(CompositeModelBuilder::begin)
                .child("flower0", models().singleTexture(flower + "0_stage" + stage, new ResourceLocation(MODID, "block/cross_stage" + stage), "cross", mcLoc(texture))
                        .rootTransforms().translation(0.23f, vertical_offset, 0.23f).end()
                        .renderType("minecraft:cutout_mipped"))
                .child("flower1", models().singleTexture(flower + "1_stage" + stage, new ResourceLocation(MODID, "block/cross_stage" + stage), "cross", mcLoc(texture))
                        .rootTransforms().translation(-0.23f, vertical_offset, 0.23f).end()
                        .renderType("minecraft:cutout_mipped"))
                .child("flower2", models().singleTexture(flower + "2_stage" + stage, new ResourceLocation(MODID, "block/cross_stage" + stage), "cross", mcLoc(texture))
                        .rootTransforms().translation(-0.23f, vertical_offset, -0.23f).end()
                        .renderType("minecraft:cutout_mipped"))
                .child("flower3", models().singleTexture(flower + "3_stage" + stage, new ResourceLocation(MODID, "block/cross_stage" + stage), "cross", mcLoc(texture))
                        .rootTransforms().translation(0.23f, vertical_offset, -0.23f).end()
                        .renderType("minecraft:cutout_mipped"))
                .end();
    }

    public void flowerModel(Block block, String texture, String flower) {

        getVariantBuilder(block)
                .partialState()
                .with(CropBlock.AGE, 0)
                .modelForState()
                .modelFile(stage(texture, 0, flower))
                .addModel()
                .partialState()
                .with(CropBlock.AGE, 1)
                .modelForState()
                .modelFile(stage(texture, 0, flower))
                .addModel()
                .partialState()
                .with(CropBlock.AGE, 2)
                .modelForState()
                .modelFile(stage(texture, 1, flower))
                .addModel()
                .partialState()
                .with(CropBlock.AGE, 3)
                .modelForState()
                .modelFile(stage(texture, 1, flower))
                .addModel()
                .partialState()
                .with(CropBlock.AGE, 4)
                .modelForState()
                .modelFile(stage(texture, 1, flower))
                .addModel()
                .partialState()
                .with(CropBlock.AGE, 5)
                .modelForState()
                .modelFile(stage(texture, 2, flower))
                .addModel()
                .partialState()
                .with(CropBlock.AGE, 6)
                .modelForState()
                .modelFile(fullyGrown(texture, texture, flower))
                .addModel()
                .partialState()
                .with(CropBlock.AGE, 7)
                .modelForState()
                .modelFile(fullyGrown(texture, texture, flower))
                .addModel();

    }

    public BlockModelBuilder fullyGrownCompat(ResourceLocation texture, String model, String flower, String modid) {

        return models().getBuilder(flower + "_seed_stage3")
                .texture("particle", texture)
                .customLoader(CompositeModelBuilder::begin)
                .child("flower0", models().withExistingParent(flower + "0_stage3", new ResourceLocation(modid + ":" + model))
                        .rootTransforms().translation(0.23f, vertical_offset, 0.23f).end().renderType("minecraft:cutout_mipped"))
                .child("flower1", models().withExistingParent(flower + "1_stage3", new ResourceLocation(modid + ":" + model))
                        .rootTransforms().translation(-0.23f, vertical_offset, 0.23f).end().renderType("minecraft:cutout_mipped"))
                .child("flower2", models().withExistingParent(flower + "2_stage3", new ResourceLocation(modid + ":" + model))
                        .rootTransforms().translation(-0.23f, vertical_offset, -0.23f).end().renderType("minecraft:cutout_mipped"))
                .child("flower3", models().withExistingParent(flower + "3_stage3", new ResourceLocation(modid + ":" + model))
                        .rootTransforms().translation(0.23f, vertical_offset, -0.23f).end().renderType("minecraft:cutout_mipped"))
                .end();
    }

    public BlockModelBuilder stageCompat(ResourceLocation texture, Integer stage, String flower, String modid) {

        return models().getBuilder(flower + "_seed_stage" + stage)
                .texture("particle", texture)
                .texture("cross", texture)
                .customLoader(CompositeModelBuilder::begin)
                .child("flower0", models().singleTexture(flower + "0_stage" + stage, new ResourceLocation(MODID, "block/cross_stage" + stage), "cross", texture)
                        .rootTransforms().translation(0.23f, vertical_offset, 0.23f).end()
                        .renderType("minecraft:cutout_mipped"))
                .child("flower1", models().singleTexture(flower + "1_stage" + stage, new ResourceLocation(MODID, "block/cross_stage" + stage), "cross", texture)
                        .rootTransforms().translation(-0.23f, vertical_offset, 0.23f).end()
                        .renderType("minecraft:cutout_mipped"))
                .child("flower2", models().singleTexture(flower + "2_stage" + stage, new ResourceLocation(MODID, "block/cross_stage" + stage), "cross", texture)
                        .rootTransforms().translation(-0.23f, vertical_offset, -0.23f).end()
                        .renderType("minecraft:cutout_mipped"))
                .child("flower3", models().singleTexture(flower + "3_stage" + stage, new ResourceLocation(MODID, "block/cross_stage" + stage), "cross", texture)
                        .rootTransforms().translation(0.23f, vertical_offset, -0.23f).end()
                        .renderType("minecraft:cutout_mipped"))
                .end();
    }

    public void flowerModelCompat(Block block, String texture, String flower, String modid, Block compatBlock) {

        getVariantBuilder(block)
                .partialState()
                .with(CropBlock.AGE, 0)
                .modelForState()
                .modelFile(stageCompat(blockTexture(compatBlock), 0, flower, modid))
                .addModel()
                .partialState()
                .with(CropBlock.AGE, 1)
                .modelForState()
                .modelFile(stageCompat(blockTexture(compatBlock), 0, flower, modid))
                .addModel()
                .partialState()
                .with(CropBlock.AGE, 2)
                .modelForState()
                .modelFile(stageCompat(blockTexture(compatBlock), 1, flower, modid))
                .addModel()
                .partialState()
                .with(CropBlock.AGE, 3)
                .modelForState()
                .modelFile(stageCompat(blockTexture(compatBlock), 1, flower, modid))
                .addModel()
                .partialState()
                .with(CropBlock.AGE, 4)
                .modelForState()
                .modelFile(stageCompat(blockTexture(compatBlock), 1, flower, modid))
                .addModel()
                .partialState()
                .with(CropBlock.AGE, 5)
                .modelForState()
                .modelFile(stageCompat(blockTexture(compatBlock), 2, flower, modid))
                .addModel()
                .partialState()
                .with(CropBlock.AGE, 6)
                .modelForState()
                .modelFile(fullyGrownCompat(blockTexture(compatBlock), texture, flower, modid))
                .addModel()
                .partialState()
                .with(CropBlock.AGE, 7)
                .modelForState()
                .modelFile(fullyGrownCompat(blockTexture(compatBlock), texture, flower, modid))
                .addModel();

    }

    private void blockWithItem(DeferredHolder<Block, ? extends Block> blockRegistryObject) {

        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }
}
