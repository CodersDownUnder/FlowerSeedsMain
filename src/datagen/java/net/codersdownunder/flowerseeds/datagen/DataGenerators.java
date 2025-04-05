package net.codersdownunder.flowerseeds.datagen;

import net.codersdownunder.flowerseeds.FlowerSeeds;
import net.codersdownunder.flowerseeds.datagen.client.lang.EN_US;
import net.codersdownunder.flowerseeds.datagen.client.lang.JA_JP;
import net.codersdownunder.flowerseeds.datagen.client.models.MainBlockStateProvider;
import net.codersdownunder.flowerseeds.datagen.client.models.MainItemModelProvider;
import net.codersdownunder.flowerseeds.datagen.server.compat.MainCompatDataProvider;
import net.codersdownunder.flowerseeds.datagen.server.datamaps.DataMapDataProvider;
import net.codersdownunder.flowerseeds.datagen.server.loot.MainLootTableProvider;
import net.codersdownunder.flowerseeds.datagen.server.recipes.MainRecipeProvider;
import net.codersdownunder.flowerseeds.datagen.server.tags.MainBlockTagProvider;
import net.codersdownunder.flowerseeds.datagen.server.tags.MainItemTagProvider;
import net.minecraft.DetectedVersion;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.OverlayMetadataSection;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.util.InclusiveRange;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = FlowerSeeds.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {

        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(true, new PackMetadataGenerator(packOutput)
                .add(OverlayMetadataSection.TYPE, new OverlayMetadataSection(List.of(
                        new OverlayMetadataSection.OverlayEntry(new InclusiveRange<>(0, Integer.MAX_VALUE), "pack_overlays_test"))))
                .add(PackMetadataSection.TYPE, new PackMetadataSection(
                        Component.translatable("flowerseeds.packmeta.description"),
                        DetectedVersion.BUILT_IN.getPackVersion(PackType.CLIENT_RESOURCES),
                        Optional.of(new InclusiveRange<>(0, Integer.MAX_VALUE)))));

        /*
            Lang files Start
         */
        generator.addProvider(event.includeClient(), new EN_US(packOutput));
        generator.addProvider(event.includeClient(), new JA_JP(packOutput));

        /*
            Lang files end
         */

        ItemModelProvider itemModels = new MainItemModelProvider(packOutput, FlowerSeeds.MODID, event.getExistingFileHelper());
        generator.addProvider(event.includeClient(), itemModels);
        generator.addProvider(event.includeClient(), new MainBlockStateProvider(packOutput, FlowerSeeds.MODID, itemModels.existingFileHelper));

        generator.addProvider(event.includeServer(), MainLootTableProvider.create(packOutput));
        generator.addProvider(event.includeServer(), new MainRecipeProvider(packOutput));

        MainBlockTagProvider blockTagGenerator = generator.addProvider(event.includeServer(),
                new MainBlockTagProvider(packOutput, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new MainItemTagProvider(packOutput, lookupProvider, blockTagGenerator.contentsGetter(), existingFileHelper));

        generator.addProvider(event.includeServer(), new MainCompatDataProvider(FlowerSeeds.MODID, event));
        new MainCompatDataProvider(FlowerSeeds.MODID, event);

        generator.addProvider(event.includeServer(), new DataMapDataProvider(packOutput, lookupProvider));

    }

}
