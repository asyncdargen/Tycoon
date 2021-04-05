package ru.dargen.tycoon.modules.command;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import ru.dargen.tycoon.modules.chat.Prefix;
import ru.dargen.tycoon.modules.command.args.Argument;
import ru.dargen.tycoon.modules.command.ctx.CommandContext;
import ru.dargen.tycoon.modules.command.enums.SenderType;
import ru.dargen.tycoon.modules.command.requirements.Requirement;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;

public abstract class Command implements CommandExecutor {

    private @Getter final String name;
    private @Getter @Setter SenderType sender = SenderType.BOTH;
    private @Getter @Setter Requirement requirement;
    private @Getter final String[] aliases;
    private @Getter final String description;
    private @Getter final List<Command> subCommands = new ArrayList<>();
    private @Getter final List<Argument> arguments = new LinkedList<>();

    public Command(String name, String[] aliases, String description) {
        this.name = name.toLowerCase();
        this.description = description;
        if (aliases == null) {
            this.aliases = new String[0];
            return;
        }
        this.aliases = new String[aliases.length];
        for (int i = 0; i < aliases.length; i++) {
            this.aliases[i] = aliases[i].toLowerCase();
        }
    }

    public Command addArgument(Argument argument) {
        arguments.add(argument);
        return this;
    }

    public Command addSubCommand(Command subCommand) {
        subCommands.add(subCommand);
        return this;
    }

    public abstract void run(CommandContext ctx);

    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        List<Object> arg = new LinkedList<>();
        if (!arguments.isEmpty())
            for (int i = 0; i < arguments.size(); i++) {
                Argument argument = arguments.get(i);
                try {
                    arg.add(argument.get(args[i]));
                } catch (Exception e) {
                    if (argument.isRequired()) {
                        sender.sendMessage(Prefix.ERR + "Укажите §c" + argument.getName());
                        return true;
                    }
                }
            }
        CommandContext ctx = new CommandContext.DefaultContext(sender, this.sender, args, arg);
        run(ctx);
        return true;
    }

    public boolean execute(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        if (this.sender != SenderType.BOTH && this.sender != SenderType.of(sender)) {
            sender.sendMessage(Prefix.ERR + "Эту команду может писать только §c" + this.sender.getName());
            return true;
        }
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
        String subArgs[] = new String[args.length - 1];
        for (int i = 1; i < args.length; i++) {
            subArgs[i - 1] = args[i];
        }
        subCommand.execute(sender, cmd, label, subArgs);
        return true;
    }

    public void sendHelp(CommandSender sender){
        BiConsumer<Command, StringBuilder> add = (c,s) -> {
            s.append("   /").append(name).append(" ").append(c.getName()).append(" ");
            if (!c.getArguments().isEmpty())
                for (Argument arg : c.getArguments()) {
                    boolean rq = arg.isRequired();
                    s.append((rq ? "§7[§a" : "§7<§a") + arg.getName().toUpperCase() + (rq ? "§7] §r" : "§7> §r"));
                }
            s.append("§7- §f").append(c.getDescription()).append(" §r\n");
        };StringBuilder help = new StringBuilder().append("§eПомощь по ").append(name).append("\n")
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
