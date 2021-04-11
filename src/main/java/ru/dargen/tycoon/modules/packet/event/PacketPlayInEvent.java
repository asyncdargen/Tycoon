package ru.dargen.tycoon.modules.packet.event;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_12_R1.Packet;
import net.minecraft.server.v1_12_R1.PacketListenerPlayIn;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PacketPlayInEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private @Getter
    @Setter
    Packet<PacketListenerPlayIn> packet;
    private @Getter
    @Setter
    boolean cancelled;

    public PacketPlayInEvent(Player player, Packet<PacketListenerPlayIn> packet) {
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
