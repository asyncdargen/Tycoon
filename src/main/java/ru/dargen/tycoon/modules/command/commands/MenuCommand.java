package ru.dargen.tycoon.modules.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import ru.dargen.tycoon.modules.command.Command;
import ru.dargen.tycoon.modules.menu.menus.MainMenu;

public class MenuCommand extends Command {

    public MenuCommand() {
        super("menu", new String[]{"меню"}, "Отрывает меню режима");
    }

    public void run(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender)
            return;
        Player player = (Player) sender;
        new MainMenu(player);
    }

}
