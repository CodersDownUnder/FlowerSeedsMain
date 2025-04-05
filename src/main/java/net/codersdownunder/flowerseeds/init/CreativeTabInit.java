package net.codersdownunder.flowerseeds.init;

import net.codersdownunder.flowerseeds.FlowerSeeds;
import net.codersdownunder.flowerseeds.blocks.CustomCropBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CreativeTabInit {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, FlowerSeeds.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> FLOWER_SEEDS_TAB = CREATIVE_MODE_TABS.register("flower_seeds_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(BlockInit.DANDELION_SEED.get()))
                    .title(Component.translatable("flowerseeds.creativetab.name"))
                    .withSearchBar()
                    .displayItems((pParameters, pOutput) -> {
                        for (DeferredHolder<Block, ? extends Block> block : BlockInit.BLOCKS.getEntries()) {
                            pOutput.accept(block.get());
                        }

                    })
                    .build());

    public static void register(IEventBus eventBus) {

        CREATIVE_MODE_TABS.register(eventBus);
    }

}
