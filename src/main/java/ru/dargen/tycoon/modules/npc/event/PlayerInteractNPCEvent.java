package ru.dargen.tycoon.modules.npc.event;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import ru.dargen.tycoon.modules.npc.NPC;
import ru.dargen.tycoon.modules.npc.enums.Interact;

public class PlayerInteractNPCEvent extends PlayerEvent implements Cancellable {

    private @Getter @Setter boolean cancelled;
    private @Getter NPC npc;
    private @Getter Interact interact;
    private static final HandlerList handlers = new HandlerList();

    public PlayerInteractNPCEvent(Player player, NPC npc, Interact interact){
        super(player);
        this.npc = npc;
        this.interact = interact;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
