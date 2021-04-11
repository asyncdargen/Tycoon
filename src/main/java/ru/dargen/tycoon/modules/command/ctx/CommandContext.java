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

        private @Getter final CommandSender sender;
        private @Getter final SenderType senderType;
        private @Getter final String[] originalArgs;
        private @Getter final List<Object> args;

        public <T> T getArg(int index) {
            return (T) args.get(index);
        }

        public boolean hasArg(int index) {
            return args.size() - 1 >= index;
        }

        public void sendMessage(String msg) {
            sender.sendMessage(msg);
        }
    }
}
