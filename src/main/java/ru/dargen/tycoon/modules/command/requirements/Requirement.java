package ru.dargen.tycoon.modules.command.requirements;

import org.bukkit.command.CommandSender;

public interface Requirement {

    boolean canExecute(CommandSender player);

    String getErrorMessage(CommandSender player);

}
