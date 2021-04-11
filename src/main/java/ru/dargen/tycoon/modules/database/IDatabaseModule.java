package ru.dargen.tycoon.modules.database;

import com.zaxxer.hikari.HikariDataSource;
import ru.dargen.tycoon.Tycoon;
import ru.dargen.tycoon.modules.IModule;

import java.sql.Connection;

public interface IDatabaseModule extends IModule, IDatabaseExecutor {

    static IDatabaseModule get() {
        return (IDatabaseModule) Tycoon.getInstance().getModule(IDatabaseModule.class);
    }

    HikariDataSource getDataSource();

    boolean isConnected();

    Connection getConnection();


}
