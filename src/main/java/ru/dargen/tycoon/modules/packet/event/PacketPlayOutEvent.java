package ru.dargen.tycoon.modules.packet.event;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_12_R1.Packet;
import net.minecraft.server.v1_12_R1.PacketListenerPlayOut;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PacketPlayOutEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private @Getter
    @Setter
    Packet<PacketListenerPlayOut> packet;
    private @Getter
    @Setter
    boolean cancelled;

    public PacketPlayOutEvent(Player player, Packet<PacketListenerPlayOut> packet) {
        super(player);
        this.packet = packet;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
