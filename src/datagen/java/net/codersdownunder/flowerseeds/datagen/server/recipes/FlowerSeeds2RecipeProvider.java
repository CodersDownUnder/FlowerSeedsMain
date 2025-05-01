package net.codersdownunder.flowerseeds.datagen.server.recipes;

import net.codersdownunder.flowerseeds2.data.FlowerSeedsRecipes;
import net.codersdownunder.flowerseeds2.init.CropRegistries;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class FlowerSeeds2RecipeProvider extends FlowerSeedsRecipes {

    public FlowerSeeds2RecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
            super(output, provider);
        }

    @Override
    protected void buildRecipes(RecipeOutput output) {

        flowerSeedRecipe(CropRegistries.DANDELION_CROP.get().asItem(), Items.DANDELION, output);
        flowerSeedRecipe(CropRegistries.ALLIUM_CROP.get().asItem(), Items.ALLIUM, output);
        flowerSeedRecipe(CropRegistries.AZURE_BLUET_CROP.get().asItem(), Items.AZURE_BLUET, output);
        flowerSeedRecipe(CropRegistries.CORNFLOWER_CROP.get().asItem(), Items.CORNFLOWER, output);
        flowerSeedRecipe(CropRegistries.BLUE_ORCHID_CROP.get().asItem(), Items.BLUE_ORCHID, output);
        flowerSeedRecipe(CropRegistries.LILY_OF_THE_VALLEY_CROP.get().asItem(), Items.LILY_OF_THE_VALLEY, output);
        flowerSeedRecipe(CropRegistries.ORANGE_TULIP_CROP.get().asItem(), Items.ORANGE_TULIP, output);
        flowerSeedRecipe(CropRegistries.PINK_TULIP_CROP.get().asItem(), Items.PINK_TULIP, output);
        flowerSeedRecipe(CropRegistries.POPPY_CROP.get().asItem(), Items.POPPY, output);
        flowerSeedRecipe(CropRegistries.RED_TULIP_CROP.get().asItem(), Items.RED_TULIP, output);
        flowerSeedRecipe(CropRegistries.OXEYE_DAISY_CROP.get().asItem(), Items.OXEYE_DAISY, output);
        flowerSeedRecipe(CropRegistries.WHITE_TULIP_CROP.get().asItem(), Items.WHITE_TULIP, output);
        flowerSeedRecipe(CropRegistries.WITHER_ROSE_CROP.get().asItem(), Items.WITHER_ROSE, output);
    }

}
