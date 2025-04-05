package net.codersdownunder.flowerseeds.init;

import net.codersdownunder.flowerseeds.FlowerSeeds;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemInit {

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(FlowerSeeds.MODID);

//    public static final RegistryObject<Item> flowerseed = ITEMS.register("flowerseed",
//            () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus) {

        ITEMS.register(eventBus);
    }

}
