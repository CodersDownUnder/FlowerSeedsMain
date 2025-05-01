package net.codersdownunder.flowerseeds2.init;

import net.codersdownunder.flowerseeds2.FlowerSeeds2;
import net.codersdownunder.flowerseeds2.blocks.GenericFlowerCropBlock;
import net.codersdownunder.flowerseeds2.util.SeedColour;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

public class CropRegistries {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(FlowerSeeds2.MODID);

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(FlowerSeeds2.MODID);

//    public static final DeferredBlock<Block> DANDELION_CROP = registerBlock("dandelion_crop",
//            GenericFlowerCropBlock::new,
//            BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).noCollission().randomTicks().strength(0.0F)
//            .sound(SoundType.CROP));
//
//    public static final DeferredBlock<Block> WITHER_ROSE_CROP = registerBlock("wither_rose_crop",
//            GenericFlowerCropBlock::new,
//            BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).noCollission().randomTicks().strength(0.0F)
//                    .sound(SoundType.CROP));
//
//    public static final DeferredBlock<Block> ALLIUM_CROP = registerBlock("allium_crop",
//            GenericFlowerCropBlock::new,
//            BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).noCollission().randomTicks().strength(0.0F)
//                    .sound(SoundType.CROP));
//
//    public static final DeferredBlock<Block> AZURE_BLUET_CROP = registerBlock("azure_bluet_crop",
//            GenericFlowerCropBlock::new,
//            BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).noCollission().randomTicks().strength(0.0F)
//                    .sound(SoundType.CROP));
//
//    public static final DeferredBlock<Block> BLUE_ORCHID_CROP = registerBlock("blue_orchid_crop",
//            GenericFlowerCropBlock::new,
//            BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).noCollission().randomTicks().strength(0.0F)
//                    .sound(SoundType.CROP));
//
//    public static final DeferredBlock<Block> CORNFLOWER_CROP = registerBlock("cornflower_crop",
//            GenericFlowerCropBlock::new,
//            BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).noCollission().randomTicks().strength(0.0F)
//                    .sound(SoundType.CROP));
//
////    public static final DeferredBlock<Block> EYEBLOSSOM_CROP = registerBlock("eyeblossom_crop",
////            EyeBlossomFlowerCropBlock::new,
////            BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).noCollission().randomTicks().strength(0.0F)
////                    .sound(SoundType.CROP));
//
//    public static final DeferredBlock<Block> LILY_OF_THE_VALLEY_CROP = registerBlock("lily_of_the_valley_crop",
//            GenericFlowerCropBlock::new,
//            BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).noCollission().randomTicks().strength(0.0F)
//                    .sound(SoundType.CROP));
//
//    public static final DeferredBlock<Block> OXEYE_DAISY_CROP = registerBlock("oxeye_daisy_crop",
//            GenericFlowerCropBlock::new,
//            BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).noCollission().randomTicks().strength(0.0F)
//                    .sound(SoundType.CROP));
//
//    public static final DeferredBlock<Block> POPPY_CROP = registerBlock("poppy_crop",
//            GenericFlowerCropBlock::new,
//            BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).noCollission().randomTicks().strength(0.0F)
//                    .sound(SoundType.CROP));
//
//    public static final DeferredBlock<Block> ORANGE_TULIP_CROP = registerBlock("orange_tulip_crop",
//            GenericFlowerCropBlock::new,
//            BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).noCollission().randomTicks().strength(0.0F)
//                    .sound(SoundType.CROP));
//
//    public static final DeferredBlock<Block> PINK_TULIP_CROP = registerBlock("pink_tulip_crop",
//            GenericFlowerCropBlock::new,
//            BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).noCollission().randomTicks().strength(0.0F)
//                    .sound(SoundType.CROP));
//
//    public static final DeferredBlock<Block> RED_TULIP_CROP = registerBlock("red_tulip_crop",
//            GenericFlowerCropBlock::new,
//            BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).noCollission().randomTicks().strength(0.0F)
//                    .sound(SoundType.CROP));
//
//    public static final DeferredBlock<Block> WHITE_TULIP_CROP = registerBlock("white_tulip_crop",
//            GenericFlowerCropBlock::new,
//            BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).noCollission().randomTicks().strength(0.0F)
//                    .sound(SoundType.CROP));


    public static final DeferredBlock<Block> DANDELION_CROP = registerBlock("dandelion_crop", () -> new GenericFlowerCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).sound(SoundType.CROP), SeedColour.YELLOW));

    public static final DeferredBlock<Block> POPPY_CROP = registerBlock("poppy_crop", () -> new GenericFlowerCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).sound(SoundType.CROP), SeedColour.RED));

    public static final DeferredBlock<Block> BLUE_ORCHID_CROP = registerBlock("blue_orchid_crop", () -> new GenericFlowerCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).sound(SoundType.CROP), SeedColour.LIGHT_BLUE));

    public static final DeferredBlock<Block> ALLIUM_CROP = registerBlock("allium_crop", () -> new GenericFlowerCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).sound(SoundType.CROP), SeedColour.MAGENTA));

    public static final DeferredBlock<Block> AZURE_BLUET_CROP = registerBlock("azure_bluet_crop", () -> new GenericFlowerCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).sound(SoundType.CROP), SeedColour.LIGHT_GREY));

    public static final DeferredBlock<Block> RED_TULIP_CROP = registerBlock("red_tulip_crop", () -> new GenericFlowerCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).sound(SoundType.CROP), SeedColour.RED));

    public static final DeferredBlock<Block> ORANGE_TULIP_CROP = registerBlock("orange_tulip_crop", () -> new GenericFlowerCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).sound(SoundType.CROP), SeedColour.ORANGE));

    public static final DeferredBlock<Block> WHITE_TULIP_CROP = registerBlock("white_tulip_crop", () -> new GenericFlowerCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).sound(SoundType.CROP), SeedColour.LIGHT_GREY));

    public static final DeferredBlock<Block> PINK_TULIP_CROP = registerBlock("pink_tulip_crop", () -> new GenericFlowerCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).sound(SoundType.CROP), SeedColour.PINK));

    public static final DeferredBlock<Block> OXEYE_DAISY_CROP = registerBlock("oxeye_daisy_crop", () -> new GenericFlowerCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).sound(SoundType.CROP), SeedColour.LIGHT_GREY));

    public static final DeferredBlock<Block> CORNFLOWER_CROP = registerBlock("cornflower_crop", () -> new GenericFlowerCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).sound(SoundType.CROP), SeedColour.BLUE));

    public static final DeferredBlock<Block> LILY_OF_THE_VALLEY_CROP = registerBlock("lily_of_the_valley_crop", () -> new GenericFlowerCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).sound(SoundType.CROP), SeedColour.WHITE));

    public static final DeferredBlock<Block> WITHER_ROSE_CROP = registerBlock("wither_rose_crop", () -> new GenericFlowerCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).sound(SoundType.CROP), SeedColour.BLACK));


    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {

        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {

        ITEMS.register(name, () -> new ItemNameBlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }
}
