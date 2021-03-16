package ru.dargen.tycoon.modules.player;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.dargen.tycoon.Tycoon;
import ru.dargen.tycoon.modules.Module;
import ru.dargen.tycoon.modules.chat.Prefix;
import ru.dargen.tycoon.modules.database.IAsyncDatabaseExecutor;
import ru.dargen.tycoon.modules.database.IDatabaseModule;
import ru.dargen.tycoon.modules.player.top.TopUpdater;
import ru.dargen.tycoon.utils.Final;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PlayerModule extends Module implements IPlayerModule {

    private @Getter Map<String, IPlayerData> playerMap;
    private @Getter TopUpdater top;

    private String get;
    private String save;
    private String create;

    private IAsyncDatabaseExecutor executor;

    public void enable() throws Exception {
        playerMap = new HashMap<>();
        while (executor == null)
            try {
                executor = IDatabaseModule.get();
            } catch (Exception e){}
        executor.update("CREATE DATABASE IF NOT EXISTS `tycoon`;");
        executor.update(new StringBuilder()
                .append("CREATE TABLE IF NOT EXISTS `tycoon`.`players` (")
                .append("`player` VARCHAR(20) NOT NULL , ")
                .append("`money` DOUBLE NOT NULL DEFAULT '0', ")
                .append("`prestige` INT NOT NULL DEFAULT '0', ")
                .append("`points` INT NOT NULL DEFAULT '0', ")
                .append("`top` INT NOT NULL DEFAULT '0', ")
                .append("`income` TEXT NOT NULL, ")
                .append("`perks` TEXT NOT NULL, ")
                .append("PRIMARY KEY (`player`));").toString());
        get = "SELECT * FROM `tycoon`.`players` WHERE `player` = '%s';";
        save = "UPDATE `tycoon`.`players` SET `money` = '%s', `prestige` = '%s', points = '%s' WHERE `player` = '%s';";
        create = "INSERT INTO `tycoon`.`players` (`player`, `income`, `perks`) VALUES ('%s', '{}', '{}');";
        registerListener();
        runTaskTimerAsynchronously(Tycoon.getInstance(), 0, 20 * 60 * 5);
        top = new TopUpdater(executor, this);
        top.start();
    }

    public void disable() throws Exception {
        top.stop();
        cancel();
        unregisterAll();
        unRegisterListener();
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

    public IPlayerData getPlayer(String name) {
        return playerMap.getOrDefault(name.toLowerCase(), null);
    }

    public void registerPlayer(String name) {
        final String fname = name.toLowerCase();
        String get = String.format(this.get, name);
        String create = String.format(this.create, name);
        IPlayerData.Builder builder = new IPlayerData.Builder();
        Final<IPlayerData> data = new Final<>();
        executor.query(get, (rs) -> {
                try {
                    if (rs != null && !rs.next()){
                        data.setValue(builder.def(name));
                        executor.update(create);
                    } else {
                        builder.money(rs.getDouble("money"))
                                .prestige(rs.getInt("prestige"))
                                .points(rs.getInt("points"))
                                .top(rs.getInt("top"));
                        data.setValue(builder.build(fname));
                    }
                    Bukkit.getPlayer(fname).sendMessage(Prefix.SUCCESS + "Ваш профиль загружен!");
                    playerMap.put(fname, data.getValue());
                } catch (SQLException throwables) {
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
        final String fname = name.toLowerCase();
        if (!isRegistered(name))
            return;
        IPlayerData data = getPlayer(fname);
        String save = String.format(this.save,
                data.getMoney(),
                data.getPrestige(),
                data.getPoints(),
                name);
        executor.update(save);
    }

    public void run() {
        for (IPlayerData data : playerMap.values()) {
            save(data);
        }
    }
}
