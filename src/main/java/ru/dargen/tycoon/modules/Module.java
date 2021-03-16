package ru.dargen.tycoon.modules;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import ru.dargen.tycoon.Tycoon;

public abstract class Module extends BukkitRunnable implements Listener {

    public final void registerListener() {
        Bukkit.getServer().getPluginManager().registerEvents(this, Tycoon.getInstance());
    }

    public final void unRegisterListener() {
        HandlerList.unregisterAll(this);
    }

    public void run() {}

}
