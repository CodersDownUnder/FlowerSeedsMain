package net.codersdownunder.flowerseeds.datagen.server.compat;

import com.github.minecraftschurlimods.easydatagenlib.CompatDataProvider;
import com.github.minecraftschurlimods.easydatagenlib.util.botanypots.DisplayState;
import com.github.minecraftschurlimods.easydatagenlib.util.immersiveengineering.ClocheRenderType;
import net.codersdownunder.flowerseeds.data.FlowerSeedsCompat;
import net.codersdownunder.flowerseeds.init.BlockInit;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class MainCompatDataProvider extends FlowerSeedsCompat {

    public MainCompatDataProvider(String modid, GatherDataEvent event) {

        super(modid, event);
    }

    @Override
    protected @NotNull CompletableFuture<?> generate() {

        basicFlowerProcessing(BlockInit.DANDELION_SEED.get(), Items.DANDELION);
        basicFlowerProcessing(BlockInit.ALLIUM_SEED.get(), Items.ALLIUM);
        basicFlowerProcessing(BlockInit.AZURE_BLUET_SEED.get(), Items.AZURE_BLUET);
        basicFlowerProcessing(BlockInit.CORNFLOWER_SEED.get(), Items.CORNFLOWER);
        basicFlowerProcessing(BlockInit.BLUE_ORCHID_SEED.get(), Items.BLUE_ORCHID);
        basicFlowerProcessing(BlockInit.LILY_OF_THE_VALLEY_SEED.get(), Items.LILY_OF_THE_VALLEY);
        basicFlowerProcessing(BlockInit.ORANGE_TULIP_SEED.get(), Items.ORANGE_TULIP);
        basicFlowerProcessing(BlockInit.PINK_TULIP_SEED.get(), Items.PINK_TULIP);
        basicFlowerProcessing(BlockInit.POPPY_SEED.get(), Items.POPPY);
        basicFlowerProcessing(BlockInit.RED_TULIP_SEED.get(), Items.RED_TULIP);
        basicFlowerProcessing(BlockInit.OXEYE_DAISY_SEED.get(), Items.OXEYE_DAISY);
        basicFlowerProcessing(BlockInit.WHITE_TULIP_SEED.get(), Items.WHITE_TULIP);
        basicWitherFlowerProcessing(BlockInit.WITHER_ROSE_SEED.get(), Items.WITHER_ROSE);
        //This is here to force it to generate all the files, otherwise the last few files don't get generated for some reason or other.
        //TODO: fix this in future (somehow?)
        //MEKANISM_CRUSHING.builder(toName(Blocks.AIR), Ingredient.of(Blocks.AIR.asItem()), new ResourceLocation("minecraft:air"), 0);

        //THERMAL_INSOLATING.builder(toName(Items.AIR))
        //.addInput(Ingredient.EMPTY)
        //.addOutputItem(Items.AIR, 2, 4f)
        //.addOutputItem(Items.AIR, 1, 2f);

        return CompletableFuture.completedFuture(null);
    }

    @Override
    public String getName() {

        return "Flower Seeds 2 Compat data Provider";
    }

}
