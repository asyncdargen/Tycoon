package ru.dargen.tycoon.modules.command.commands;

import org.bukkit.entity.Player;
import ru.dargen.tycoon.modules.command.Command;
import ru.dargen.tycoon.modules.command.ctx.CommandContext;
import ru.dargen.tycoon.modules.command.enums.SenderType;
import ru.dargen.tycoon.modules.menu.menus.MainMenu;

public class MenuCommand extends Command {

    public MenuCommand() {
        super("menu", new String[]{"меню"}, "Отрывает меню режима");
        setSender(SenderType.PLAYER);
    }

    public void run(CommandContext ctx) {
        Player player = (Player) ctx.getSender();
        new MainMenu(player);
    }

}
