package ru.dargen.tycoon.modules.mechanic;

import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.PacketPlayOutBlockChange;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import ru.dargen.tycoon.modules.player.IPlayerData;
import ru.dargen.tycoon.modules.player.IPlayerModule;
import ru.dargen.tycoon.utils.reflect.ReflectUtil;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class OreRecover {

    private boolean enabled;
    private Map<UUID, List<Ore>> recovery;

    public OreRecover(IPlayerModule module) {
        enabled = true;
        recovery = new HashMap<>();

        new Thread(() -> {
            while (enabled) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (UUID uuid : new ArrayList<>(recovery.keySet())) {
                    IPlayerData data;

                    if ((data = module.getPlayer(Bukkit.getPlayer(uuid))) == null) {
                        recovery.remove(uuid);
                        continue;
                    }

                    List<Ore> ores = recovery.getOrDefault(uuid, new ArrayList<>());

                    if (ores.isEmpty())
                        continue;

                    for (Ore ore : new ArrayList<>(ores)) {
                        if (--ore.to > 0)
                            continue;
                        recover(ore.block, data.getPlayer());
                        ores.remove(ore);
                    }
                }
            }
        }).start();
    }

    public void recover(BlockPosition pos, Player player) {
        PacketPlayOutBlockChange bc = new PacketPlayOutBlockChange();
        Block block = player.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ());
        String type = block.getType().name().replace("GLOWING", "LIT").toLowerCase();
        bc.block = net.minecraft.server.v1_12_R1.Block.getByName(type).getBlockData();
        ReflectUtil.setValue(bc, "a", pos);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(bc);
    }

    public void add(UUID uuid, BlockPosition loc) {
        List<Ore> ores = recovery.getOrDefault(uuid, new ArrayList<>());
        ores.add(new Ore(loc));
        recovery.put(uuid, ores);
    }

    public void remove(UUID uuid) {
        recovery.remove(uuid);
    }
    public void stop() {
        recovery.clear();
        enabled = false;
    }

    class Ore {

        private int to;
        private BlockPosition block;

        public Ore(BlockPosition block) {
            this.block = block;
            to = 10 + ThreadLocalRandom.current().nextInt(5);
        }

    }
}
