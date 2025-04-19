package net.codersdownunder.flowerseeds2.events;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.codersdownunder.flowerseeds2.Config;
import net.codersdownunder.flowerseeds2.FlowerSeeds2;
import net.codersdownunder.flowerseeds2.init.CropRegistries;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;

@EventBusSubscriber(modid = FlowerSeeds2.MODID)
public class VillagerTradesEventHandler {

    @SubscribeEvent
    public static void onVillagerTrades(VillagerTradesEvent pEvent) {

        if (pEvent.getType().equals(VillagerProfession.FARMER)) {
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = pEvent.getTrades();
            int villagerLevel = 1;

            for (DeferredHolder<Block, ? extends Block> block : CropRegistries.BLOCKS.getEntries()) {
                trades.get(villagerLevel).add((trader, rand) -> addTrade(block.get()));
            }

            if (Config.allowWitherRoseTrading) {
                trades.get(Config.witherRoseTradeLevel).add((trader, rand) -> addTrade(CropRegistries.WITHER_ROSE_CROP.get()));
            }
        }

        if (pEvent.getType().equals(VillagerProfession.CLERIC)) {
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = pEvent.getTrades();

            if (Config.allowWitherRoseTradingCleric) {
                trades.get(Config.witherRoseTradeLevelCleric).add((trader, rand) -> addTrade(CropRegistries.WITHER_ROSE_CROP.get()));
            }
        }
    }

    /**
     * Add default trade of 1 emerald for 1 seed
     *
     * @param seed a seed item
     * @return {@link MerchantOffer}
     */
    public static MerchantOffer addTrade(ItemLike seed) {

        ItemStack stack = new ItemStack(seed, 1);

        return new MerchantOffer(
                new ItemCost(Items.EMERALD, 1),
                stack, 10, 8, 0.02F);
    }

    /**
     * Add trade for x number of emeralds for 1 seed
     *
     * @param seed   a seed item
     * @param amount amount of emeralds for trade
     * @return {@link MerchantOffer}
     */
    public static MerchantOffer addTrade(ItemLike seed, int amount) {

        ItemStack stack = new ItemStack(seed, 1);
        int villagerLevel = 1;

        return new MerchantOffer(
                new ItemCost(Items.EMERALD, amount),
                stack, 10, 8, 0.02F);
    }

}
