package net.codersdownunder.flowerseeds.datagen.client.models;

import net.codersdownunder.flowerseeds2.init.CropRegistries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

public class FlowerSeeds2ItemModelProvider extends ItemModelProvider {

    private static String MODID = "";

    public FlowerSeeds2ItemModelProvider(PackOutput output, String MODID, ExistingFileHelper existingFileHelper) {

        super(output, MODID, existingFileHelper);
        FlowerSeeds2ItemModelProvider.MODID = MODID;
    }

    @Override
    public String getName() {

        return "Flower Seeds 2 Main Item Model Provider";
    }

    @Override
    protected void registerModels() {

        oneLayerItem(CropRegistries.DANDELION_CROP.get().asItem());
        oneLayerItem(CropRegistries.WITHER_ROSE_CROP.get().asItem());
        oneLayerItem(CropRegistries.DANDELION_CROP.get().asItem());
        oneLayerItem(CropRegistries.WHITE_TULIP_CROP.get().asItem());
        oneLayerItem(CropRegistries.OXEYE_DAISY_CROP.get().asItem());
        oneLayerItem(CropRegistries.RED_TULIP_CROP.get().asItem());
        oneLayerItem(CropRegistries.POPPY_CROP.get().asItem());
        oneLayerItem(CropRegistries.PINK_TULIP_CROP.get().asItem());
        oneLayerItem(CropRegistries.ORANGE_TULIP_CROP.get().asItem());
        oneLayerItem(CropRegistries.LILY_OF_THE_VALLEY_CROP.get().asItem());
        oneLayerItem(CropRegistries.BLUE_ORCHID_CROP.get().asItem());
        oneLayerItem(CropRegistries.CORNFLOWER_CROP.get().asItem());
        oneLayerItem(CropRegistries.AZURE_BLUET_CROP.get().asItem());
        oneLayerItem(CropRegistries.ALLIUM_CROP.get().asItem());
    }

    private void oneLayerItem(final Item item) {

        withExistingParent(BuiltInRegistries.ITEM.getKey(item.asItem()).getPath(), "item/generated")
                .texture("layer0", modLoc("item/seed"));
    }

    private ItemModelBuilder simpleItem(DeferredHolder<Item, ? extends Item> item) {

        return withExistingParent(item.getId().getPath(),
                ResourceLocation.parse("item/generated")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(MODID, "item/" + item.getId().getPath()));
    }

}