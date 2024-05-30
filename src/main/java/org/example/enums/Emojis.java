package org.example.enums;

public enum Emojis {

    TENNIS("\uD83C\uDFBE"),
    PIN("\uD83D\uDCCD"),
    CUP("\uD83C\uDFC6"),
    CALENDAR("\uD83D\uDCC5"),
    COST("\uD83D\uDCB0"),
    GOLD_MEDAL("\uD83E\uDD47"),
    SILVER_MEDAL("\uD83E\uDD48"),
    BRONZE_MEDAL("\uD83E\uDD49"),
    CASTLE("\uD83C\uDFF0"),
    USER("\uD83D\uDC64"),
    CHECK("✔️"),
    CROSS("❌"),
    RIGHT_ARROW("➡"),
    LEFT_ARROW("⬅"),
    PHONE("\uD83D\uDCDE");

    private String unicode;
    Emojis(String unicode){
        this.unicode = unicode;
    }

    public String getUnicode() {
        return unicode;
    }
}
