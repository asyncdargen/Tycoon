package ru.dargen.tycoon.modules.hologram.text;

import org.bukkit.entity.Player;

public abstract class HologramLine<T> {

    protected T line;

    public HologramLine(T line) {
        this.line = line;
    }

    public abstract String getLine(Player player);

    public static HologramLine<?> empty() {
        return TextLine.of("");
    }

}
