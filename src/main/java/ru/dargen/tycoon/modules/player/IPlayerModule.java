package ru.dargen.tycoon.modules.player;

import org.bukkit.entity.Player;
import ru.dargen.tycoon.Tycoon;
import ru.dargen.tycoon.modules.IModule;
import ru.dargen.tycoon.modules.player.top.TopUpdater;

import java.util.Map;

public interface IPlayerModule extends IModule {

    static IPlayerModule get() {
        return (IPlayerModule) Tycoon.getInstance().getModule(IPlayerModule.class);
    }

    IPlayerData getPlayer(String name);

    default IPlayerData getPlayer(IPlayerData data) {
        return getPlayer(data.getName());
    }

    default IPlayerData getPlayer(Player player) {
        return getPlayer(player.getName());
    }

    void registerPlayer(String name);

    default void registerPlayer(Player player) {
        registerPlayer(player.getName());
    }

    void unregisterPlayer(String name);

    default void unregisterPlayer(IPlayerData data) {
        unregisterPlayer(data.getName());
    }

    default void unregisterPlayer(Player player) {
        unregisterPlayer(player.getName());
    }

    void save(String name);

    default void save(IPlayerData data) {
        save(data.getName());
    }

    default void save(Player player) {
        save(player.getName());
    }

    boolean isRegistered(String name);

    default boolean isRegistered(Player player) {
        return isRegistered(player.getName());
    }

    void unregisterAll();

    TopUpdater getTop();

    Map<String, IPlayerData> getPlayerMap();

}
