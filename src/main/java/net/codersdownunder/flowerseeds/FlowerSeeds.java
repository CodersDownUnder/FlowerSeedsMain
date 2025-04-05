package net.codersdownunder.flowerseeds;

import com.mojang.logging.LogUtils;
import net.codersdownunder.flowerseeds.blocks.CustomCropBlock;
import net.codersdownunder.flowerseeds.init.BlockInit;
import net.codersdownunder.flowerseeds.init.CreativeTabInit;
import net.codersdownunder.flowerseeds.init.ItemInit;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.slf4j.Logger;

@SuppressWarnings("unused")
@Mod(FlowerSeeds.MODID)
public class FlowerSeeds {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "flowerseeds";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public FlowerSeeds(IEventBus modEventBus) {

        CreativeTabInit.register(modEventBus);

        ItemInit.register(modEventBus);
        BlockInit.register(modEventBus);

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        //NeoForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);

    }

    private void commonSetup(final FMLCommonSetupEvent event) {

        LogFilter.apply();

    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void registerItemColor(RegisterColorHandlersEvent.Item event) {

            for (DeferredHolder<Block, ? extends Block> block : BlockInit.BLOCKS.getEntries()) {
                CustomCropBlock item = (CustomCropBlock) block.get();
                event.register(item.getColour().get(), item.asItem());
            }
        }

    }

}
