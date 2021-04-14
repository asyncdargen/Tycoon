package ru.dargen.tycoon.modules.database;

import org.intellij.lang.annotations.Language;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.function.Consumer;

public interface IDatabaseExecutor {

    PreparedStatement preparedStatement(@Language("SQL") String sql);

    void update(@Language("SQL") String update);

    ResultSet query(@Language("SQL") String query);

    void query(@Language("SQL") String query, Consumer<ResultSet> consumer);

}
