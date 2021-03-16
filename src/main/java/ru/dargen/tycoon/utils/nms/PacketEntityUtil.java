package ru.dargen.tycoon.utils.nms;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class PacketEntityUtil {

    public static void spawnEntitys(Player player, Entity... entitys){
        spawnEntitys(player, Arrays.asList(entitys));
    }

    public static void spawnEntitys(Player player, List<Entity> entitys){
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        for (Entity entity : entitys){
            PacketPlayOutSpawnEntity packet = new PacketPlayOutSpawnEntity(entity, EntityType.fromName(EntityTypes.b(entity)).getTypeId());
            connection.sendPacket(packet);
        }
    }

    public static void spawnEntitys(Player player, int id, Entity... entitys){
        spawnEntitys(player, id, Arrays.asList(entitys));
    }

    public static void spawnEntitys(Player player, int id, List<Entity> entitys){
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        for (Entity entity : entitys){
            PacketPlayOutSpawnEntity packet = new PacketPlayOutSpawnEntity(entity, id);
            connection.sendPacket(packet);
        }
    }

    public static void spawnEntityLivings(Player player, EntityLiving... entitys){
        spawnEntityLivings(player, Arrays.asList(entitys));
    }

    public static void spawnEntityLivings(Player player, List<EntityLiving> entitys){
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        for (EntityLiving entity : entitys){
            PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(entity);
            connection.sendPacket(packet);
        }
    }

    public static void destroyEntitys(Player player, Entity... entitys){
        destroyEntitys(player, Arrays.asList(entitys));
    }

    public static void destroyEntitys(Player player, List<Entity> entitys){
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        for (Entity entity : entitys){
            PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(entity.getId());
            connection.sendPacket(packet);
        }
    }

    public static void metadataUpdate(Player player, int id, DataWatcher watcher){
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        PacketPlayOutEntityMetadata edit = new PacketPlayOutEntityMetadata(id, watcher, false);
        connection.sendPacket(edit);
    }

    public static void headRotation(Player player, Entity entity, byte rotate){
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutEntityHeadRotation(entity, rotate));
    }

    public static void entityHeadLook(Player player, Entity entity, byte yaw, byte pitch){
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutEntity.PacketPlayOutEntityLook(entity.getId(), yaw, pitch, true));
    }

    public static void moveEntity(Player player, int x, int y, int z, Entity entity){
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        PacketPlayOutEntity.PacketPlayOutRelEntityMove packet = new PacketPlayOutEntity.PacketPlayOutRelEntityMove(entity.getId(), x, y, z, false);
        connection.sendPacket(packet);
    }

    public static void equippedEntityItem(Player player, Entity entity, ItemStack item, EnumItemSlot slot){
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        PacketPlayOutEntityEquipment equipment = new PacketPlayOutEntityEquipment(entity.getId(), slot, CraftItemStack.asNMSCopy(item));
        connection.sendPacket(equipment);
    }

    public static void teleportEntity(Player player, Entity entity){
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        PacketPlayOutEntityTeleport teleport = new PacketPlayOutEntityTeleport(entity);
        connection.sendPacket(teleport);
    }

    public static void statusEntity(Player player, Entity entity, byte status){
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        PacketPlayOutEntityStatus packet = new PacketPlayOutEntityStatus(entity, status);
        connection.sendPacket(packet);
    }

}
