package net.codersdownunder.flowerseeds.data;

import com.github.minecraftschurlimods.easydatagenlib.CompatDataProvider;
import com.github.minecraftschurlimods.easydatagenlib.util.botanypots.DisplayState;
import com.github.minecraftschurlimods.easydatagenlib.util.immersiveengineering.ClocheRenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

public abstract class FlowerSeedsCompat extends CompatDataProvider
{

    protected FlowerSeedsCompat(String namespace, GatherDataEvent event) {

        super(namespace, event);
    }

    protected void basicFlowerProcessing(@NotNull Block seed, Item flower) {

        Ingredient ingredient = Ingredient.of(seed.asItem());

        BOTANY_POTS_CROP.builder(toName(seed.asItem()), ingredient, 1400)
                .addCategory("dirt")
                .addCategory("farmland")
                .addDisplay(new DisplayState.Aging(seed))
                .addOutput(flower)
                .addOutput(seed.asItem()).build();

        THERMAL_INSOLATING.builder(toName(seed.asItem()))
                .addInput(ingredient)
                .addOutputItem(flower, 2, 4f)
                .addOutputItem(seed.asItem(), 1, 2f).build();

        IMMERSIVE_ENGINEERING_CLOCHE.builder(toName(seed.asItem()), 240, ingredient, Ingredient.of(Blocks.DIRT), ClocheRenderType.CROP, blockId(seed))
                .addOutput(Ingredient.of(flower))
                .addOutput(ingredient).build();
        MEKANISM_CRUSHING.builder(toName(seed.asItem()), ingredient, BIO_FUEL, 2).build();

        MEKANISM_CRUSHING.builder(toName(Blocks.AIR), Ingredient.of(Blocks.AIR.asItem()), new ResourceLocation("minecraft:air"), 0);

//        THERMAL_INSOLATING.builder(toName(Items.AIR))
//                .addInput(Ingredient.EMPTY)
//                .addOutputItem(Items.AIR, 2, 4f)
//                .addOutputItem(Items.AIR, 1, 2f);
    }

    protected void basicWitherFlowerProcessing(@NotNull Block seed, Item flower) {

        Ingredient ingredient = Ingredient.of(seed.asItem());

        BOTANY_POTS_CROP.builder(toName(seed.asItem()), ingredient, 1200)
                .addCategory("soul_sand")
                .addCategory("netherrack")
                .addCategory("dirt")
                .addCategory("farmland")
                .addDisplay(new DisplayState.Aging(seed))
                .addOutput(flower)
                .addOutput(seed.asItem()).build();

        THERMAL_INSOLATING.builder(toName(seed.asItem()))
                .addInput(ingredient)
                .addOutputItem(flower, 2, 4f)
                .addOutputItem(seed.asItem(), 1, 2f).build();

        IMMERSIVE_ENGINEERING_CLOCHE.builder(toName(seed.asItem()), 240, ingredient, Ingredient.of(Blocks.SOUL_SAND), ClocheRenderType.CROP, blockId(seed))
                .addOutput(Ingredient.of(flower))
                .addOutput(ingredient).build();

        MEKANISM_CRUSHING.builder(toName(seed.asItem()), ingredient, BIO_FUEL, 2).build();

        MEKANISM_CRUSHING.builder(toName(Blocks.AIR), Ingredient.of(Blocks.AIR.asItem()), new ResourceLocation("minecraft:air"), 0);

//        THERMAL_INSOLATING.builder(toName(Items.AIR))
//                .addInput(Ingredient.EMPTY)
//                .addOutputItem(Items.AIR, 2, 4f)
//                .addOutputItem(Items.AIR, 1, 2f);
    }

    protected void basicNetherFlowerProcessing(@NotNull Block seed, Item flower) {

        Ingredient ingredient = Ingredient.of(seed.asItem());

        BOTANY_POTS_CROP.builder(toName(seed.asItem()), ingredient, 1200)
                .addCategory("netherrack")
                .addCategory("nether")
                .addDisplay(new DisplayState.Aging(seed))
                .addOutput(flower)
                .addOutput(seed.asItem()).build();

        THERMAL_INSOLATING.builder(toName(seed.asItem()))
                .addInput(ingredient)
                .addOutputItem(flower, 2, 4f)
                .addOutputItem(seed.asItem(), 1, 2f).build();

        IMMERSIVE_ENGINEERING_CLOCHE.builder(toName(seed.asItem()), 240, ingredient, Ingredient.of(Blocks.NETHERRACK), ClocheRenderType.CROP, blockId(seed))
                .addOutput(Ingredient.of(flower))
                .addOutput(ingredient).build();

        MEKANISM_CRUSHING.builder(toName(seed.asItem()), ingredient, BIO_FUEL, 2).build();

        MEKANISM_CRUSHING.builder(toName(Blocks.AIR), Ingredient.of(Blocks.AIR.asItem()), new ResourceLocation("minecraft:air"), 0);

//        THERMAL_INSOLATING.builder(toName(Items.AIR))
//                .addInput(Ingredient.EMPTY)
//                .addOutputItem(Items.AIR, 2, 4f)
//                .addOutputItem(Items.AIR, 1, 2f);
    }
}
