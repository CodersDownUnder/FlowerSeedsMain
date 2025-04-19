package net.codersdownunder.flowerseeds2.init;

import net.codersdownunder.flowerseeds2.FlowerSeeds2;
import net.codersdownunder.flowerseeds2.blocks.GenericFlowerCropBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

public class CropRegistries {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(FlowerSeeds2.MODID);

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(FlowerSeeds2.MODID);

    public static final DeferredBlock<Block> DANDELION_CROP = registerBlock("dandelion_crop",
            GenericFlowerCropBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).noCollission().randomTicks().strength(0.0F)
            .sound(SoundType.CROP));

    public static final DeferredBlock<Block> WITHER_ROSE_CROP = registerBlock("wither_rose_crop",
            GenericFlowerCropBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).noCollission().randomTicks().strength(0.0F)
                    .sound(SoundType.CROP));

    public static final DeferredBlock<Block> ALLIUM_CROP = registerBlock("allium_crop",
            GenericFlowerCropBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).noCollission().randomTicks().strength(0.0F)
                    .sound(SoundType.CROP));

    public static final DeferredBlock<Block> AZURE_BLUET_CROP = registerBlock("azure_bluet_crop",
            GenericFlowerCropBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).noCollission().randomTicks().strength(0.0F)
                    .sound(SoundType.CROP));

    public static final DeferredBlock<Block> BLUE_ORCHID_CROP = registerBlock("blue_orchid_crop",
            GenericFlowerCropBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).noCollission().randomTicks().strength(0.0F)
                    .sound(SoundType.CROP));

    public static final DeferredBlock<Block> CORNFLOWER_CROP = registerBlock("cornflower_crop",
            GenericFlowerCropBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).noCollission().randomTicks().strength(0.0F)
                    .sound(SoundType.CROP));

//    public static final DeferredBlock<Block> EYEBLOSSOM_CROP = registerBlock("eyeblossom_crop",
//            EyeBlossomFlowerCropBlock::new,
//            BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).noCollission().randomTicks().strength(0.0F)
//                    .sound(SoundType.CROP));

    public static final DeferredBlock<Block> LILY_OF_THE_VALLEY_CROP = registerBlock("lily_of_the_valley_crop",
            GenericFlowerCropBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).noCollission().randomTicks().strength(0.0F)
                    .sound(SoundType.CROP));

    public static final DeferredBlock<Block> OXEYE_DAISY_CROP = registerBlock("oxeye_daisy_crop",
            GenericFlowerCropBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).noCollission().randomTicks().strength(0.0F)
                    .sound(SoundType.CROP));

    public static final DeferredBlock<Block> POPPY_CROP = registerBlock("poppy_crop",
            GenericFlowerCropBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).noCollission().randomTicks().strength(0.0F)
                    .sound(SoundType.CROP));

    public static final DeferredBlock<Block> ORANGE_TULIP_CROP = registerBlock("orange_tulip_crop",
            GenericFlowerCropBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).noCollission().randomTicks().strength(0.0F)
                    .sound(SoundType.CROP));

    public static final DeferredBlock<Block> PINK_TULIP_CROP = registerBlock("pink_tulip_crop",
            GenericFlowerCropBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).noCollission().randomTicks().strength(0.0F)
                    .sound(SoundType.CROP));

    public static final DeferredBlock<Block> RED_TULIP_CROP = registerBlock("red_tulip_crop",
            GenericFlowerCropBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).noCollission().randomTicks().strength(0.0F)
                    .sound(SoundType.CROP));

    public static final DeferredBlock<Block> WHITE_TULIP_CROP = registerBlock("white_tulip_crop",
            GenericFlowerCropBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).noCollission().randomTicks().strength(0.0F)
                    .sound(SoundType.CROP));



    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Function<BlockBehaviour.Properties, ? extends T> block, BlockBehaviour.Properties props) {
        DeferredBlock<T> toReturn = BLOCKS.registerBlock(name, block, props);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ITEMS.registerSimpleBlockItem(name, block);
    }

//    public static final DeferredBlock<Block> DANDELION_SEED = registerBlock("dandelion_seed", () -> new CustomCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).sound(SoundType.CROP)));
//
//    public static final DeferredBlock<Block> POPPY_SEED = registerBlock("poppy_seed", () -> new CustomCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).sound(SoundType.CROP)));
//
//    public static final DeferredBlock<Block> BLUE_ORCHID_SEED = registerBlock("blue_orchid_seed", () -> new CustomCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).sound(SoundType.CROP)));
//
//    public static final DeferredBlock<Block> ALLIUM_SEED = registerBlock("allium_seed", () -> new CustomCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).sound(SoundType.CROP)));
//
//    public static final DeferredBlock<Block> AZURE_BLUET_SEED = registerBlock("azure_bluet_seed", () -> new CustomCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).sound(SoundType.CROP)));
//
//    public static final DeferredBlock<Block> RED_TULIP_SEED = registerBlock("red_tulip_seed", () -> new CustomCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).sound(SoundType.CROP)));
//
//    public static final DeferredBlock<Block> ORANGE_TULIP_SEED = registerBlock("orange_tulip_seed", () -> new CustomCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).sound(SoundType.CROP)));
//
//    public static final DeferredBlock<Block> WHITE_TULIP_SEED = registerBlock("white_tulip_seed", () -> new CustomCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).sound(SoundType.CROP)));
//
//    public static final DeferredBlock<Block> PINK_TULIP_SEED = registerBlock("pink_tulip_seed", () -> new CustomCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).sound(SoundType.CROP)));
//
//    public static final DeferredBlock<Block> OXEYE_DAISY_SEED = registerBlock("oxeye_daisy_seed", () -> new CustomCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).sound(SoundType.CROP)));
//
//    public static final DeferredBlock<Block> CORNFLOWER_SEED = registerBlock("cornflower_seed", () -> new CustomCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).sound(SoundType.CROP)));
//
//    public static final DeferredBlock<Block> LILY_OF_THE_VALLEY_SEED = registerBlock("lily_of_the_valley_seed", () -> new CustomCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).sound(SoundType.CROP)));
//
//    public static final DeferredBlock<Block> WITHER_ROSE_SEED = registerBlock("wither_rose_seed", () -> new CustomCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).sound(SoundType.CROP)));
//
//
//
//

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }
}
