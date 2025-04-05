package net.codersdownunder.flowerseeds.datagen.client.lang;

import net.codersdownunder.flowerseeds.FlowerSeeds;
import net.codersdownunder.flowerseeds.init.BlockInit;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

/*
 *  Credits to https://github.com/RascalNiki for the JA_JP translations
 */

public class JA_JP extends LanguageProvider {

    public JA_JP(PackOutput gen) {

        super(gen, FlowerSeeds.MODID, "ja_jp");
    }

    @Override
    public String getName() {

        return "Flower Seeds 2 Main JA_JP Lang Provider";
    }

    @Override
    protected void addTranslations() {

        add(BlockInit.DANDELION_SEED.get(), "タンポポ");
        add(BlockInit.DANDELION_SEED.get().asItem(), "タンポポの種");
        add(BlockInit.WITHER_ROSE_SEED.get(), "ウィザーローズ");
        add(BlockInit.WITHER_ROSE_SEED.get().asItem(), "ウィザーローズの種");
        add(BlockInit.OXEYE_DAISY_SEED.get(), "フランスギク");
        add(BlockInit.OXEYE_DAISY_SEED.get().asItem(), "フランスギクの種");
        add(BlockInit.POPPY_SEED.get(), "ポピー");
        add(BlockInit.POPPY_SEED.get().asItem(), "ポピーの種");
        add(BlockInit.PINK_TULIP_SEED.get(), "桃色のチューリップ");
        add(BlockInit.PINK_TULIP_SEED.get().asItem(), "桃色のチューリップの種");
        add(BlockInit.RED_TULIP_SEED.get(), "赤色のチューリップ");
        add(BlockInit.RED_TULIP_SEED.get().asItem(), "赤色のチューリップの種");
        add(BlockInit.WHITE_TULIP_SEED.get(), "白色のチューリップ");
        add(BlockInit.WHITE_TULIP_SEED.get().asItem(), "白色のチューリップの種");
        add(BlockInit.ORANGE_TULIP_SEED.get(), "橙色のチューリップ");
        add(BlockInit.ORANGE_TULIP_SEED.get().asItem(), "橙色のチューリップの種");
        add(BlockInit.LILY_OF_THE_VALLEY_SEED.get(), "スズラン");
        add(BlockInit.LILY_OF_THE_VALLEY_SEED.get().asItem(), "スズランの種");
        add(BlockInit.BLUE_ORCHID_SEED.get(), "ヒスイラン");
        add(BlockInit.BLUE_ORCHID_SEED.get().asItem(), "ヒスイランの種");
        add(BlockInit.CORNFLOWER_SEED.get(), "ヤグルマギク");
        add(BlockInit.CORNFLOWER_SEED.get().asItem(), "ヤグルマギクの種");
        add(BlockInit.AZURE_BLUET_SEED.get(), "ヒナソウ");
        add(BlockInit.AZURE_BLUET_SEED.get().asItem(), "ヒナソウの種");
        add(BlockInit.ALLIUM_SEED.get(), "アリウム");
        add(BlockInit.ALLIUM_SEED.get().asItem(), "アリウムの種");

        add("flowerseeds.packmeta.description", "Flower Seeds 2のリソースとデータパック");
        add("flowerseeds.creativetab.name", "Flower Seeds");
    }

}
