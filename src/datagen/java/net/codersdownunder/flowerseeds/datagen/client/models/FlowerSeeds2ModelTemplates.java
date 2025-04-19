package net.codersdownunder.flowerseeds.datagen.client.models;

import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public class FlowerSeeds2ModelTemplates {

    public static final ModelTemplate stage0 = new ModelTemplate(
            Optional.empty(),
            Optional.of("_stage0"),
            TextureSlot.CROSS,
            TextureSlot.PARTICLE
    ).extend().renderType("minecraft:cutout_mipped").build();

    public static final ModelTemplate stage0_internal = new ModelTemplate(
            Optional.of(ResourceLocation.parse("flowerseeds2:block/cross_stage0")),
            Optional.of("_stage0"),
            TextureSlot.CROSS,
            TextureSlot.PARTICLE
    ).extend().renderType("minecraft:cutout_mipped").build();

    public static final ModelTemplate stage1 = new ModelTemplate(
            Optional.empty(),
            Optional.of("_stage1"),
            TextureSlot.CROSS,
            TextureSlot.PARTICLE
    ).extend().renderType("minecraft:cutout_mipped").build();

    public static final ModelTemplate stage1_internal = new ModelTemplate(
            Optional.of(ResourceLocation.parse("flowerseeds2:block/cross_stage1")),
            Optional.of("_stage0"),
            TextureSlot.CROSS,
            TextureSlot.PARTICLE
    ).extend().renderType("minecraft:cutout_mipped").build();

    public static final ModelTemplate stage2 = new ModelTemplate(
            Optional.empty(),
            Optional.of("_stage2"),
            TextureSlot.CROSS,
            TextureSlot.PARTICLE
    ).extend().renderType("minecraft:cutout_mipped").build();

    public static final ModelTemplate stage2_internal = new ModelTemplate(
            Optional.of(ResourceLocation.parse("flowerseeds2:block/cross_stage2")),
            Optional.of("_stage0"),
            TextureSlot.CROSS,
            TextureSlot.PARTICLE
    ).extend().renderType("minecraft:cutout_mipped").build();

    public static final ModelTemplate stage3 = new ModelTemplate(
            Optional.empty(),
            Optional.of("_stage3"),
            TextureSlot.CROSS,
            TextureSlot.PARTICLE
    ).extend().renderType("minecraft:cutout_mipped").build();

    public static final ModelTemplate stage3_internal = new ModelTemplate(
            Optional.empty(),
            Optional.of("_stage3"),
            TextureSlot.CROSS,
            TextureSlot.PARTICLE
    ).extend().renderType("minecraft:cutout_mipped").build();

}
