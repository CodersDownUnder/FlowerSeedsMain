package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Tesselator {
    private static final int MAX_BYTES = 786432;
    private final BufferBuilder builder;
    @Nullable
    private static Tesselator instance;

    public static void init() {
        RenderSystem.assertOnGameThreadOrInit();
        if (instance != null) {
            throw new IllegalStateException("Tesselator has already been initialized");
        } else {
            instance = new Tesselator();
        }
    }

    public static Tesselator getInstance() {
        RenderSystem.assertOnGameThreadOrInit();
        if (instance == null) {
            throw new IllegalStateException("Tesselator has not been initialized");
        } else {
            return instance;
        }
    }

    public Tesselator(int p_85912_) {
        this.builder = new BufferBuilder(p_85912_);
    }

    public Tesselator() {
        this(786432);
    }

    public void end() {
        BufferUploader.drawWithShader(this.builder.end());
    }

    public BufferBuilder getBuilder() {
        return this.builder;
    }
}
