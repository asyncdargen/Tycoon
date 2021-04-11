package ru.dargen.tycoon.modules.command.args;

import java.util.List;

public interface Argument<T> {

    boolean isRequired();

    T get(String arg) throws Exception;

    String getName();

    List<T> getFilter();
}
