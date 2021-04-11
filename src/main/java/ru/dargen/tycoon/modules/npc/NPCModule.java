package ru.dargen.tycoon.modules.npc;

import lombok.val;
import net.minecraft.server.v1_12_R1.EnumHand;
import net.minecraft.server.v1_12_R1.PacketPlayInUseEntity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitTask;
import ru.dargen.tycoon.Tycoon;
import ru.dargen.tycoon.modules.Module;
import ru.dargen.tycoon.modules.npc.enums.Interact;
import ru.dargen.tycoon.modules.npc.event.PlayerInteractNPCEvent;
import ru.dargen.tycoon.modules.packet.event.PacketPlayInEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class NPCModule extends Module implements INPCModule {

    private Map<Integer, NPC> npcMap;
    private BukkitTask lookTask;

    @EventHandler
    public void onClick(PacketPlayInEvent e) {
        if (e.getPacket().getClass() != PacketPlayInUseEntity.class)
            return;
        PacketPlayInUseEntity packet = (PacketPlayInUseEntity) e.getPacket();
        if (packet.b() == EnumHand.OFF_HAND)
            return;
        NPC npc;
        if ((npc = getNPC(packet.getEntityId())) == null)
            return;
        val a = packet.a();
        if (a == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT_AT)
            return;
        Interact action = Interact.getByEntityUseAction(a);
        PlayerInteractNPCEvent event = new PlayerInteractNPCEvent(e.getPlayer(), npc, action);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return;
        if (npc.hasClick(action))
            npc.onClick(e.getPlayer(), action);
        e.setCancelled(true);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        for (NPC npc : npcMap.values()) {
            npc.getPlayers().remove(e.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onDeath(PlayerRespawnEvent e) {
        for (NPC npc : npcMap.values()) {
            npc.getPlayers().remove(e.getPlayer().getUniqueId());
        }
    }

    protected void lookUpdate() {
        for (NPC npc : npcMap.values()) {
            if (npc.isLookHeadRotate())
                npc.updateLook();
        }
    }

    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (NPC npc : npcMap.values()) {
                if (npc.getLocation().distance(player.getLocation()) > 35) {
                    if (npc.getPlayers().contains(player.getUniqueId())) {
                        npc.destroy(player);
                        npc.getPlayers().remove(player.getUniqueId());
                    }
                } else {
                    if (!npc.getPlayers().contains(player.getUniqueId())) {
                        npc.show(player);
                        npc.getPlayers().add(player.getUniqueId());
                    }
                }
            }
        }
    }

    public void enable(Tycoon tycoon) throws Exception {
        NPC.module = this;
        npcMap = new HashMap<>();
        registerListener();
        lookTask = Bukkit.getScheduler().runTaskTimer(tycoon, this::lookUpdate, 0, 1);
        runTaskTimer(tycoon, 0, 20);
    }

    public void disable() throws Exception {
        NPC.module = null;
        if (lookTask != null)
            lookTask.cancel();
        cancel();
        unRegisterListener();
        unRegisterAll();
    }

    public NPC register(NPC npc) {
        return npcMap.put(npc.getId(), npc);
    }

    public NPC getNPC(Integer id) {
        return npcMap.getOrDefault(id, null);
    }

    public void unRegister(NPC npc) {
        if ((npc = npcMap.remove(npc.getId())) == null)
            return;
        npc.destroyAll();
    }

    public void unRegister(Integer id) {
        NPC npc;
        if ((npc = npcMap.remove(id)) == null)
            return;
        npc.destroyAll();
    }

    public void unRegisterAll() {
        Iterator iterator = Arrays.stream(Arrays.copyOf(npcMap.values().toArray(), npcMap.size())).iterator();
        while (iterator.hasNext())
            unRegister((NPC) iterator.next());
    }


}
