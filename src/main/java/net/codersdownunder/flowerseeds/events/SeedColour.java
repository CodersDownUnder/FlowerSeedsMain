package net.codersdownunder.flowerseeds.events;

import net.minecraft.client.color.item.ItemColor;

public enum SeedColour {
    RED(0xB02E26),
    YELLOW(0xFED83D),
    LIGHT_BLUE(0x3AB3DA),
    MAGENTA(0xC74EBD),
    LIGHT_GREY(0x9D9D97),
    ORANGE(0xF9801D),
    PINK(0xF38BAA),
    BLUE(0x3C44AA),
    WHITE(0xffffff),
    BLACK(0x1D1D21),
    CYAN(0x158991),
    LIME(0x80C71F),
    PURPLE(0x8932B8),
    BROWN(0x835432),

    GREY(0x474F52);

    private final int colour;

    SeedColour(int colour) {

        this.colour = colour;
    }

    public ItemColor get() {

        return (stack, tintIndex) -> colour;
    }

}
