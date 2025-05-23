package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.SalmonModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Salmon;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SalmonRenderer extends MobRenderer<Salmon, SalmonModel<Salmon>> {
    private static final ResourceLocation SALMON_LOCATION = new ResourceLocation("textures/entity/fish/salmon.png");

    public SalmonRenderer(EntityRendererProvider.Context p_174364_) {
        super(p_174364_, new SalmonModel<>(p_174364_.bakeLayer(ModelLayers.SALMON)), 0.4F);
    }

    public ResourceLocation getTextureLocation(Salmon p_115826_) {
        return SALMON_LOCATION;
    }

    protected void setupRotations(Salmon p_115828_, PoseStack p_115829_, float p_115830_, float p_115831_, float p_115832_) {
        super.setupRotations(p_115828_, p_115829_, p_115830_, p_115831_, p_115832_);
        float f = 1.0F;
        float f1 = 1.0F;
        if (!p_115828_.isInWater()) {
            f = 1.3F;
            f1 = 1.7F;
        }

        float f2 = f * 4.3F * Mth.sin(f1 * 0.6F * p_115830_);
        p_115829_.mulPose(Axis.YP.rotationDegrees(f2));
        p_115829_.translate(0.0F, 0.0F, -0.4F);
        if (!p_115828_.isInWater()) {
            p_115829_.translate(0.2F, 0.1F, 0.0F);
            p_115829_.mulPose(Axis.ZP.rotationDegrees(90.0F));
        }
    }
}
