package net.codersdownunder.flowerseeds.init;

import net.codersdownunder.flowerseeds.FlowerSeeds;
import net.codersdownunder.flowerseeds.blocks.CustomCropBlock;
import net.codersdownunder.flowerseeds.events.SeedColour;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BlockInit {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(FlowerSeeds.MODID);

    public static final DeferredBlock<Block> DANDELION_SEED = registerBlock("dandelion_seed", () -> new CustomCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).sound(SoundType.CROP), SeedColour.YELLOW));

    public static final DeferredBlock<Block> POPPY_SEED = registerBlock("poppy_seed", () -> new CustomCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).sound(SoundType.CROP), SeedColour.RED));

    public static final DeferredBlock<Block> BLUE_ORCHID_SEED = registerBlock("blue_orchid_seed", () -> new CustomCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).sound(SoundType.CROP), SeedColour.LIGHT_BLUE));

    public static final DeferredBlock<Block> ALLIUM_SEED = registerBlock("allium_seed", () -> new CustomCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).sound(SoundType.CROP), SeedColour.MAGENTA));

    public static final DeferredBlock<Block> AZURE_BLUET_SEED = registerBlock("azure_bluet_seed", () -> new CustomCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).sound(SoundType.CROP), SeedColour.LIGHT_GREY));

    public static final DeferredBlock<Block> RED_TULIP_SEED = registerBlock("red_tulip_seed", () -> new CustomCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).sound(SoundType.CROP), SeedColour.RED));

    public static final DeferredBlock<Block> ORANGE_TULIP_SEED = registerBlock("orange_tulip_seed", () -> new CustomCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).sound(SoundType.CROP), SeedColour.ORANGE));

    public static final DeferredBlock<Block> WHITE_TULIP_SEED = registerBlock("white_tulip_seed", () -> new CustomCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).sound(SoundType.CROP), SeedColour.LIGHT_GREY));

    public static final DeferredBlock<Block> PINK_TULIP_SEED = registerBlock("pink_tulip_seed", () -> new CustomCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).sound(SoundType.CROP), SeedColour.PINK));

    public static final DeferredBlock<Block> OXEYE_DAISY_SEED = registerBlock("oxeye_daisy_seed", () -> new CustomCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).sound(SoundType.CROP), SeedColour.LIGHT_GREY));

    public static final DeferredBlock<Block> CORNFLOWER_SEED = registerBlock("cornflower_seed", () -> new CustomCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).sound(SoundType.CROP), SeedColour.BLUE));

    public static final DeferredBlock<Block> LILY_OF_THE_VALLEY_SEED = registerBlock("lily_of_the_valley_seed", () -> new CustomCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).sound(SoundType.CROP), SeedColour.WHITE));

    public static final DeferredBlock<Block> WITHER_ROSE_SEED = registerBlock("wither_rose_seed", () -> new CustomCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).sound(SoundType.CROP), SeedColour.BLACK));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {

        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {

        ItemInit.ITEMS.register(name, () -> new ItemNameBlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {

        BLOCKS.register(eventBus);
    }

}
