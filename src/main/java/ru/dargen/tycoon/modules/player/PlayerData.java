package ru.dargen.tycoon.modules.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.dargen.tycoon.modules.perk.PlayerPerks;
import ru.dargen.tycoon.modules.perk.enums.Perk;
import ru.dargen.tycoon.utils.formatter.PrestigeFormatter;
import ru.dream.network.core.service.account.IAccountService;
import ru.dream.network.core.service.account.PlayerProfile;
import ru.dream.network.core.service.account.group.PlayerGroup;

@AllArgsConstructor
class PlayerData implements IPlayerData {

    private @Getter final String name;
    private @Getter final boolean newbe;
    private @Getter @Setter double money;
    private @Getter @Setter int prestige;
    private @Getter @Setter int points;
    private @Getter @Setter int topPosition;
    private @Getter PlayerPerks perks;

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

    public int upPrestige() {
        prestige++;
        if (prestige % 50 == 0) {
            PlayerGroup group = getProfile().getGroup();
            Bukkit.broadcastMessage(
                    (group == PlayerGroup.player
                            ? ""
                            : group.getPrefix())
                            + " §7" + name + "§f сделал "
                            + PrestigeFormatter.format(prestige) + "§f престиж");
        }
        return prestige;
    }

    public void checkPerks() {
        getPlayer().setWalkSpeed(0.1f + (getPerks().getPerk(Perk.SPEED) * 0.025f));
    }
}
