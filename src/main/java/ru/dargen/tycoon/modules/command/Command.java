package ru.dargen.tycoon.modules.command;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import ru.dargen.tycoon.modules.command.requirements.Requirement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public abstract class Command implements CommandExecutor {

    private @Getter final String name;
    private @Getter @Setter Requirement requirement;
    private @Getter final String[] aliases;
    private @Getter final String description;
    private @Getter List<Command> subCommands = new ArrayList<>();

    public Command(String name, String[] aliases, String description) {
        this.name = name.toLowerCase();
        this.aliases = new String[aliases.length];
        for (int i = 0; i < aliases.length; i++) {
            this.aliases[i] = aliases[i].toLowerCase();
        }
        this.description = description;
    }

    public Command addSubCommand(Command subCommand) {
        subCommands.add(subCommand);
        return this;
    }

    public abstract void run(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args);

    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        run(sender, cmd, label, args);
        return true;
    }

    public boolean execute(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        if (requirement != null && !requirement.canExecute(sender)) {
            sender.sendMessage(requirement.getErrorMessage(sender));
            return true;
        }
        if (subCommands.isEmpty() || args.length == 0) {
            onCommand(sender, cmd, label, args);
            return true;
        }
        String subName = args[0].toLowerCase();
        Command subCommand = null;
        for (Command sub : subCommands) {
            if (sub.getName().equals(subName)) {
                subCommand = sub;
                break;
            }
            for (String alias : sub.getAliases()) {
                if (alias.equals(subName)) {
                    subCommand = sub;
                    break;
                }
            }
        }
        if (subCommand == null) {
            onCommand(sender, cmd, label, args);
            return true;
        }
        String subArgs[] = new String[args.length];
        for (int i = 1; i < args.length; i++) {
            subArgs[i - 1] = args[i];
        }
        subCommand.execute(sender, cmd, label, subArgs);
        return true;
    }

    public void sendHelp(CommandSender sender){
        BiConsumer<Command, StringBuilder> add = (c,s) -> s.append("    /").append(name).append(" ").append(c.getName()).append(" ").append("§7- §f").append(c.getDescription()).append(" §r\n");
        StringBuilder help = new StringBuilder().append("§eПомощь по ").append(name).append("\n")
                .append("§6================================================§r\n");
        for (Command command : subCommands) {
            boolean canExec = true;
            if (command.getRequirement() !=  null)
                canExec = command.getRequirement().canExecute(sender);
            if (canExec)
                add.accept(command, help);
        }
        help.append("§6================================================");
        sender.sendMessage(help.toString());
    }

}
