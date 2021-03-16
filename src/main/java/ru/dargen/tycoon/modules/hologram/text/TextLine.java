package ru.dargen.tycoon.modules.hologram.text;

import org.bukkit.entity.Player;

public class TextLine extends HologramLine<String> {

    public TextLine(String line) {
        super(line);
    }

    public String getLine(Player player) {
        return line;
    }

    public static TextLine of(String line) {
        return new TextLine(line);
    }

}
