package ru.dargen.tycoon.modules.menu.handler;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.function.Consumer;

public class MenuHandler {

    private @Setter
    @Getter
    Consumer<InventoryCloseEvent> onClose;
    private @Setter
    @Getter
    Consumer<InventoryClickEvent> onClick;

}
