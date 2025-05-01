package net.codersdownunder.flowerseeds2.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.concurrent.CompletableFuture;

public abstract class FlowerSeedsRecipes extends RecipeProvider implements IConditionBuilder {

    public FlowerSeedsRecipes(PackOutput p_248933_, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(p_248933_, lookupProvider);
    }
    protected static void flowerSeedRecipe(Item flowerSeed, Item flower, RecipeOutput pFinishedRecipeConsumer) {

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, flowerSeed, 2)
                .requires(flower)
                .requires(Tags.Items.SEEDS)
                .unlockedBy(getHasName(flower), has(flower))
                .save(pFinishedRecipeConsumer);
    }

    public void flowerSeedRecipe(Item flowerSeed, Item flower, RecipeOutput pFinishedRecipeConsumer, String modid) {

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, flowerSeed, 2)
                .requires(flower)
                .requires(Tags.Items.SEEDS)
                .unlockedBy(getHasName(flower), has(flower))
                .save(pFinishedRecipeConsumer.withConditions(
                                and(
                                        not(modLoaded(modid)),
                                        FALSE())),
                        ResourceLocation.parse(getHasName(flowerSeed)));

    }
}