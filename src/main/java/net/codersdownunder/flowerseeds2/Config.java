package net.codersdownunder.flowerseeds2;


import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs

//TODO: Use Translatable thingymabobs
@EventBusSubscriber(modid = FlowerSeeds2.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue WITHER_ROSE_TRADING = BUILDER
            .comment("Allow Wither Rose seeds to be obtainable from Farmers?")
            .define("witherrosetrading", false);

    private static final ModConfigSpec.IntValue WITHER_ROSE_TRADE_LEVEL = BUILDER
            .comment("Wither Rose Farmer Trade Level")
            .defineInRange("witherrosettradelevel", 5, 1, 5);

    private static final ModConfigSpec.BooleanValue WITHER_ROSE_TRADING_CLERIC = BUILDER
            .comment("Allow Wither Rose seeds to be obtainable from master Clerics?")
            .define("witherrosetradingcleric", false);

    private static final ModConfigSpec.IntValue WITHER_ROSE_TRADE_LEVEL_CLERIC = BUILDER
            .comment("Wither Rose Cleric Trade Level")
            .defineInRange("witherrosettradelevelcleric", 5, 1, 5);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean allowWitherRoseTrading;
    public static boolean allowWitherRoseTradingCleric;

    public static int witherRoseTradeLevel;
    public static int witherRoseTradeLevelCleric;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {

        allowWitherRoseTrading = WITHER_ROSE_TRADING.get();
        allowWitherRoseTradingCleric = WITHER_ROSE_TRADING_CLERIC.get();

        witherRoseTradeLevel = WITHER_ROSE_TRADE_LEVEL.get();
        witherRoseTradeLevelCleric = WITHER_ROSE_TRADE_LEVEL_CLERIC.get();

    }

}
