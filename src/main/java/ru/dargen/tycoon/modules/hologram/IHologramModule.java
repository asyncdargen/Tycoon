package ru.dargen.tycoon.modules.hologram;

import ru.dargen.tycoon.Tycoon;
import ru.dargen.tycoon.modules.IModule;

public interface IHologramModule extends IModule {

    static IHologramModule get() {
        return (IHologramModule) Tycoon.getInstance().getModule(IHologramModule.class);
    }

    Hologram register(Hologram hologram);

    void unRegister(int id);

    void unRegister(Hologram hologram);

    void unRegisterAll();

    Hologram getHologramByStandId(int id);

    Hologram getHologram(int id);

}
