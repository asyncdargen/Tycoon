package ru.dargen.tycoon.modules.player;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.dargen.tycoon.Tycoon;
import ru.dargen.tycoon.modules.Module;
import ru.dargen.tycoon.modules.chat.Prefix;
import ru.dargen.tycoon.modules.database.IDatabaseExecutor;
import ru.dargen.tycoon.modules.database.IDatabaseModule;
import ru.dargen.tycoon.modules.item.IItemModule;
import ru.dargen.tycoon.modules.player.top.TopUpdater;
import ru.dream.network.core.api.bungee.BungeeAPI;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PlayerModule extends Module implements IPlayerModule {

    private @Getter
    Map<String, IPlayerData> playerMap;
    private @Getter
    TopUpdater top;

    private String get;
    private String save;
    private String create;

    private IDatabaseExecutor executor;

    public void enable(Tycoon tycoon) throws Exception {
        playerMap = new HashMap<>();
        while (executor == null)
            try {
                executor = IDatabaseModule.get();
            } catch (Exception e) {
            }
        PreparedStatement cdb = executor.preparedStatement("CREATE DATABASE IF NOT EXISTS `tycoon`;");
        cdb.executeUpdate();
        cdb.close();
        executor.update(new StringBuilder()
                .append("CREATE TABLE IF NOT EXISTS `tycoon`.`players` (")
                .append("`player` VARCHAR(20) NOT NULL , ")
                .append("`money` DOUBLE NOT NULL DEFAULT '0', ")
                .append("`prestige` INT NOT NULL DEFAULT '0', ")
                .append("`points` INT NOT NULL DEFAULT '0', ")
                .append("`top` INT NOT NULL DEFAULT '0', ")
                .append("`factory` TEXT, ")
                .append("`perks` TEXT, ")
                .append("PRIMARY KEY (`player`));").toString());
        get = "SELECT * FROM `tycoon`.`players` WHERE `player` = '%s';";
        save = "UPDATE `tycoon`.`players` SET `money` = '%s', `prestige` = '%s', points = '%s', perks = '%s' WHERE `player` = '%s';";
        create = "INSERT INTO `tycoon`.`players` (`player`, `factory`, `perks`) VALUES ('%s', '', '');";
        registerListener();
        runTaskTimerAsynchronously(tycoon, 0, 20 * 60 * 5);
        top = new TopUpdater(executor, this);
        top.start();
    }

    public void disable() throws Exception {
        unRegisterListener();
        cancel();
        top.stop();
        unregisterAll();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        event.getPlayer().sendMessage(Prefix.WARN + "Подождите, загрузка вашего профиля...");
        registerPlayer(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        unregisterPlayer(event.getPlayer());
    }

    private void start(Player player) {
        IItemModule.get().startKit(player);
    }

    public IPlayerData getPlayer(String name) {
        return playerMap.getOrDefault(name.toLowerCase(), null);
    }

    public void registerPlayer(String name) {
        String get = format(this.get, name);
        String create = format(this.create, name);
        IPlayerData.Builder builder = new IPlayerData.Builder();
        executor.query(get, (rs) -> {
            IPlayerData data;
            try {
                if (rs != null && !rs.next()) {
                    data = builder.def(name);
                    executor.update(create);
                    start(Bukkit.getPlayer(name));
                } else {
                    builder.money(rs.getDouble("money"))
                            .prestige(rs.getInt("prestige"))
                            .points(rs.getInt("points"))
                            .top(rs.getInt("top"))
                            .perks(rs.getString("perks"));
                    data = builder.build(name);
                }
                Bukkit.getPlayer(name).sendMessage(Prefix.SUCCESS + "Ваш профиль загружен!");
                playerMap.put(name.toLowerCase(), data);
            } catch (SQLException throwables) {
                Bukkit.getPlayer(name).sendMessage(Prefix.ERR + "Ошибка загрузки профиля");
                BungeeAPI.getInstance().connectPlayer(Bukkit.getPlayer(name), "lobby");
                throwables.printStackTrace();
            }
        });
    }

    public void unregisterPlayer(String name) {
        save(name);
        playerMap.remove(name.toLowerCase());
    }

    public boolean isRegistered(String name) {
        return playerMap.containsKey(name.toLowerCase());
    }

    public void unregisterAll() {
        Iterator iterator = Arrays.stream(Arrays.copyOf(playerMap.keySet().toArray(), playerMap.size())).iterator();
        while (iterator.hasNext())
            unregisterPlayer((String) iterator.next());
    }

    public void save(String name) {
        if (!isRegistered(name))
            return;
        IPlayerData data = getPlayer(name);
        executor.update(
                format(save,
                        data.getMoney(),
                        data.getPrestige(),
                        data.getPoints(),
                        data.getPerks().toString(),
                        name)
        );
    }

    public void run() {
        for (IPlayerData data : playerMap.values()) {
            save(data);
        }
    }

    private String format(String format, Object... fragments) {
        return String.format(format, fragments);
    }
}
