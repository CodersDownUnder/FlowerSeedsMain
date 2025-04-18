package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.DrownedModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.DrownedOuterLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Zombie;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DrownedRenderer extends AbstractZombieRenderer<Drowned, DrownedModel<Drowned>> {
    private static final ResourceLocation DROWNED_LOCATION = new ResourceLocation("textures/entity/zombie/drowned.png");

    public DrownedRenderer(EntityRendererProvider.Context p_173964_) {
        super(
            p_173964_,
            new DrownedModel<>(p_173964_.bakeLayer(ModelLayers.DROWNED)),
            new DrownedModel<>(p_173964_.bakeLayer(ModelLayers.DROWNED_INNER_ARMOR)),
            new DrownedModel<>(p_173964_.bakeLayer(ModelLayers.DROWNED_OUTER_ARMOR))
        );
        this.addLayer(new DrownedOuterLayer<>(this, p_173964_.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(Zombie p_114115_) {
        return DROWNED_LOCATION;
    }

    protected void setupRotations(Drowned p_114109_, PoseStack p_114110_, float p_114111_, float p_114112_, float p_114113_) {
        super.setupRotations(p_114109_, p_114110_, p_114111_, p_114112_, p_114113_);
        float f = p_114109_.getSwimAmount(p_114113_);
        if (f > 0.0F) {
            float f1 = -10.0F - p_114109_.getXRot();
            float f2 = Mth.lerp(f, 0.0F, f1);
            p_114110_.rotateAround(Axis.XP.rotationDegrees(f2), 0.0F, p_114109_.getBbHeight() / 2.0F, 0.0F);
        }
    }
}
