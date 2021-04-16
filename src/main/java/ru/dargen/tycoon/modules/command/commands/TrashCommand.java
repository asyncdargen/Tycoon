package ru.dargen.tycoon.modules.command.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.dargen.tycoon.modules.command.Command;
import ru.dargen.tycoon.modules.command.ctx.CommandContext;
import ru.dargen.tycoon.modules.command.enums.SenderType;

public class TrashCommand extends Command {

    public TrashCommand() {
        super("trash", new String[]{"помойка"}, "Помойка");
        setSender(SenderType.PLAYER);
    }

    public void run(CommandContext ctx) {
        ((Player) ctx.getSender()).openInventory(Bukkit.createInventory(null, 36, "Помойка"));
    }
}
