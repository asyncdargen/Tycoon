package ru.dargen.tycoon.modules.menu.item;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class MenuItem {

    private @Setter
    @Getter
    ItemStack item;
    private @Setter
    Consumer<InventoryClickEvent> click;

    public MenuItem(ItemStack item) {
        this.item = item;
    }

    public static MenuItem of(ItemStack item) {
        return new MenuItem(item);
    }

    public static MenuItem of(ItemStack item, Consumer<InventoryClickEvent> click) {
        MenuItem menuItem = new MenuItem(item);
        menuItem.setClick(click);
        return menuItem;
    }

    public void click(InventoryClickEvent event) {
        if (click != null)
            click.accept(event);
    }


}
