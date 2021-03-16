package ru.dargen.tycoon.modules.hologram.event;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import ru.dargen.tycoon.modules.hologram.Hologram;
import ru.dargen.tycoon.modules.hologram.enums.ClickType;

public class PlayerInteractHologramEvent extends PlayerEvent implements Cancellable {

    private @Getter @Setter boolean cancelled;
    private @Getter Hologram hologram;
    private @Getter @Setter ClickType click;
    private static final HandlerList handlers = new HandlerList();

    public PlayerInteractHologramEvent(Player player, Hologram hologram, ClickType click){
        super(player);
        this.hologram = hologram;
        this.click = click;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
