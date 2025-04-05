package net.codersdownunder.flowerseeds.datagen.client.models;

import net.codersdownunder.flowerseeds.init.BlockInit;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

public class MainItemModelProvider extends ItemModelProvider {

    private static String MODID = "";

    public MainItemModelProvider(PackOutput output, String MODID, ExistingFileHelper existingFileHelper) {

        super(output, MODID, existingFileHelper);
        MainItemModelProvider.MODID = MODID;
    }

    @Override
    public String getName() {

        return "Flower Seeds 2 Main Item Model Provider";
    }

    @Override
    protected void registerModels() {

        oneLayerItem(BlockInit.DANDELION_SEED.get().asItem());
        oneLayerItem(BlockInit.WITHER_ROSE_SEED.get().asItem());
        oneLayerItem(BlockInit.DANDELION_SEED.get().asItem());
        oneLayerItem(BlockInit.WHITE_TULIP_SEED.get().asItem());
        oneLayerItem(BlockInit.OXEYE_DAISY_SEED.get().asItem());
        oneLayerItem(BlockInit.RED_TULIP_SEED.get().asItem());
        oneLayerItem(BlockInit.POPPY_SEED.get().asItem());
        oneLayerItem(BlockInit.PINK_TULIP_SEED.get().asItem());
        oneLayerItem(BlockInit.ORANGE_TULIP_SEED.get().asItem());
        oneLayerItem(BlockInit.LILY_OF_THE_VALLEY_SEED.get().asItem());
        oneLayerItem(BlockInit.BLUE_ORCHID_SEED.get().asItem());
        oneLayerItem(BlockInit.CORNFLOWER_SEED.get().asItem());
        oneLayerItem(BlockInit.AZURE_BLUET_SEED.get().asItem());
        oneLayerItem(BlockInit.ALLIUM_SEED.get().asItem());
    }

    private void oneLayerItem(final Item item) {

        withExistingParent(BuiltInRegistries.ITEM.getKey(item.asItem()).getPath(), "item/generated")
                .texture("layer0", modLoc("item/seed"));
    }

    private ItemModelBuilder simpleItem(DeferredHolder<Item, ? extends Item> item) {

        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(MODID, "item/" + item.getId().getPath()));
    }

}
