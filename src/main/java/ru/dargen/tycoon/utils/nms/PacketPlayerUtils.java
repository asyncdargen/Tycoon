package ru.dargen.tycoon.utils.nms;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PacketPlayerUtils {

    public static void playerInfo(Player player, PacketPlayOutPlayerInfo.EnumPlayerInfoAction info, EntityPlayer... packetPlayer){
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutPlayerInfo(info, packetPlayer));
    }

    public static void spawnPlayer(Player player, EntityHuman packetPlayer){
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutNamedEntitySpawn(packetPlayer));
    }


}
