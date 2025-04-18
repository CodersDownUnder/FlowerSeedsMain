package net.minecraft.world.item;

import net.minecraft.resources.ResourceLocation;

public class HorseArmorItem extends Item {
    private static final String TEX_FOLDER = "textures/entity/horse/";
    private final int protection;
    private final ResourceLocation texture;

    public HorseArmorItem(int p_41364_, String p_41365_, Item.Properties p_41366_) {
        this(p_41364_, new ResourceLocation("textures/entity/horse/armor/horse_armor_" + p_41365_ + ".png"), p_41366_);
    }

    public HorseArmorItem(int p_41364_, ResourceLocation p_41365_, Item.Properties p_41366_) {
        super(p_41366_);
        this.protection = p_41364_;
        this.texture = p_41365_;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public int getProtection() {
        return this.protection;
    }
}
