package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public record BonusLevelTableCondition(Holder<Enchantment> enchantment, List<Float> values) implements LootItemCondition {
    public static final Codec<BonusLevelTableCondition> CODEC = RecordCodecBuilder.create(
        p_298172_ -> p_298172_.group(
                    BuiltInRegistries.ENCHANTMENT.holderByNameCodec().fieldOf("enchantment").forGetter(BonusLevelTableCondition::enchantment),
                    Codec.FLOAT.listOf().fieldOf("chances").forGetter(BonusLevelTableCondition::values)
                )
                .apply(p_298172_, BonusLevelTableCondition::new)
    );

    @Override
    public LootItemConditionType getType() {
        return LootItemConditions.TABLE_BONUS;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParams.TOOL);
    }

    public boolean test(LootContext p_81521_) {
        ItemStack itemstack = p_81521_.getParamOrNull(LootContextParams.TOOL);
        int i = itemstack != null ? EnchantmentHelper.getItemEnchantmentLevel(this.enchantment.value(), itemstack) : 0;
        float f = this.values.get(Math.min(i, this.values.size() - 1));
        return p_81521_.getRandom().nextFloat() < f;
    }

    public static LootItemCondition.Builder bonusLevelFlatChance(Enchantment p_81518_, float... p_81519_) {
        List<Float> list = new ArrayList<>(p_81519_.length);

        for(float f : p_81519_) {
            list.add(f);
        }

        return () -> new BonusLevelTableCondition(p_81518_.builtInRegistryHolder(), list);
    }
}
