package ru.dargen.tycoon.modules.command.requirements;

import org.bukkit.command.CommandSender;
import ru.dargen.tycoon.modules.chat.Prefix;

public class DonateRequirement extends PermissionRequirement {

    public DonateRequirement(String permission) {
        super(permission);
    }

    public String getErrorMessage(CommandSender sender) {
        return Prefix.ERR + "Нужно купить в §c/donate";
    }
}
