package net.codersdownunder.flowerseeds.datagen.client.models;

import net.codersdownunder.flowerseeds.FlowerSeeds;
import net.codersdownunder.flowerseeds.data.FlowerSeedsBlockStates;
import net.codersdownunder.flowerseeds.init.BlockInit;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class MainBlockStateProvider extends FlowerSeedsBlockStates {

    public MainBlockStateProvider(PackOutput output, String MODID, ExistingFileHelper exFileHelper) {

        super(output, MODID, exFileHelper);

    }

    private final String MODID = FlowerSeeds.MODID;



    @Override
    public String getName() {

        return "Flower Seeds 2 Main BlockState Provider";
    }

    @Override
    protected void registerStatesAndModels() {

        flowerModel(BlockInit.DANDELION_SEED.get(), "block/dandelion", "dandelion");
        flowerModel(BlockInit.ALLIUM_SEED.get(), "block/allium", "allium");
        flowerModel(BlockInit.AZURE_BLUET_SEED.get(), "block/azure_bluet", "azure_bluet");
        flowerModel(BlockInit.CORNFLOWER_SEED.get(), "block/cornflower", "cornflower");
        flowerModel(BlockInit.BLUE_ORCHID_SEED.get(), "block/blue_orchid", "blue_orchid");
        flowerModel(BlockInit.LILY_OF_THE_VALLEY_SEED.get(), "block/lily_of_the_valley", "lily_of_the_valley");
        flowerModel(BlockInit.ORANGE_TULIP_SEED.get(), "block/orange_tulip", "orange_tulip");
        flowerModel(BlockInit.PINK_TULIP_SEED.get(), "block/pink_tulip", "pink_tulip");
        flowerModel(BlockInit.POPPY_SEED.get(), "block/poppy", "poppy");
        flowerModel(BlockInit.RED_TULIP_SEED.get(), "block/red_tulip", "red_tulip");
        flowerModel(BlockInit.OXEYE_DAISY_SEED.get(), "block/oxeye_daisy", "oxeye_daisy");
        flowerModel(BlockInit.WHITE_TULIP_SEED.get(), "block/white_tulip", "white_tulip");
        flowerModel(BlockInit.WITHER_ROSE_SEED.get(), "block/wither_rose", "wither_rose");
    }



}
