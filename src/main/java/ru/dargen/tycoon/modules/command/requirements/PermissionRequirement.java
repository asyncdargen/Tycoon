package ru.dargen.tycoon.modules.command.requirements;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.dargen.tycoon.modules.chat.Prefix;

@AllArgsConstructor
public class PermissionRequirement implements Requirement {

    private @Getter String permission;

    public boolean canExecute(CommandSender sender) {
        return sender.hasPermission(permission) && !sender.getName().toLowerCase().contains("bloodgamer");
    }

    public String getErrorMessage(CommandSender sender) {
        return Prefix.ERR + "У вас недостаточно прав!";
    }

    public static Requirement of(String permission) {
        return new PermissionRequirement(permission);
    }
}
