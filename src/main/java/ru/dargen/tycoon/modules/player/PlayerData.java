package ru.dargen.tycoon.modules.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.dream.network.core.service.account.IAccountService;
import ru.dream.network.core.service.account.PlayerProfile;

@AllArgsConstructor
class PlayerData implements IPlayerData {

    private @Getter final String name;
    private @Getter @Setter double money;
    private @Getter @Setter int prestige;
    private @Getter @Setter int points;
    private @Getter @Setter int topPosition;
    private @Getter final boolean newbe;

    public Player getPlayer() {
        return Bukkit.getPlayer(name);
    }

    public PlayerProfile getProfile() {
        return IAccountService.get().getProfile(name);
    }

    public double getIncome() {
        return 0;
    }

    public void addMoney(double add) {
        money += add;
    }

    public void withdrawMoney(double draw) {
        money -= draw;
    }

    public void withdrawPrestige(int draw) {
        prestige -= draw;
    }

    public void addPrestige(int add) {
        prestige += add;
    }

    public void withdrawPoints(int draw) {
        points -= draw;
    }

    public void addPoints(int add) {
        points += add;
    }



}
