package com.mojang.realmsclient.gui.screens;

import javax.annotation.Nullable;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsParentalConsentScreen extends RealmsScreen {
    private static final Component MESSAGE = Component.translatable("mco.account.privacy.information");
    private static final int SPACING = 15;
    private final LinearLayout layout = LinearLayout.vertical();
    private final Screen lastScreen;
    @Nullable
    private MultiLineTextWidget textWidget;

    public RealmsParentalConsentScreen(Screen p_88861_) {
        super(GameNarrator.NO_TITLE);
        this.lastScreen = p_88861_;
    }

    @Override
    public void init() {
        this.layout.spacing(15).defaultCellSetting().alignHorizontallyCenter();
        this.textWidget = new MultiLineTextWidget(MESSAGE, this.font).setCentered(true);
        this.layout.addChild(this.textWidget);
        LinearLayout linearlayout = this.layout.addChild(LinearLayout.horizontal().spacing(8));
        Component component = Component.translatable("mco.account.privacy.info.button");
        linearlayout.addChild(Button.builder(component, ConfirmLinkScreen.confirmLink(this, "https://aka.ms/MinecraftGDPR")).build());
        linearlayout.addChild(Button.builder(CommonComponents.GUI_BACK, p_307025_ -> this.onClose()).build());
        this.layout.visitWidgets(this::addRenderableWidget);
        this.repositionElements();
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    protected void repositionElements() {
        if (this.textWidget != null) {
            this.textWidget.setMaxWidth(this.width - 15);
        }

        this.layout.arrangeElements();
        FrameLayout.centerInRectangle(this.layout, this.getRectangle());
    }

    @Override
    public Component getNarrationMessage() {
        return MESSAGE;
    }
}
