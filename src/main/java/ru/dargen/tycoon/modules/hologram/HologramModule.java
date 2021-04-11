package ru.dargen.tycoon.modules.hologram;

import lombok.val;
import net.minecraft.server.v1_12_R1.EnumHand;
import net.minecraft.server.v1_12_R1.PacketPlayInUseEntity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import ru.dargen.tycoon.Tycoon;
import ru.dargen.tycoon.modules.IModule;
import ru.dargen.tycoon.modules.Module;
import ru.dargen.tycoon.modules.hologram.enums.ClickType;
import ru.dargen.tycoon.modules.hologram.event.PlayerInteractHologramEvent;
import ru.dargen.tycoon.modules.packet.event.PacketPlayInEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HologramModule extends Module implements IHologramModule {

    private Map<Integer, Hologram> hologramMap;

    public Hologram register(Hologram hologram) {
        return hologramMap.put(hologram.getId(), hologram);
    }


    @EventHandler
    public void onClick(PacketPlayInEvent e){
        if (e.getPacket().getClass() != PacketPlayInUseEntity.class)
            return;
        PacketPlayInUseEntity packet = (PacketPlayInUseEntity) e.getPacket();
        if (packet.b() == EnumHand.OFF_HAND)
            return;
        Hologram hologram = getHologram(packet.getEntityId());
        if (hologram == null)
            return;
        val a = packet.a();
        if (a == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT_AT)
            return;
        ClickType action = ClickType.getByEntityUseAction(a);
        PlayerInteractHologramEvent event = new PlayerInteractHologramEvent(e.getPlayer(), hologram, action);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return;
        if (hologram.hasClick(action))
            hologram.onClick(e.getPlayer(), action);
        e.setCancelled(true);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        for (Hologram hologram : hologramMap.values()){
            hologram.getPlayers().remove(e.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onDeath(PlayerRespawnEvent e){
        for (Hologram hologram : hologramMap.values()){
            hologram.getPlayers().remove(e.getPlayer().getUniqueId());
        }
    }

    public void unRegister(int id) {
        Hologram hologram;
        if ((hologram = hologramMap.remove(id)) == null)
            return;
        hologram.destroyAll();
    }

    public void unRegister(Hologram hologram) {
        if ((hologram = hologramMap.remove(hologram.getId())) == null)
            return;
        hologram.destroyAll();
    }


    public Hologram getHologram(int id) {
        return hologramMap.getOrDefault(id, null);
    }

    public void enable(Tycoon tycoon) throws Exception {
        Hologram.module = this;
        hologramMap = new HashMap<>();
        runTaskTimer(tycoon, 0, 20);
        registerListener();
    }

    public void disable() throws Exception {
        Hologram.module = null;
        cancel();
        unRegisterListener();
        unRegisterAll();
    }

    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()){
            for (Hologram hologram : hologramMap.values()){
                if (hologram.getLocation().distance(player.getLocation()) > 40){
                    hologram.destroy(player);
                    hologram.getPlayers().remove(player.getUniqueId());
                } else {
                    if (!hologram.getPlayers().contains(player.getUniqueId())) {
                        hologram.getPlayers().add(player.getUniqueId());
                        hologram.show(player);
                    }
                    hologram.onUpdate(player);
                }
            }
        }
    }

    public void unRegisterAll() {
        Iterator iterator = Arrays.stream(Arrays.copyOf(hologramMap.values().toArray(), hologramMap.size())).iterator();
        while (iterator.hasNext())
            unRegister((Hologram) iterator.next());
    }

    public Hologram getHologramByStandId(int id) {
        for (Hologram holo : hologramMap.values()) {
            if (holo.isHologramStand(id))
                return holo;
        }
        return null;
    }

}
