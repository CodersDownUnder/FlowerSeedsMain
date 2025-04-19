package net.codersdownunder.flowerseeds.datagen.server.recipes;

import net.codersdownunder.flowerseeds2.init.CropRegistries;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.NeoForgeConditions;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

import static net.neoforged.neoforge.common.conditions.NeoForgeConditions.modLoaded;

public class FlowerSeeds2RecipeProvider extends RecipeProvider {

        // Construct the provider to run
        protected FlowerSeeds2RecipeProvider(HolderLookup.Provider provider, RecipeOutput output) {
            super(provider, output);
        }

        @Override
        protected void buildRecipes() {
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

    protected void flowerSeedRecipe(Item flowerSeed, Item flower, RecipeOutput pFinishedRecipeConsumer) {

        ShapelessRecipeBuilder.shapeless(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, flowerSeed, 2)
                .requires(flower)
                .requires(Tags.Items.SEEDS)
                .unlockedBy(getHasName(flower), has(flower))
                .save(pFinishedRecipeConsumer);
    }

    public void flowerSeedRecipe(Item flowerSeed, Item flower, RecipeOutput pFinishedRecipeConsumer, String modid) {

        ShapelessRecipeBuilder.shapeless(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, flowerSeed, 2)
                .requires(flower)
                .requires(Tags.Items.SEEDS)
                .unlockedBy(getHasName(flower), has(flower))
                .save(pFinishedRecipeConsumer.withConditions(
                                NeoForgeConditions.and(
                                        NeoForgeConditions.not(modLoaded(modid)))),
                        getHasName(flowerSeed));

    }

        // The runner to add to the data generator
        public static class Runner extends RecipeProvider.Runner {
            public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
                super(packOutput, registries);
            }

            @Override
            protected @NotNull RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
                return new FlowerSeeds2RecipeProvider(registries, output);
            }

            @Override
            public String getName() {
                return "FlowerSeeds2 Recipes";
            }
        }
}
