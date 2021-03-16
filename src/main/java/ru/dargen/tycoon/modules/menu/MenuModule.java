package ru.dargen.tycoon.modules.menu;

import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import ru.dargen.tycoon.Tycoon;
import ru.dargen.tycoon.modules.Module;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MenuModule extends Module implements IMenuModule {

    private List<Menu> menuList;

    @EventHandler
    public void onClick(InventoryClickEvent event){
        Menu menu = getMenu(event.getInventory());
        if (menu == null)
            return;
        menu.onClick(event.getSlot(), event);
        if(event.getWhoClicked().getOpenInventory().getTopInventory().equals(menu.getInventory()))
            menu.onAllClick(event);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event){
        Menu menu = getMenu(event.getInventory());
        if(menu == null)
            return;
        if (event.getInventory().equals(event.getInventory()))
            menu.onClose(event);
    }

    public void run() {
        CompletableFuture.runAsync(()-> {
            menuList.forEach(Menu::update);
        });
    }

    public void enable() throws Exception {
        menuList = new ArrayList<>();
        Menu.module = this;
        registerListener();
        runTaskTimerAsynchronously(Tycoon.getInstance(), 0, 20);
    }

    public void disable() throws Exception {
        Menu.module = null;
        unRegisterListener();
        menuList.clear();
        cancel();
    }

    public void register(Menu menu) {
        menuList.add(menu);
    }

    public void unregister(Menu menu) {
        menuList.remove(menu);
    }

    public Menu getMenu(Inventory inv) {
        for (Menu menu : menuList) {
            if (menu.getInventory().equals(inv))
                return menu;
        }
        return null;
    }

}
