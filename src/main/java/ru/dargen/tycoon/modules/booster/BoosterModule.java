package ru.dargen.tycoon.modules.booster;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.dargen.tycoon.Tycoon;
import ru.dargen.tycoon.modules.Module;
import ru.dargen.tycoon.modules.booster.enums.Source;
import ru.dargen.tycoon.modules.booster.enums.Spread;
import ru.dargen.tycoon.modules.booster.enums.Type;
import ru.dargen.tycoon.modules.chat.Prefix;
import ru.dargen.tycoon.modules.player.IPlayerData;
import ru.dargen.tycoon.modules.player.IPlayerModule;
import ru.dargen.tycoon.utils.formatter.DoubleFormatter;
import ru.dargen.tycoon.utils.formatter.TimeFormatter;

import java.util.*;

public class BoosterModule extends Module implements IBoosterModule {

    private EnumMap<Type, Booster> global;
    private Map<UUID, List<Booster>> locals;
    private BossBar bar;

    public void enable(Tycoon tycoon) throws Exception {
        global = new EnumMap<>(Type.class);
        locals = new HashMap<>();
        bar = Bukkit.createBossBar("", BarColor.GREEN, BarStyle.SEGMENTED_20);
        bar.setVisible(false);
        runTaskTimer(tycoon, 0, 20);
        registerListener();
    }

    public void disable() throws Exception {
        unRegisterListener();
        bar.removeAll();
    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        bar.addPlayer(e.getPlayer());
    }

    @EventHandler
    public void quit(PlayerQuitEvent e) {
        bar.removePlayer(e.getPlayer());
    }

    public Booster startBooster(Booster booster) {
        Spread spread = booster.getSpread();
        Type type = booster.getType();
        if (spread == Spread.GLOBAL && !global.containsKey(type)) {
            global.put(type, booster);
            Bukkit.broadcastMessage(Prefix.SUCCESS
                    + "Запущен глобальный бустер §a"
                    + type.getName()
                    + "§a x" + DoubleFormatter.format(booster.getMultiplier())
                    + "§f на §a" + TimeFormatter.format(booster.getDuration())
                    + "§f от §a" + booster.getOwner());
        } else {
            IPlayerData data = IPlayerModule.get().getPlayer(booster.getOwner());
            List<Booster> boosters = locals.getOrDefault(data.getPlayer().getUniqueId(), new ArrayList<>());
            boosters.add(booster);
            data.getPlayer().sendMessage(Prefix.SUCCESS
                    + "Запущен локальный бустер §a"
                    + type.getName()
                    + "§a x" + DoubleFormatter.format(booster.getMultiplier())
                    + "§f на §a" + TimeFormatter.format(booster.getDuration()));
            locals.put(data.getPlayer().getUniqueId(), boosters);
        }
        return booster;
    }


    public List<Booster> getBoosters(IPlayerData data, Type type) {
        List<Booster> boosters = new ArrayList<>();

        for (Booster booster : locals.getOrDefault(data.getPlayer().getUniqueId(), new ArrayList<>())) {
            if (booster.getType() == type)
                boosters.add(booster);
        }

        if (global.containsKey(type))
            boosters.add(global.get(type));

        if (data.getPlayer().hasPermission("tycoon.donate.pickaxe") && type == Type.INCOME)
            boosters.add(new Booster(0, 0, Spread.LOCAL, Source.PICKAXE, type, data.getName(), 1.2d));

        if (data.getPrestige() > 0)
            boosters.add(new Booster(0, 0, Spread.LOCAL, Source.PRESTIGE, type, data.getName(), data.getPrestige() / 10d + 1));

        // TODO: 013 13.04.21 donate group booster
        return boosters;
    }

    public double getMultiplier(IPlayerData data, Type type) {
        double multiplier = 1;
        List<Booster> boosters = getBoosters(data, type);
        for (Booster booster : boosters) {
            multiplier += (booster.getMultiplier() - 1) > 0 ? (booster.getMultiplier() - 1) : 0;
        }
        return multiplier;
    }

    public boolean hasBooster(IPlayerData data, Type type, Spread spread) {
        if (spread == Spread.GLOBAL)
            return global.containsKey(type);

        for (Booster booster : locals.getOrDefault(data.getPlayer().getUniqueId(), new ArrayList<>())) {
            if (booster.getType() == type)
                return true;
        }

        return false;
    }

    public boolean incomeBar = true;
    public int toUpd = 5;
    private void updateBar() {
        if (global.isEmpty()) {
            bar.setVisible(false);
            return;
        }
        bar.setVisible(true);
        if (--toUpd == 0) {
            toUpd = 5;
            incomeBar = !incomeBar;
        }
        if (incomeBar && !global.containsKey(Type.INCOME))
            incomeBar = false;
        if (!incomeBar && !global.containsKey(Type.CASE))
            incomeBar = true;
        Type type = incomeBar ? Type.INCOME : Type.CASE;
        Booster booster = global.get(type);
        String title = "§fБустер §a" + type.getName() + "§a x" + DoubleFormatter.format(booster.getMultiplier()) + "§f от §a" + booster.getOwner() + "§f ещё §a" + TimeFormatter.format(booster.getLeft());
        double progress = booster.getLeft() / (double) booster.getDuration();
        bar.setProgress(progress);
        bar.setTitle(title);
    }

    private void updateBoosters() {
        if (!global.isEmpty()) {
            List<Type> toRemove = new ArrayList<>();
            for (Booster boost : global.values()) {
                if (boost.isExpired()) {
                    toRemove.add(boost.getType());
                    Bukkit.broadcastMessage(Prefix.ERR
                            + "Закончился глобальный бустер §c"
                            + boost.getType().getName()
                            + "§c x" + DoubleFormatter.format(boost.getMultiplier())
                            + "§f от §c" + boost.getOwner());
                }
            }
            for (Type type : toRemove) {
                global.remove(type);
            }
        }

        if (locals.isEmpty())
            return;

        for (UUID uuid : locals.keySet()) {
            List<Booster> boosters = locals.getOrDefault(uuid, new ArrayList<>());
            if (boosters.isEmpty())
                continue;
            List<Booster> toRemove = new ArrayList<>();
            for (Booster boost : boosters) {
                if (boost.isExpired()) {
                    toRemove.add(boost);
                    if (Bukkit.getPlayer(uuid) != null)
                        Bukkit.getPlayer(uuid).sendMessage(Prefix.ERR
                                + "Закончился локальный бустер §c"
                                + boost.getType().getName()
                                + "§c x" + DoubleFormatter.format(boost.getMultiplier()));
                }
            }
            boosters.removeAll(toRemove);
        }

    }

    public void run() {
        updateBoosters();
        updateBar();
    }

}
