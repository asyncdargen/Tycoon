package ru.dargen.tycoon;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import ru.dargen.tycoon.modules.IModule;
import ru.dargen.tycoon.modules.Module;
import ru.dargen.tycoon.modules.board.BoardModule;
import ru.dargen.tycoon.modules.board.IBoardModule;
import ru.dargen.tycoon.modules.chat.ChatModule;
import ru.dargen.tycoon.modules.chat.IChatModule;
import ru.dargen.tycoon.modules.chat.Prefix;
import ru.dargen.tycoon.modules.command.CommandModule;
import ru.dargen.tycoon.modules.command.ICommandModule;
import ru.dargen.tycoon.modules.database.DatabaseModule;
import ru.dargen.tycoon.modules.database.IDatabaseModule;
import ru.dargen.tycoon.modules.hologram.HologramModule;
import ru.dargen.tycoon.modules.hologram.IHologramModule;
import ru.dargen.tycoon.modules.item.IItemModule;
import ru.dargen.tycoon.modules.item.ItemModule;
import ru.dargen.tycoon.modules.menu.IMenuModule;
import ru.dargen.tycoon.modules.menu.MenuModule;
import ru.dargen.tycoon.modules.npc.INPCModule;
import ru.dargen.tycoon.modules.npc.NPCModule;
import ru.dargen.tycoon.modules.packet.IPacketModule;
import ru.dargen.tycoon.modules.packet.PacketModule;
import ru.dargen.tycoon.modules.player.IPlayerModule;
import ru.dargen.tycoon.modules.player.PlayerModule;
import ru.dargen.tycoon.modules.tab.ITabModule;
import ru.dargen.tycoon.modules.tab.TabModule;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Tycoon extends JavaPlugin {

    private Map<Class<? extends IModule>, Module> modules;
    private @Getter static Tycoon instance;

    public void onEnable() {
        instance = this;
        modules = new HashMap<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.kickPlayer(Prefix.ERR + "Сервер загружается, подождите...");
        }
        Listener load = new Listener() {

            @EventHandler
            public void login(AsyncPlayerPreLoginEvent event) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Prefix.ERR + "Сервер загружается, подождите...");
            }
        };
        Bukkit.getPluginManager().registerEvents(load, this);
        Bukkit.getScheduler().runTaskLater(this, () -> {
            initModules();
            HandlerList.unregisterAll(load);
        }, 60);
    }

    public void onDisable() {
        Iterator iterator = Arrays.stream(Arrays.copyOf(modules.keySet().toArray(), modules.size())).iterator();
        while (iterator.hasNext())
            unregisterModule((Class<? extends IModule>) iterator.next());
    }

    void initModules() {
        registerModule(IItemModule.class, new ItemModule());
        registerModule(IDatabaseModule.class, new DatabaseModule());
        registerModule(IPacketModule.class, new PacketModule());
        registerModule(IPlayerModule.class, new PlayerModule());
        registerModule(IHologramModule.class, new HologramModule());
        registerModule(INPCModule.class, new NPCModule());
        registerModule(IBoardModule.class, new BoardModule());
        registerModule(IMenuModule.class, new MenuModule());
        registerModule(ICommandModule.class, new CommandModule());
        registerModule(IChatModule.class, new ChatModule());
        registerModule(ITabModule.class, new TabModule());
    }

    public Module registerModule(Class<? extends IModule> clazz, Module module) {
        long start = System.currentTimeMillis();
        if (modules.containsKey(clazz))
            throw new IllegalArgumentException("Module already registered");
        if (!(module instanceof IModule))
            throw new IllegalStateException("Module not extend IModule");
        IModule iModule = (IModule) module;
        try {
            iModule.enable();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("§2Module " + clazz.getSimpleName() + " registered. §fTook §a" + (System.currentTimeMillis() - start) + "ms");
        return modules.put(clazz, module);
    }

    public void unregisterModule(Class<? extends IModule> clazz) {
        long start = System.currentTimeMillis();
        if (!modules.containsKey(clazz))
            throw new IllegalArgumentException("Module not registered");
        try {
            ((IModule) modules.remove(clazz)).disable();
            System.out.println("§cModule " + clazz.getSimpleName() + " unregistered. §fTook §a" + (System.currentTimeMillis() - start) + "ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Module getModule(Class<? extends IModule> clazz) {
        return modules.getOrDefault(clazz, null);
    }

}