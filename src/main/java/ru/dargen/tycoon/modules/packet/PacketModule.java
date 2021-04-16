package ru.dargen.tycoon.modules.packet;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import net.minecraft.server.v1_12_R1.Packet;
import net.minecraft.server.v1_12_R1.PacketListenerPlayIn;
import net.minecraft.server.v1_12_R1.PacketListenerPlayOut;
import net.minecraft.server.v1_12_R1.PacketPlayOutAttachEntity;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.dargen.tycoon.Tycoon;
import ru.dargen.tycoon.modules.Module;
import ru.dargen.tycoon.modules.packet.event.PacketPlayInEvent;
import ru.dargen.tycoon.modules.packet.event.PacketPlayOutEvent;

import java.util.*;

public class PacketModule extends Module implements IPacketModule {

    private Map<UUID, ChannelPipeline> pipelines;

    public void enable(Tycoon tycoon) throws Exception {
        pipelines = new HashMap<>();
        registerListener();
    }

    public void disable() throws Exception {
        unRegisterListener();
        Iterator iterator = Arrays.stream(Arrays.copyOf(pipelines.keySet().toArray(), pipelines.size())).iterator();
        while (iterator.hasNext())
            unListenPlayer((UUID) iterator.next());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        unListenPlayer(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        listenPlayer(event.getPlayer());
    }

    public void handleInPacket(Player player, Packet<PacketListenerPlayIn> packet, List<Object> out) {
        PacketPlayInEvent event = new PacketPlayInEvent(player, packet);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled())
            out.add(packet);
    }

    public void handleOutPacket(Player player, Packet<PacketListenerPlayOut> packet, List<Object> out) {
        PacketPlayOutEvent event = new PacketPlayOutEvent(player, packet);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled())
            out.add(packet);
    }

    public void listenPlayer(Player player) {
        playerPipeline(player)
                .addAfter("decoder", "inListener", new MessageToMessageDecoder<Packet<PacketListenerPlayIn>>() {
                    protected void decode(ChannelHandlerContext ctx, Packet<PacketListenerPlayIn> packet, List<Object> out) throws Exception {
                        handleInPacket(player, packet, out);
                    }
                })
                .addAfter("encoder", "outListener", new MessageToMessageEncoder<Packet<PacketListenerPlayOut>>() {
                    protected void encode(ChannelHandlerContext ctx, Packet<PacketListenerPlayOut> packet, List<Object> out) throws Exception {
                        handleOutPacket(player, packet, out);
                    }
                });
    }

    public void unListenPlayer(UUID uuid) {
        ChannelPipeline pipeline;
        if ((pipeline = pipelines.remove(uuid)) == null)
            return;
        pipeline.remove("inListener");
        pipeline.remove("outListener");
    }

    private ChannelPipeline playerPipeline(Player player) {
        return ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel.pipeline();
    }

}
