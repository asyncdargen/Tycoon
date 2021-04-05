package ru.dargen.tycoon.modules.item;

import org.apache.logging.log4j.util.BiConsumer;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import ru.dargen.tycoon.Tycoon;
import ru.dargen.tycoon.modules.IModule;

import java.util.Map;

public interface IItemModule extends IModule {

    static IItemModule get() {
        return (IItemModule) Tycoon.getInstance().getModule(IItemModule.class);
    }

    ItemStack getItem(String name);

    void registerItem(String name, ItemStack item);

    void registerItem(String name, ItemStack item, BiConsumer<PlayerInteractEvent, ItemStack> interact);

    void interact(PlayerInteractEvent event);

    Map<String, ItemStack> getItems();

    Map<String, BiConsumer<PlayerInteractEvent, ItemStack>> getInteracts();

}
