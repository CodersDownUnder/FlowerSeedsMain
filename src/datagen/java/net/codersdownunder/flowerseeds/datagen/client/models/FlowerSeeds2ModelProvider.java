package net.codersdownunder.flowerseeds.datagen.client.models;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.codersdownunder.flowerseeds2.FlowerSeeds2;
import net.codersdownunder.flowerseeds2.blocks.GenericFlowerCropBlock;
import net.codersdownunder.flowerseeds2.init.CropRegistries;
import net.codersdownunder.flowerseeds2.util.SeedColour;
import net.minecraft.client.color.item.Dye;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.blockstates.Variant;
import net.minecraft.client.data.models.blockstates.VariantProperties;
import net.minecraft.client.data.models.model.*;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.generators.loaders.CompositeModelBuilder;

public class FlowerSeeds2ModelProvider extends ModelProvider {
    public FlowerSeeds2ModelProvider(PackOutput output) {
        super(output, FlowerSeeds2.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {

        createVanillaFlowerCrop(blockModels, CropRegistries.DANDELION_CROP.get(), "block/dandelion", SeedColour.YELLOW);
        createVanillaFlowerCrop(blockModels, CropRegistries.ALLIUM_CROP.get(), "block/allium", SeedColour.MAGENTA);
        createVanillaFlowerCrop(blockModels, CropRegistries.AZURE_BLUET_CROP.get(), "block/azure_bluet", SeedColour.LIGHT_GREY);
        createVanillaFlowerCrop(blockModels, CropRegistries.BLUE_ORCHID_CROP.get(), "block/blue_orchid", SeedColour.LIGHT_BLUE);
        createVanillaFlowerCrop(blockModels, CropRegistries.CORNFLOWER_CROP.get(), "block/cornflower", SeedColour.BLUE);
        //createVanillaFlowerCrop(blockModels, CropRegistries.EYEBLOSSOM_CROP.get(), "block/eyeblossom", SeedColour.GREY);
        createVanillaFlowerCrop(blockModels, CropRegistries.LILY_OF_THE_VALLEY_CROP.get(), "block/lily_of_the_valley", SeedColour.WHITE);
        createVanillaFlowerCrop(blockModels, CropRegistries.OXEYE_DAISY_CROP.get(), "block/oxeye_daisy", SeedColour.LIGHT_GREY);
        createVanillaFlowerCrop(blockModels, CropRegistries.POPPY_CROP.get(), "block/poppy", SeedColour.RED);
        createVanillaFlowerCrop(blockModels, CropRegistries.ORANGE_TULIP_CROP.get(), "block/orange_tulip", SeedColour.ORANGE);
        createVanillaFlowerCrop(blockModels, CropRegistries.PINK_TULIP_CROP.get(), "block/pink_tulip", SeedColour.PINK);
        createVanillaFlowerCrop(blockModels, CropRegistries.RED_TULIP_CROP.get(), "block/red_tulip", SeedColour.RED);
        createVanillaFlowerCrop(blockModels, CropRegistries.WHITE_TULIP_CROP.get(), "block/white_tulip", SeedColour.LIGHT_GREY);
        createVanillaFlowerCrop(blockModels, CropRegistries.WITHER_ROSE_CROP.get(), "block/wither_rose", 0.00, SeedColour.BLACK);




    }


    public void createVanillaFlowerCrop(BlockModelGenerators blockModelGenerators, Block block, String texture, SeedColour colour) {
        createCropBlock(blockModelGenerators, (GenericFlowerCropBlock) block, BlockStateProperties.AGE_7,
                texture,mcLocation(texture), -0.06, colour, 0,0,1,1,1,2,3,3);
    }

    public void createVanillaFlowerCrop(BlockModelGenerators blockModelGenerators, Block block, String texture, double y, SeedColour colour) {
        createCropBlock(blockModelGenerators, (GenericFlowerCropBlock) block, BlockStateProperties.AGE_7,
                texture,mcLocation(texture), y, colour, 0,0,1,1,1,2,3,3);
    }

    private void createCropBlock(BlockModelGenerators blockModels, GenericFlowerCropBlock block, Property<Integer> maxAge, String texture,
            ResourceLocation model, double y, SeedColour colour, int... age) {
        if (maxAge.getPossibleValues().size() != age.length) {
            throw new IllegalArgumentException();
        } else {

            TextureMapping texture0 = new TextureMapping().put(TextureSlot.CROSS, mcLocation(texture)).put(TextureSlot.PARTICLE, mcLocation(texture));

            Int2ObjectMap<ResourceLocation> int2objectmap = new Int2ObjectOpenHashMap<>();

            blockModels.registerSimpleTintedItemModel(block, ResourceLocation.fromNamespaceAndPath(FlowerSeeds2.MODID, "item/flowerseed"), new Dye(colour.get()));
            blockModels.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block)
                    .with(createGrowthVariant(blockModels, int2objectmap, block, maxAge, texture0, model, y, age)));
        }
    }


    private PropertyDispatch createGrowthVariant(BlockModelGenerators blockModels, Int2ObjectMap<ResourceLocation> map, Block block,
            Property<Integer> maxAge, TextureMapping texture, ResourceLocation model, double y, int... age) {

        ResourceLocation stage0final = stage0(texture, y).create(block, texture, blockModels.modelOutput);
        ResourceLocation stage1final = stage1(texture, y).create(block, texture, blockModels.modelOutput);
        ResourceLocation stage2final = stage2(texture, y).create(block, texture, blockModels.modelOutput);
        ResourceLocation stage3final = stage3(texture, y, model).create(block, texture, blockModels.modelOutput);

        PropertyDispatch propertydispatch = PropertyDispatch.property(maxAge)
                .select(0, Variant.variant().with(VariantProperties.MODEL, stage0final))
                .select(1, Variant.variant().with(VariantProperties.MODEL, stage0final))
                .select(2, Variant.variant().with(VariantProperties.MODEL, stage1final))
                .select(3, Variant.variant().with(VariantProperties.MODEL, stage1final))
                .select(4, Variant.variant().with(VariantProperties.MODEL, stage1final))
                .select(5, Variant.variant().with(VariantProperties.MODEL, stage2final))
                .select(6, Variant.variant().with(VariantProperties.MODEL, stage3final))
                .select(7, Variant.variant().with(VariantProperties.MODEL, stage3final))
                ;

        return propertydispatch;
    }


    private ModelTemplate stage0(TextureMapping texture, double y) {
        ModelTemplate stage = FlowerSeeds2ModelTemplates.stage0.extend().customLoader(CompositeModelBuilder::new,
                compositeModelBuilder -> compositeModelBuilder
                .inlineChild("flower1", FlowerSeeds2ModelTemplates.stage0_internal.extend().rootTransforms(rootTransformsBuilder ->
                        rootTransformsBuilder.translation((float)0.23, (float)y, (float)0.23)).build(), texture)
                .inlineChild("flower2", FlowerSeeds2ModelTemplates.stage0_internal.extend().rootTransforms(rootTransformsBuilder ->
                        rootTransformsBuilder.translation((float)-0.23, (float)y, (float)0.23)).build(), texture)
                .inlineChild("flower3", FlowerSeeds2ModelTemplates.stage0_internal.extend().rootTransforms(rootTransformsBuilder ->
                        rootTransformsBuilder.translation((float)-0.23, (float)y, (float)-0.23)).build(), texture)
                .inlineChild("flower4", FlowerSeeds2ModelTemplates.stage0_internal.extend().rootTransforms(rootTransformsBuilder ->
                        rootTransformsBuilder.translation((float)0.23, (float)y, (float)-0.23)).build(), texture)
                .itemRenderOrder("flower1", "flower2", "flower3", "flower4")).build();

        return stage;
    }

    private ModelTemplate stage1(TextureMapping texture, double y) {
        ModelTemplate stage = FlowerSeeds2ModelTemplates.stage1.extend().customLoader(CompositeModelBuilder::new,
                compositeModelBuilder -> compositeModelBuilder
                        .inlineChild("flower1", FlowerSeeds2ModelTemplates.stage1_internal.extend().rootTransforms(rootTransformsBuilder ->
                                rootTransformsBuilder.translation((float)0.23, (float)y, (float)0.23)).build(), texture)
                        .inlineChild("flower2", FlowerSeeds2ModelTemplates.stage1_internal.extend().rootTransforms(rootTransformsBuilder ->
                                rootTransformsBuilder.translation((float)-0.23, (float)y, (float)0.23)).build(), texture)
                        .inlineChild("flower3", FlowerSeeds2ModelTemplates.stage1_internal.extend().rootTransforms(rootTransformsBuilder ->
                                rootTransformsBuilder.translation((float)-0.23, (float)y, (float)-0.23)).build(), texture)
                        .inlineChild("flower4", FlowerSeeds2ModelTemplates.stage1_internal.extend().rootTransforms(rootTransformsBuilder ->
                                rootTransformsBuilder.translation((float)0.23, (float)y, (float)-0.23)).build(), texture)
                        .itemRenderOrder("flower1", "flower2", "flower3", "flower4")).build();

        return stage;
    }

    private ModelTemplate stage2(TextureMapping texture, double y) {
        ModelTemplate stage = FlowerSeeds2ModelTemplates.stage2.extend().customLoader(CompositeModelBuilder::new,
                compositeModelBuilder -> compositeModelBuilder
                        .inlineChild("flower1", FlowerSeeds2ModelTemplates.stage2_internal.extend().rootTransforms(rootTransformsBuilder ->
                                rootTransformsBuilder.translation((float)0.23, (float)y, (float)0.23)).build(), texture)
                        .inlineChild("flower2", FlowerSeeds2ModelTemplates.stage2_internal.extend().rootTransforms(rootTransformsBuilder ->
                                rootTransformsBuilder.translation((float)-0.23, (float)y, (float)0.23)).build(), texture)
                        .inlineChild("flower3", FlowerSeeds2ModelTemplates.stage2_internal.extend().rootTransforms(rootTransformsBuilder ->
                                rootTransformsBuilder.translation((float)-0.23, (float)y, (float)-0.23)).build(), texture)
                        .inlineChild("flower4", FlowerSeeds2ModelTemplates.stage2_internal.extend().rootTransforms(rootTransformsBuilder ->
                                rootTransformsBuilder.translation((float)0.23, (float)y, (float)-0.23)).build(), texture)
                        .itemRenderOrder("flower1", "flower2", "flower3", "flower4")).build();

        return stage;
    }

    private ModelTemplate stage3(TextureMapping texture, double y, ResourceLocation model) {
        ModelTemplate stage = FlowerSeeds2ModelTemplates.stage3.extend().customLoader(CompositeModelBuilder::new,
                compositeModelBuilder -> compositeModelBuilder
                        .inlineChild("flower1", FlowerSeeds2ModelTemplates.stage3_internal.extend().parent(model).rootTransforms(rootTransformsBuilder ->
                                rootTransformsBuilder.translation((float)0.23, (float)y, (float)0.23)).build(), texture)
                        .inlineChild("flower2", FlowerSeeds2ModelTemplates.stage3_internal.extend().parent(model).rootTransforms(rootTransformsBuilder ->
                                rootTransformsBuilder.translation((float)-0.23, (float)y, (float)0.23)).build(), texture)
                        .inlineChild("flower3", FlowerSeeds2ModelTemplates.stage3_internal.extend().parent(model).rootTransforms(rootTransformsBuilder ->
                                rootTransformsBuilder.translation((float)-0.23, (float)y, (float)-0.23)).build(), texture)
                        .inlineChild("flower4", FlowerSeeds2ModelTemplates.stage3_internal.extend().parent(model).rootTransforms(rootTransformsBuilder ->
                                rootTransformsBuilder.translation((float)0.23, (float)y, (float)-0.23)).build(), texture)
                        .itemRenderOrder("flower1", "flower2", "flower3", "flower4")).build();

        return stage;
    }
}
