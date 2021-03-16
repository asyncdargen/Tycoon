package ru.dargen.tycoon.modules.command;

import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.dargen.tycoon.Tycoon;
import ru.dargen.tycoon.modules.IModule;

import java.util.UUID;

public interface ICommandModule extends IModule{

    static ICommandModule get() {
        return (ICommandModule) Tycoon.getInstance().getModule(ICommandModule.class);
    }

    void registerCommand(Command command);

    void unregisterCommand(String name);

    CommandMap getCommandMap();

    boolean cooldown(UUID uuid);

    String getHelp(CommandSender dargen);

}
