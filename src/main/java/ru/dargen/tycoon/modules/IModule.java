package ru.dargen.tycoon.modules;

import ru.dargen.tycoon.Tycoon;

public interface IModule {

    void enable(Tycoon tycoon) throws Exception;

    void disable() throws Exception;

}
