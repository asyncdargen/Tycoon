package ru.dargen.tycoon.modules.tab;

import org.bukkit.entity.Player;
import ru.dargen.tycoon.Tycoon;
import ru.dargen.tycoon.modules.IModule;

public interface ITabModule extends IModule {

    static ITabModule get() {
        return (ITabModule) Tycoon.getInstance().getModule(ITabModule.class);
    }

    void applyTab(Player player);

    ITabView getView();

    void setView(ITabView view);

}
