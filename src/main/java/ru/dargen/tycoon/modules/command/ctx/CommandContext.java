package ru.dargen.tycoon.modules.command.ctx;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import ru.dargen.tycoon.modules.command.enums.SenderType;

import java.util.List;

public interface CommandContext {

    CommandSender getSender();

    SenderType getSenderType();

    String[] getOriginalArgs();

    List<Object> getArgs();

    void sendMessage(String msg);

    <T> T getArg(int index);

    boolean hasArg(int index);

    @AllArgsConstructor
    class DefaultContext implements CommandContext {

        private @Getter CommandSender sender;
        private @Getter SenderType senderType;
        private @Getter String[] originalArgs;
        private @Getter List<Object> args;

        public <T> T getArg(int index) {
            return (T) args.get(index);
        }

        public boolean hasArg(int index) {
            return args.size() >= index;
        }

        public void sendMessage(String msg) {
            sender.sendMessage(msg);
        }
    }
}
