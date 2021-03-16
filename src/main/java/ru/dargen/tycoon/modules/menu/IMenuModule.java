package ru.dargen.tycoon.modules.menu;

import org.bukkit.inventory.Inventory;
import ru.dargen.tycoon.Tycoon;
import ru.dargen.tycoon.modules.IModule;

public interface IMenuModule extends IModule {

    static IMenuModule get() {
        return (IMenuModule) Tycoon.getInstance().getModule(IMenuModule.class);
    }

    void register(Menu menu);

    void unregister(Menu menu);

    Menu getMenu(Inventory inv);

}
