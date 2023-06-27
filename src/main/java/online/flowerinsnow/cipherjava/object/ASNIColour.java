package online.flowerinsnow.cipherjava.object;

import org.jetbrains.annotations.NotNull;

public enum ASNIColour {
    RESET("<c:reset>", "\u001B[0m", false),
    BLACK("<f:black>", "\u001B[30m", false),
    RED("<f:red>", "\u001B[31m", false),
    GREEN("<f:green>", "\u001B[32m", false),
    YELLOW("<f:yellow>", "\u001B[33m", false),
    BLUE("<f:blue>", "\u001B[34m", false),
    PURPLE("<f:purple>", "\u001B[35m", false),
    CYAN("<f:cyan>", "\u001B[36m", false),
    WHITE("<f:white>", "\u001B[37m", false),
    BG_BLACK("<b:black>", "\u001B[40m", true),
    BG_RED("<b:red>", "\u001B[41m", true),
    BG_GREEN("<b:green>", "\u001B[42m", true),
    BG_YELLOW("<b:yellow>", "\u001B[43m", true),
    BG_BLUE("<b:blue>", "\u001B[44m", true),
    BG_PURPLE("<b:purple>", "\u001B[45m", true),
    BG_CYAN("<b:cyan>", "\u001B[46m", true),
    BG_WHITE("<b:white>", "\u001B[47m", true);

    @NotNull public final String id;
    @NotNull public final String code;
    public final boolean background;

    ASNIColour(@NotNull String id, @NotNull String code, boolean background) {
        this.id = id;
        this.code = code;
        this.background = background;
    }

    public static @NotNull String format(@NotNull String s) {
        String result = s;
        for (ASNIColour value : values()) {
            result = result.replace(value.id, value.code);
        }
        return result;
    }
}
