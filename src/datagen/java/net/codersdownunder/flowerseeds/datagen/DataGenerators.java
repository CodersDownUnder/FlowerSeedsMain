package net.codersdownunder.flowerseeds.datagen;

import net.codersdownunder.flowerseeds.datagen.client.lang.EN_US;
import net.codersdownunder.flowerseeds.datagen.client.models.FlowerSeeds2ModelProvider;
import net.codersdownunder.flowerseeds.datagen.server.datamaps.FlowerSeeds2DataMapProvider;
import net.codersdownunder.flowerseeds.datagen.server.loot.FlowerSeeds2LootProvider;
import net.codersdownunder.flowerseeds.datagen.server.recipes.FlowerSeeds2RecipeProvider;
import net.codersdownunder.flowerseeds.datagen.server.tags.FlowerSeeds2BlockTagsProvider;
import net.codersdownunder.flowerseeds.datagen.server.tags.FlowerSeeds2ItemTagsProvider;
import net.codersdownunder.flowerseeds2.FlowerSeeds2;
import net.minecraft.DetectedVersion;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.OverlayMetadataSection;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = FlowerSeeds2.MODID, bus = EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {

        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        //ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(true, new PackMetadataGenerator(packOutput)
                .add(OverlayMetadataSection.TYPE, new OverlayMetadataSection(List.of(
                        new OverlayMetadataSection.OverlayEntry(new InclusiveRange<>(0, Integer.MAX_VALUE), "pack_overlays_test"))))
                .add(PackMetadataSection.TYPE, new PackMetadataSection(
                        Component.translatable("flowerseeds2.packmeta.description"),
                        DetectedVersion.BUILT_IN.getPackVersion(PackType.CLIENT_RESOURCES),
                        Optional.of(new InclusiveRange<>(0, Integer.MAX_VALUE)))));

        /*
            Lang files Start
         */
        generator.addProvider(true, new EN_US(packOutput));

        /*
            Lang files end
         */

        /*
        Models Start
         */

        generator.addProvider(true, new FlowerSeeds2ModelProvider(packOutput));

        /*
        Models End
         */

        /*
        Tags Start
         */
        FlowerSeeds2BlockTagsProvider blockTagGenerator = generator.addProvider(true,
                new FlowerSeeds2BlockTagsProvider(packOutput, lookupProvider));
        generator.addProvider(true, new FlowerSeeds2ItemTagsProvider(packOutput, lookupProvider, blockTagGenerator.contentsGetter()));
        /*
        Tags End
         */

        /*
        DataMaps Start
         */
        generator.addProvider(true, new FlowerSeeds2DataMapProvider(packOutput, lookupProvider));
        /*
        DataMaps End
         */

        /*
        Loot Tables Start
         */

        generator.addProvider(true, new FlowerSeeds2LootProvider(packOutput, lookupProvider));

        /*
        Loot Tables End
         */

        /*
        Recipes Start
         */
        generator.addProvider(true, new FlowerSeeds2RecipeProvider.Runner(packOutput, lookupProvider));
        /*
        Recipes End
         */

        //ItemModelProvider itemModels = new MainItemModelProvider(packOutput, FlowerSeeds.MODID, event.getExistingFileHelper());
        //generator.addProvider(event.includeClient(), itemModels);
        //generator.addProvider(event.includeClient(), new MainBlockStateProvider(packOutput, FlowerSeeds.MODID, itemModels.existingFileHelper));

        //generator.addProvider(event.includeServer(), new LootTableProvider(packOutput, Collections.emptySet(),
        //        List.of(new LootTableProvider.SubProviderEntry(MainBlockLootTables::new, LootContextParamSets.BLOCK)), lookupProvider));
        //generator.addProvider(event.includeServer(), new MainRecipeProvider(packOutput, lookupProvider));

        //MainBlockTagProvider blockTagGenerator = generator.addProvider(event.includeServer(),
        //        new MainBlockTagProvider(packOutput, lookupProvider, existingFileHelper));
        //generator.addProvider(event.includeServer(), new MainItemTagProvider(packOutput, lookupProvider, blockTagGenerator.contentsGetter(), existingFileHelper));

//        generator.addProvider(event.includeServer(), new MainCompatDataProvider(FlowerSeeds.MODID, event));
//        new MainCompatDataProvider(FlowerSeeds.MODID, event);

        //generator.addProvider(event.includeServer(), new DataMapDataProvider(packOutput, lookupProvider));

    }

}