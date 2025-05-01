package net.codersdownunder.flowerseeds.datagen.client.models;

import net.codersdownunder.flowerseeds2.FlowerSeeds2;
import net.codersdownunder.flowerseeds2.data.FlowerSeedsBlockStates;
import net.codersdownunder.flowerseeds2.init.CropRegistries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class FlowerSeeds2ModelProvider extends FlowerSeedsBlockStates {


    private final String MODID;

    public FlowerSeeds2ModelProvider (PackOutput output, String MODID, ExistingFileHelper exFileHelper) {

        super(output, MODID, exFileHelper);
        this.MODID = MODID;
    }


    @Override
    public String getName() {

        return "Flower Seeds 2 Main BlockState Provider";
    }

    @Override
    protected void registerStatesAndModels() {

        flowerModel(CropRegistries.DANDELION_CROP.get(), "block/dandelion", "dandelion");
        flowerModel(CropRegistries.ALLIUM_CROP.get(), "block/allium", "allium");
        flowerModel(CropRegistries.AZURE_BLUET_CROP.get(), "block/azure_bluet", "azure_bluet");
        flowerModel(CropRegistries.CORNFLOWER_CROP.get(), "block/cornflower", "cornflower");
        flowerModel(CropRegistries.BLUE_ORCHID_CROP.get(), "block/blue_orchid", "blue_orchid");
        flowerModel(CropRegistries.LILY_OF_THE_VALLEY_CROP.get(), "block/lily_of_the_valley", "lily_of_the_valley");
        flowerModel(CropRegistries.ORANGE_TULIP_CROP.get(), "block/orange_tulip", "orange_tulip");
        flowerModel(CropRegistries.PINK_TULIP_CROP.get(), "block/pink_tulip", "pink_tulip");
        flowerModel(CropRegistries.POPPY_CROP.get(), "block/poppy", "poppy");
        flowerModel(CropRegistries.RED_TULIP_CROP.get(), "block/red_tulip", "red_tulip");
        flowerModel(CropRegistries.OXEYE_DAISY_CROP.get(), "block/oxeye_daisy", "oxeye_daisy");
        flowerModel(CropRegistries.WHITE_TULIP_CROP.get(), "block/white_tulip", "white_tulip");
        flowerModel(CropRegistries.WITHER_ROSE_CROP.get(), "block/wither_rose", "wither_rose");
    }



}