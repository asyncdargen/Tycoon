package ru.dargen.tycoon.modules.command.args;

public interface Argument<T> {

    boolean isRequired();

    T get(String arg) throws Exception;

    String getName();

}
