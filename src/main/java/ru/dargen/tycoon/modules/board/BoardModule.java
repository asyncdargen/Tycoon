package ru.dargen.tycoon.modules.board;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import ru.dargen.tycoon.Tycoon;
import ru.dargen.tycoon.modules.Module;
import ru.dargen.tycoon.modules.player.IPlayerData;
import ru.dargen.tycoon.modules.player.IPlayerModule;
import ru.dargen.tycoon.utils.formatter.DoubleFormatter;
import ru.dargen.tycoon.utils.formatter.PrestigeFormatter;

public class BoardModule extends Module implements IBoardModule {

    private IPlayerModule module;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        apply(event.getPlayer());
    }

    public void enable(Tycoon tycoon) throws Exception {
        module = IPlayerModule.get();
        registerListener();
        runTaskTimer(tycoon, 0, 20);
    }

    public void disable() throws Exception {
        unRegisterListener();
        cancel();
        module = null;
    }

    public void updateAll() {
        for (Player pl : Bukkit.getOnlinePlayers()) {
            update(pl);
        }
    }

    public void update(Player player) {
        try {
            Scoreboard board = player.getScoreboard();
            IPlayerData data = module.getPlayer(player);
            int top = data.getTopPosition();
            updateTeam(board.getTeam("balance"), "", DoubleFormatter.format(data.getMoney()) + "$");
            updateTeam(board.getTeam("income"), "", DoubleFormatter.format(data.getIncome()) + "$§7/§aсек");
            updateTeam(board.getTeam("position"), "", (top <= 0 || top > 500) ? "§c>500" : top + "");
            updateTeam(board.getTeam("prestige"), "", PrestigeFormatter.format(data.getPrestige()));
            updateTeam(board.getTeam("coins"), "", data.getPoints() + " ❖");
            updateTeam(board.getTeam("online"), "", Bukkit.getOnlinePlayers().size() + "");
        } catch (Exception e) {}
    }

    public void apply(Player player) {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective("§b§lTycoon", "tycoon");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.setDisplayName("§b§lTycoon");
        obj.getScore(" ").setScore(13);
        obj.getScore(" §e§lПрофиль").setScore(12);
        createTeam("balance", "", "", "  §fБаланс: §a", 11, board, obj);
        createTeam("income", "", "", "  §fДоход: §a", 10, board, obj);
        createTeam("position", "", "", "  §fПозиция в топе: §a", 9, board, obj);
        obj.getScore("  ").setScore(8);
        obj.getScore(" §e§lПрестиж").setScore(7);
        createTeam("prestige", "", "", "  §fКоличество: §a", 6, board, obj);
        createTeam("coins", "", "", "  §fОчки престижа: §a", 5, board, obj);
        obj.getScore("   ").setScore(4);
        obj.getScore(" §e§lСервер").setScore(3);
        createTeam("online", "", "", "  §fОнлайн режима: §a", 2, board, obj);
        obj.getScore("    ").setScore(1);
        obj.getScore("    §dstore.dreamnw.ru").setScore(0);
        player.setScoreboard(board);
    }

    public Team createTeam(String name, String prefix, String suffix, String text, int score, Scoreboard board, Objective obj) {
        Team team;
        if ((team = board.getTeam(name)) == null) {
            team = board.registerNewTeam(name);
            team.setPrefix(prefix);
            team.setSuffix(suffix);
            team.addEntry(text);
            obj.getScore(text).setScore(score);
        }
        return team;
    }

    public void updateTeam(Team team, String prefix, String suffix) {
        if (team == null)
            return;
        team.setPrefix(prefix);
        team.setSuffix(suffix);
    }

    public void run() {
        updateAll();
    }
}
