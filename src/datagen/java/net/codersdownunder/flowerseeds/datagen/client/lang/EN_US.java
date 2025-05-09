package net.codersdownunder.flowerseeds.datagen.client.lang;

import net.codersdownunder.flowerseeds2.FlowerSeeds2;
import net.codersdownunder.flowerseeds2.init.CropRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class EN_US extends LanguageProvider {

    public EN_US(PackOutput gen) {

        super(gen, FlowerSeeds2.MODID, "en_us");
    }

    @Override
    public String getName() {

        return "Flower Seeds 2 Main EN_US Lang Provider";
    }

    @Override
    protected void addTranslations() {

        addTranslation(CropRegistries.DANDELION_CROP.get(), "Dandelion");
        addTranslation(CropRegistries.WITHER_ROSE_CROP.get(), "Wither Rose");
        addTranslation(CropRegistries.OXEYE_DAISY_CROP.get(), "Oxeye Daisy");
        addTranslation(CropRegistries.POPPY_CROP.get(), "Poppy");
        addTranslation(CropRegistries.PINK_TULIP_CROP.get(), "Pink Tulip");
        addTranslation(CropRegistries.RED_TULIP_CROP.get(), "Red Tulip");
        addTranslation(CropRegistries.WHITE_TULIP_CROP.get(), "White Tulip");
        addTranslation(CropRegistries.ORANGE_TULIP_CROP.get(), "Orange Tulip");
        addTranslation(CropRegistries.LILY_OF_THE_VALLEY_CROP.get(), "Lily of the Valley");
        addTranslation(CropRegistries.BLUE_ORCHID_CROP.get(), "Blue Orchid");
        addTranslation(CropRegistries.CORNFLOWER_CROP.get(), "Cornflower");
        addTranslation(CropRegistries.AZURE_BLUET_CROP.get(), "Azure Bluet");
        addTranslation(CropRegistries.ALLIUM_CROP.get(), "Allium");
        //add(CropRegistries.EYEBLOSSOM_CROP.get(), "Eyeblossom Seeds");

        add("flowerseeds2.packmeta.description", "Flower Seeds 2 Resource pack");
        add("itemGroup.flowerseeds2", "Flower Seeds 2");
        add("tooltip.vanillaseed", "Vanilla");
        add("tooltip.aetherseed", "Aether");
        add("tooltip.bopseed", "Biomes O Plenty");
        add("tooltip.otbwg", "Oh The Biomes We've Gone");

        add("flowerseeds2.configuration.witherrosetrading", "Allow Wither Rose seeds to be obtainable from Farmers?");
        add("flowerseeds2.configuration.witherrosetradelevel", "Wither Rose Farmer Trade Level");
        add("flowerseeds2.configuration.witherrosetradingcleric", "Allow Wither Rose seeds to be obtainable from master Clerics?");
        add("flowerseeds2.configuration.witherrosetradelevelcleric", "Wither Rose Cleric Trade Level");
    }


    public void addTranslation(Block block, String blockName) {
        add(block, blockName);
        add(block.asItem().getDefaultInstance(), blockName + " Seeds");
    }
}
