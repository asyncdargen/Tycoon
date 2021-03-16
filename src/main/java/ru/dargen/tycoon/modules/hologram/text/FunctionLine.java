package ru.dargen.tycoon.modules.hologram.text;

import org.bukkit.entity.Player;

import java.util.function.Function;

public class FunctionLine extends HologramLine<Function<Player, String>> {

    public FunctionLine(Function<Player, String> line) {
        super(line);
    }

    public String getLine(Player player) {
        return player == null ? "" : line.apply(player);
    }

    public static FunctionLine of(Function<Player, String> line) {
        return new FunctionLine(line);
    }
}
