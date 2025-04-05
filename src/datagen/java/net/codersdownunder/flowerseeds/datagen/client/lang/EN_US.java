package net.codersdownunder.flowerseeds.datagen.client.lang;

import net.codersdownunder.flowerseeds.FlowerSeeds;
import net.codersdownunder.flowerseeds.init.BlockInit;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class EN_US extends LanguageProvider {

    public EN_US(PackOutput gen) {

        super(gen, FlowerSeeds.MODID, "en_us");
    }

    @Override
    public String getName() {

        return "Flower Seeds 2 Main EN_US Lang Provider";
    }

    @Override
    protected void addTranslations() {

        add(BlockInit.DANDELION_SEED.get(), "Dandelion");
        add(BlockInit.DANDELION_SEED.get().asItem(), "Dandelion Seeds");
        add(BlockInit.WITHER_ROSE_SEED.get(), "Wither Rose");
        add(BlockInit.WITHER_ROSE_SEED.get().asItem(), "Wither Rose Seeds");
        add(BlockInit.OXEYE_DAISY_SEED.get(), "Oxeye Daisy");
        add(BlockInit.OXEYE_DAISY_SEED.get().asItem(), "Oxeye Daisy Seeds");
        add(BlockInit.POPPY_SEED.get(), "Poppy");
        add(BlockInit.POPPY_SEED.get().asItem(), "Poppy Seeds");
        add(BlockInit.PINK_TULIP_SEED.get(), "Pink Tulip");
        add(BlockInit.PINK_TULIP_SEED.get().asItem(), "Pink Tulip Seeds");
        add(BlockInit.RED_TULIP_SEED.get(), "Red Tulip");
        add(BlockInit.RED_TULIP_SEED.get().asItem(), "Red Tulip Seeds");
        add(BlockInit.WHITE_TULIP_SEED.get(), "White Tulip");
        add(BlockInit.WHITE_TULIP_SEED.get().asItem(), "White Tulip Seeds");
        add(BlockInit.ORANGE_TULIP_SEED.get(), "Orange Tulip");
        add(BlockInit.ORANGE_TULIP_SEED.get().asItem(), "Orange Tulip Seeds");
        add(BlockInit.LILY_OF_THE_VALLEY_SEED.get(), "Lily of the Valley");
        add(BlockInit.LILY_OF_THE_VALLEY_SEED.get().asItem(), "Lily of the Valley Seeds");
        add(BlockInit.BLUE_ORCHID_SEED.get(), "Blue Orchid");
        add(BlockInit.BLUE_ORCHID_SEED.get().asItem(), "Blue Orchid Seeds");
        add(BlockInit.CORNFLOWER_SEED.get(), "Cornflower");
        add(BlockInit.CORNFLOWER_SEED.get().asItem(), "Cornflower Seeds");
        add(BlockInit.AZURE_BLUET_SEED.get(), "Azure Bluet");
        add(BlockInit.AZURE_BLUET_SEED.get().asItem(), "Azure Bluet Seeds");
        add(BlockInit.ALLIUM_SEED.get(), "Allium");
        add(BlockInit.ALLIUM_SEED.get().asItem(), "Allium Seeds");

        add("flowerseeds.packmeta.description", "Flower Seeds 2 Resource pack");
        add("flowerseeds.creativetab.name", "Flower Seeds");
        add("tooltip.vanillaseed", "Vanilla");
        add("tooltip.aetherseed", "Aether");
        add("tooltip.bopseed", "Biomes O Plenty");
    }

}
