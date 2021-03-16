package ru.dargen.tycoon.modules.packet;

import net.minecraft.server.v1_12_R1.Packet;
import net.minecraft.server.v1_12_R1.PacketListenerPlayIn;
import net.minecraft.server.v1_12_R1.PacketListenerPlayOut;
import org.bukkit.entity.Player;
import ru.dargen.tycoon.Tycoon;
import ru.dargen.tycoon.modules.IModule;

import java.util.List;
import java.util.UUID;

public interface IPacketModule extends IModule {

    static IPacketModule get() {
        return (IPacketModule) Tycoon.getInstance().getModule(IPacketModule.class);
    }

    void handleInPacket(Player player, Packet<PacketListenerPlayIn> packet, List<Object> out);

    void handleOutPacket(Player player, Packet<PacketListenerPlayOut> packet, List<Object> out);

    void listenPlayer(Player player);

    void unListenPlayer(UUID uuid);

}
