package net.minecraft.world.item;

import java.util.function.Supplier;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.item.crafting.Ingredient;

public enum Tiers implements Tier {
    WOOD(0, 59, 2.0F, 0.0F, 15, () -> Ingredient.of(ItemTags.PLANKS)),
    STONE(1, 131, 4.0F, 1.0F, 5, () -> Ingredient.of(ItemTags.STONE_TOOL_MATERIALS)),
    IRON(2, 250, 6.0F, 2.0F, 14, () -> Ingredient.of(Items.IRON_INGOT)),
    DIAMOND(3, 1561, 8.0F, 3.0F, 10, () -> Ingredient.of(Items.DIAMOND)),
    GOLD(0, 32, 12.0F, 0.0F, 22, () -> Ingredient.of(Items.GOLD_INGOT)),
    NETHERITE(4, 2031, 9.0F, 4.0F, 15, () -> Ingredient.of(Items.NETHERITE_INGOT));

    private final int level;
    private final int uses;
    private final float speed;
    private final float damage;
    private final int enchantmentValue;
    private final LazyLoadedValue<Ingredient> repairIngredient;

    private Tiers(int p_43332_, int p_43333_, float p_43334_, float p_43335_, int p_43336_, Supplier<Ingredient> p_43337_) {
        this.level = p_43332_;
        this.uses = p_43333_;
        this.speed = p_43334_;
        this.damage = p_43335_;
        this.enchantmentValue = p_43336_;
        this.repairIngredient = new LazyLoadedValue<>(p_43337_);
    }

    @Override
    public int getUses() {
        return this.uses;
    }

    @Override
    public float getSpeed() {
        return this.speed;
    }

    @Override
    public float getAttackDamageBonus() {
        return this.damage;
    }

    @Override
    public int getLevel() {
        return this.level;
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }

    @org.jetbrains.annotations.Nullable public net.minecraft.tags.TagKey<net.minecraft.world.level.block.Block> getTag() { return net.neoforged.neoforge.common.CommonHooks.getTagFromVanillaTier(this); }
}
