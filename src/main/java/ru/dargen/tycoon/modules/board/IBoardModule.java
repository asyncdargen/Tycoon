package ru.dargen.tycoon.modules.board;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import ru.dargen.tycoon.Tycoon;
import ru.dargen.tycoon.modules.IModule;

public interface IBoardModule extends IModule {

    static IBoardModule get() {
        return (IBoardModule) Tycoon.getInstance().getModule(IBoardModule.class);
    }

    Team createTeam(String name, String prefix, String suffix, String text, int score, Scoreboard board, Objective obj);

    void updateTeam(Team team, String prefix, String suffix);

    void updateAll();

    void update(Player player);

    void apply(Player player);

}
