package ru.dargen.tycoon.modules.chat;

import org.bukkit.entity.Player;
import ru.dargen.tycoon.Tycoon;
import ru.dargen.tycoon.modules.IModule;

import java.util.UUID;

public interface IChatModule extends IModule {

    static IChatModule get() {
        return (IChatModule) Tycoon.getInstance().getModule(IChatModule.class);
    }

    void addChat(String prefix, IChatView view);

    void removeChat(String prefix);

    boolean message(Player player, String msg);

}
