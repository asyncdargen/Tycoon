package ru.dargen.tycoon.modules.menu.item;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class UpdatableItem extends MenuItem {

    public UpdatableItem(ItemStack item) {
        super(item);
    }

    public static MenuItem of(ItemStack item) {
        return new MenuItem(item);
    }

    public static MenuItem of(ItemStack item, Consumer<InventoryClickEvent> click) {
        MenuItem menuItem = new MenuItem(item);
        menuItem.setClick(click);
        return menuItem;
    }

    public UpdatableItem update() {
        return this;
    }

}
