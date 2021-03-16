package ru.dargen.tycoon.modules.command;

import lombok.Getter;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class CommandRegister extends org.bukkit.command.Command {

    private @Getter Command command;

    protected CommandRegister(Command command){
        super(command.getName());
        this.command = command;
        setName(command.getName());
        if(command.getAliases() != null)
            setAliases(Arrays.asList(command.getAliases()));
    }

    public boolean execute(CommandSender sender, String label, String[] args) {
        return command != null && command.execute(sender, this, label, args);
    }

}
