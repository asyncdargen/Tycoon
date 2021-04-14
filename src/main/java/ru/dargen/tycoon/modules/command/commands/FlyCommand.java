package ru.dargen.tycoon.modules.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.dargen.tycoon.modules.chat.Prefix;
import ru.dargen.tycoon.modules.command.Command;
import ru.dargen.tycoon.modules.command.ctx.CommandContext;
import ru.dargen.tycoon.modules.command.enums.SenderType;
import ru.dargen.tycoon.modules.command.requirements.PermissionRequirement;

public class FlyCommand extends Command {

    public FlyCommand() {
        super("fly", new String[]{"полёт"}, "Включение режима полёта");
        setRequirement(new PermissionRequirement("tycoon.fly") {
            @Override
            public String getErrorMessage(CommandSender sender) {
                return Prefix.ERR + "Нужно купить в §c/donate";
            }
        });
    }

    public void run(CommandContext ctx) {
        if (ctx.getSenderType() == SenderType.CONSOLE) {
            ctx.sendMessage(Prefix.ERR + "Вы можете только выдавать флай");
            return;
        }
        Player p = ((Player )ctx.getSender());
        boolean allow = !p.getAllowFlight();
        p.setAllowFlight(allow);
        ctx.sendMessage(Prefix.WARN + "Вы " + (allow ? "§aвключили" : "§cвыключили") + "§f режим полёта");
    }
}
