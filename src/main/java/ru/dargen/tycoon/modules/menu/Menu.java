package ru.dargen.tycoon.modules.menu;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import ru.dargen.tycoon.modules.menu.handler.MenuHandler;
import ru.dargen.tycoon.modules.menu.item.MenuItem;
import ru.dargen.tycoon.modules.menu.item.UpdatableItem;

import java.util.LinkedHashMap;
import java.util.Map;

public class Menu {

    protected static IMenuModule module;

    private @Setter boolean defaultCancel = false;
    private @Getter Inventory inventory;
    private @Getter MenuHandler handler;
    private @Getter Map<Integer, MenuItem> items = new LinkedHashMap<>();
    private @Getter Player player;

    public Menu(String title, int size, Player player){
        inventory = Bukkit.createInventory(null, size, title);
        module.register(this);
        this.player = player;
    }

    public Menu(String title, InventoryType type, Player player){
        inventory = Bukkit.createInventory(null, type, title);
        module.register(this);
        this.player = player;
    }

    public Menu set(int slot, MenuItem item) {
        if (slot < 0 || --slot > inventory.getSize())
            return this;
        items.put(slot, item);
        inventory.setItem(slot, item.getItem());
        return this;
    }

    public Menu add(MenuItem item) {
        for (int i = 1; i <= inventory.getSize(); i++) {
            if (items.containsKey(i))
                continue;
            set(i, item);
            break;
        }
        return this;
    }

    public void setHandler(MenuHandler handler) {
        this.handler = handler;
    }

    public void onAllClick(InventoryClickEvent event) {
        if (handler != null)
            if (handler.getOnClick() != null)
                handler.getOnClick().accept(event);
    }

    public void onClose(InventoryCloseEvent event) {
         if (handler == null) {
            module.unregister(this);
            return;
        }
        if (handler.getOnClose() != null)
            handler.getOnClose().accept(event);
        module.unregister(this);
    }

    public void onClick(int slot, InventoryClickEvent event) {
        if (!items.containsKey(slot)) {
            event.setCancelled(defaultCancel);
            return;
        }
        if (event.getInventory().getItem(slot)==null || event.getInventory().getItem(slot).getType().equals(Material.AIR)){
            event.setCancelled(defaultCancel);
            return;
        }
        MenuItem item = items.get(slot);
        item.click(event);
        event.setCancelled(defaultCancel);
    }

    public void open() {
        player.openInventory(inventory);
    }

    public void update() {
        for (Integer slot : items.keySet()) {
            MenuItem item = items.get(slot);
            if (item instanceof UpdatableItem)
                inventory.setItem(slot, ((UpdatableItem) item).update().getItem());
        }
    }

}
