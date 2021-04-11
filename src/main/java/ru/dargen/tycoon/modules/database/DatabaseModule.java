package ru.dargen.tycoon.modules.database;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import ru.dargen.tycoon.Tycoon;
import ru.dargen.tycoon.modules.Module;
import ru.dream.network.core.DreamNetworkCoreBukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class DatabaseModule extends Module implements IDatabaseModule {

    private @Getter
    HikariDataSource dataSource;
    private Connection connection;

    public void enable(Tycoon tycoon) throws Exception {
        dataSource = null;
        while (dataSource == null) {
            try {
                dataSource = DreamNetworkCoreBukkit.getConnector().getHikari();
            } catch (Exception ignored) {
            }
        }
        connection = getConnection();
    }

    public void disable() throws Exception {

    }

    public boolean isConnected() {
        return !dataSource.isClosed();
    }

    public Connection getConnection() {
        try {
            return isConnected() ? dataSource.getConnection() : null;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public PreparedStatement preparedStatement(String sql) {
        try {
            return connection.prepareStatement(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public void update(String update) {
        if (!isConnected())
            return;
        PreparedStatement ps = preparedStatement(update);
        CompletableFuture.runAsync(() -> {
            try {
                ps.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    ps.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public ResultSet query(String query) {
        if (!isConnected())
            return null;
        PreparedStatement ps = preparedStatement(query);
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return ps.executeQuery();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        ps.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void query(String query, Consumer<ResultSet> consumer) {
        if (!isConnected())
            return;
        PreparedStatement ps = preparedStatement(query);
        CompletableFuture.runAsync(() -> {
            try {
                consumer.accept(ps.executeQuery());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    ps.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
