package ru.dargen.tycoon.modules.command.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

@AllArgsConstructor
public enum SenderType {

    PLAYER("Игрок"), CONSOLE("Консоль"), BOTH("Все");

    private @Getter String name;

    public static SenderType of(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender)
            return CONSOLE;
        if (sender instanceof Player)
            return PLAYER;
        return BOTH;
    }
}
