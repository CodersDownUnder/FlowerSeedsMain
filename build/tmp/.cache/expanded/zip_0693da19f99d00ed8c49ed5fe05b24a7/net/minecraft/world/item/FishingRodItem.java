package net.minecraft.world.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class FishingRodItem extends Item implements Vanishable {
    public FishingRodItem(Item.Properties p_41285_) {
        super(p_41285_);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level p_41290_, Player p_41291_, InteractionHand p_41292_) {
        ItemStack itemstack = p_41291_.getItemInHand(p_41292_);
        if (p_41291_.fishing != null) {
            if (!p_41290_.isClientSide) {
                int i = p_41291_.fishing.retrieve(itemstack);
                ItemStack original = itemstack.copy();
                itemstack.hurtAndBreak(i, p_41291_, p_41288_ -> p_41288_.broadcastBreakEvent(p_41292_));
                if(itemstack.isEmpty()) {
                    net.neoforged.neoforge.event.EventHooks.onPlayerDestroyItem(p_41291_, original, p_41292_);
                }
            }

            p_41290_.playSound(
                null,
                p_41291_.getX(),
                p_41291_.getY(),
                p_41291_.getZ(),
                SoundEvents.FISHING_BOBBER_RETRIEVE,
                SoundSource.NEUTRAL,
                1.0F,
                0.4F / (p_41290_.getRandom().nextFloat() * 0.4F + 0.8F)
            );
            p_41291_.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
        } else {
            p_41290_.playSound(
                null,
                p_41291_.getX(),
                p_41291_.getY(),
                p_41291_.getZ(),
                SoundEvents.FISHING_BOBBER_THROW,
                SoundSource.NEUTRAL,
                0.5F,
                0.4F / (p_41290_.getRandom().nextFloat() * 0.4F + 0.8F)
            );
            if (!p_41290_.isClientSide) {
                int k = EnchantmentHelper.getFishingSpeedBonus(itemstack);
                int j = EnchantmentHelper.getFishingLuckBonus(itemstack);
                p_41290_.addFreshEntity(new FishingHook(p_41291_, p_41290_, j, k));
            }

            p_41291_.awardStat(Stats.ITEM_USED.get(this));
            p_41291_.gameEvent(GameEvent.ITEM_INTERACT_START);
        }

        return InteractionResultHolder.sidedSuccess(itemstack, p_41290_.isClientSide());
    }

    @Override
    public int getEnchantmentValue() {
        return 1;
    }

     /* ******************** FORGE START ******************** */

     @Override
    public boolean canPerformAction(ItemStack stack, net.neoforged.neoforge.common.ToolAction toolAction) {
        return net.neoforged.neoforge.common.ToolActions.DEFAULT_FISHING_ROD_ACTIONS.contains(toolAction);
    }
}
