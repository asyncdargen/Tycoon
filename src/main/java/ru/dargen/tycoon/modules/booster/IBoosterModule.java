package ru.dargen.tycoon.modules.booster;

import ru.dargen.tycoon.Tycoon;
import ru.dargen.tycoon.modules.IModule;
import ru.dargen.tycoon.modules.booster.enums.Spread;
import ru.dargen.tycoon.modules.booster.enums.Type;
import ru.dargen.tycoon.modules.player.IPlayerData;

import java.util.List;

public interface IBoosterModule extends IModule {

    static IBoosterModule get() {
        return (IBoosterModule) Tycoon.getInstance().getModule(IBoosterModule.class);
    }

    Booster startBooster(Booster booster);

    List<Booster> getBoosters(IPlayerData data, Type type);

    double getMultiplier(IPlayerData data, Type type);

    boolean hasBooster(IPlayerData data, Type type, Spread spread);
}
