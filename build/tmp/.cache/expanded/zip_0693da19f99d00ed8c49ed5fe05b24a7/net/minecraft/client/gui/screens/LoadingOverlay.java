package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LoadingOverlay extends Overlay {
    static final ResourceLocation MOJANG_STUDIOS_LOGO_LOCATION = new ResourceLocation("textures/gui/title/mojangstudios.png");
    private static final int LOGO_BACKGROUND_COLOR = FastColor.ARGB32.color(255, 239, 50, 61);
    private static final int LOGO_BACKGROUND_COLOR_DARK = FastColor.ARGB32.color(255, 0, 0, 0);
    private static final IntSupplier BRAND_BACKGROUND = () -> Minecraft.getInstance().options.darkMojangStudiosBackground().get()
            ? LOGO_BACKGROUND_COLOR_DARK
            : LOGO_BACKGROUND_COLOR;
    private static final int LOGO_SCALE = 240;
    private static final float LOGO_QUARTER_FLOAT = 60.0F;
    private static final int LOGO_QUARTER = 60;
    private static final int LOGO_HALF = 120;
    private static final float LOGO_OVERLAP = 0.0625F;
    private static final float SMOOTHING = 0.95F;
    public static final long FADE_OUT_TIME = 1000L;
    public static final long FADE_IN_TIME = 500L;
    private final Minecraft minecraft;
    private final ReloadInstance reload;
    private final Consumer<Optional<Throwable>> onFinish;
    private final boolean fadeIn;
    private float currentProgress;
    private long fadeOutStart = -1L;
    private long fadeInStart = -1L;

    public LoadingOverlay(Minecraft p_96172_, ReloadInstance p_96173_, Consumer<Optional<Throwable>> p_96174_, boolean p_96175_) {
        this.minecraft = p_96172_;
        this.reload = p_96173_;
        this.onFinish = p_96174_;
        this.fadeIn = p_96175_;
    }

    public static void registerTextures(Minecraft p_96190_) {
        p_96190_.getTextureManager().register(MOJANG_STUDIOS_LOGO_LOCATION, new LoadingOverlay.LogoTexture());
    }

    private static int replaceAlpha(int p_169325_, int p_169326_) {
        return p_169325_ & 16777215 | p_169326_ << 24;
    }

    @Override
    public void render(GuiGraphics p_281839_, int p_282704_, int p_283650_, float p_283394_) {
        int i = p_281839_.guiWidth();
        int j = p_281839_.guiHeight();
        long k = Util.getMillis();
        if (this.fadeIn && this.fadeInStart == -1L) {
            this.fadeInStart = k;
        }

        float f = this.fadeOutStart > -1L ? (float)(k - this.fadeOutStart) / 1000.0F : -1.0F;
        float f1 = this.fadeInStart > -1L ? (float)(k - this.fadeInStart) / 500.0F : -1.0F;
        float f2;
        if (f >= 1.0F) {
            if (this.minecraft.screen != null) {
                this.minecraft.screen.render(p_281839_, 0, 0, p_283394_);
            }

            int l = Mth.ceil((1.0F - Mth.clamp(f - 1.0F, 0.0F, 1.0F)) * 255.0F);
            p_281839_.fill(RenderType.guiOverlay(), 0, 0, i, j, replaceAlpha(BRAND_BACKGROUND.getAsInt(), l));
            f2 = 1.0F - Mth.clamp(f - 1.0F, 0.0F, 1.0F);
        } else if (this.fadeIn) {
            if (this.minecraft.screen != null && f1 < 1.0F) {
                this.minecraft.screen.render(p_281839_, p_282704_, p_283650_, p_283394_);
            }

            int l1 = Mth.ceil(Mth.clamp((double)f1, 0.15, 1.0) * 255.0);
            p_281839_.fill(RenderType.guiOverlay(), 0, 0, i, j, replaceAlpha(BRAND_BACKGROUND.getAsInt(), l1));
            f2 = Mth.clamp(f1, 0.0F, 1.0F);
        } else {
            int i2 = BRAND_BACKGROUND.getAsInt();
            float f3 = (float)(i2 >> 16 & 0xFF) / 255.0F;
            float f4 = (float)(i2 >> 8 & 0xFF) / 255.0F;
            float f5 = (float)(i2 & 0xFF) / 255.0F;
            GlStateManager._clearColor(f3, f4, f5, 1.0F);
            GlStateManager._clear(16384, Minecraft.ON_OSX);
            f2 = 1.0F;
        }

        int j2 = (int)((double)p_281839_.guiWidth() * 0.5);
        int k2 = (int)((double)p_281839_.guiHeight() * 0.5);
        double d1 = Math.min((double)p_281839_.guiWidth() * 0.75, (double)p_281839_.guiHeight()) * 0.25;
        int i1 = (int)(d1 * 0.5);
        double d0 = d1 * 4.0;
        int j1 = (int)(d0 * 0.5);
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(770, 1);
        p_281839_.setColor(1.0F, 1.0F, 1.0F, f2);
        p_281839_.blit(MOJANG_STUDIOS_LOGO_LOCATION, j2 - j1, k2 - i1, j1, (int)d1, -0.0625F, 0.0F, 120, 60, 120, 120);
        p_281839_.blit(MOJANG_STUDIOS_LOGO_LOCATION, j2, k2 - i1, j1, (int)d1, 0.0625F, 60.0F, 120, 60, 120, 120);
        p_281839_.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        int k1 = (int)((double)p_281839_.guiHeight() * 0.8325);
        float f6 = this.reload.getActualProgress();
        this.currentProgress = Mth.clamp(this.currentProgress * 0.95F + f6 * 0.050000012F, 0.0F, 1.0F);
        if (f < 1.0F) {
            this.drawProgressBar(p_281839_, i / 2 - j1, k1 - 5, i / 2 + j1, k1 + 5, 1.0F - Mth.clamp(f, 0.0F, 1.0F));
        }

        if (f >= 2.0F) {
            this.minecraft.setOverlay(null);
        }

        if (this.fadeOutStart == -1L && this.reload.isDone() && (!this.fadeIn || f1 >= 2.0F)) {
            this.fadeOutStart = Util.getMillis(); // Moved up to guard against inf loops caused by callback
            try {
                this.reload.checkExceptions();
                this.onFinish.accept(Optional.empty());
            } catch (Throwable throwable) {
                this.onFinish.accept(Optional.of(throwable));
            }

            if (this.minecraft.screen != null) {
                this.minecraft.screen.init(this.minecraft, p_281839_.guiWidth(), p_281839_.guiHeight());
            }
        }
    }

    private void drawProgressBar(GuiGraphics p_283125_, int p_96184_, int p_96185_, int p_96186_, int p_96187_, float p_96188_) {
        int i = Mth.ceil((float)(p_96186_ - p_96184_ - 2) * this.currentProgress);
        int j = Math.round(p_96188_ * 255.0F);
        int k = FastColor.ARGB32.color(j, 255, 255, 255);
        p_283125_.fill(p_96184_ + 2, p_96185_ + 2, p_96184_ + i, p_96187_ - 2, k);
        p_283125_.fill(p_96184_ + 1, p_96185_, p_96186_ - 1, p_96185_ + 1, k);
        p_283125_.fill(p_96184_ + 1, p_96187_, p_96186_ - 1, p_96187_ - 1, k);
        p_283125_.fill(p_96184_, p_96185_, p_96184_ + 1, p_96187_, k);
        p_283125_.fill(p_96186_, p_96185_, p_96186_ - 1, p_96187_, k);
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    static class LogoTexture extends SimpleTexture {
        public LogoTexture() {
            super(LoadingOverlay.MOJANG_STUDIOS_LOGO_LOCATION);
        }

        @Override
        protected SimpleTexture.TextureImage getTextureImage(ResourceManager p_96194_) {
            VanillaPackResources vanillapackresources = Minecraft.getInstance().getVanillaPackResources();
            IoSupplier<InputStream> iosupplier = vanillapackresources.getResource(PackType.CLIENT_RESOURCES, LoadingOverlay.MOJANG_STUDIOS_LOGO_LOCATION);
            if (iosupplier == null) {
                return new SimpleTexture.TextureImage(new FileNotFoundException(LoadingOverlay.MOJANG_STUDIOS_LOGO_LOCATION.toString()));
            } else {
                try {
                    SimpleTexture.TextureImage simpletexture$textureimage;
                    try (InputStream inputstream = iosupplier.get()) {
                        simpletexture$textureimage = new SimpleTexture.TextureImage(new TextureMetadataSection(true, true), NativeImage.read(inputstream));
                    }

                    return simpletexture$textureimage;
                } catch (IOException ioexception) {
                    return new SimpleTexture.TextureImage(ioexception);
                }
            }
        }
    }
}
