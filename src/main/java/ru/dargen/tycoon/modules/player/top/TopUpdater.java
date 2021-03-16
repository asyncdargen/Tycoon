package ru.dargen.tycoon.modules.player.top;

import lombok.Getter;
import ru.dargen.tycoon.modules.database.IAsyncDatabaseExecutor;
import ru.dargen.tycoon.modules.player.IPlayerData;
import ru.dargen.tycoon.modules.player.IPlayerModule;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class TopUpdater {

    private Thread updater;
    private boolean started;
    private final String get = "SELECT `player`, `prestige`, `top` FROM `tycoon`.`players` ORDER BY `players`.`prestige` DESC;";
    private final String set = "UPDATE `tycoon`.`players` SET `top` = '%s' WHERE `players`.`player` = '%s';";
    private IAsyncDatabaseExecutor executor;
    private IPlayerModule module;
    private @Getter Map<String, Integer> topTen;

    public TopUpdater(IAsyncDatabaseExecutor executor, IPlayerModule module) {
        this.executor = executor;
        this.module = module;
    }

    public void start() {
        started = true;
        updater = new Thread(() -> {
            while (started) {
                CompletableFuture.runAsync(this::update);
                try {
                    Thread.sleep(60000 * 5);
                } catch (Exception e) {}
            }
        });
        updater.start();
    }

    public void stop() {
        started = false;
        updater.stop();
    }

    public void update() {
        Map<String, Integer> topTen = new LinkedHashMap<>();
        executor.query(get, (rs) -> {
            int i = 0;
            while (true) {
                try {
                    if (rs.next()) {
                        i++;
                        String player = rs.getString("player");
                        int top = rs.getInt("prestige");
                        if (i <= 10)
                            topTen.put(player, top);
                        if (rs.getInt("top") != i)
                            executor.update(String.format(set, i, player));
                        IPlayerData data;
                        if ((data = module.getPlayer(player)) != null)
                            data.setTopPosition(i);
                    } else
                        break;
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            try {
                rs.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            TopUpdater.this.topTen = topTen;
        });
    }

}
