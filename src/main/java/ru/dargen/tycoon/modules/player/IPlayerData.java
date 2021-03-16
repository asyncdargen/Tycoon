package ru.dargen.tycoon.modules.player;

import org.bukkit.entity.Player;
import ru.dream.network.core.service.account.PlayerProfile;

import java.util.HashMap;
import java.util.Map;

public interface IPlayerData {
    
    Player getPlayer();

    PlayerProfile getProfile();

    double getMoney();

    void setTopPosition(int set);

    int getTopPosition();

    void setMoney(double set);

    void addMoney(double add);

    void withdrawMoney(double draw);

    int getPrestige();

    void setPrestige(int set);

    void addPrestige(int add);

    void withdrawPrestige(int draw);

    int getPoints();

    void setPoints(int set);

    void addPoints(int add);

    void withdrawPoints(int draw);

    double getIncome();

    boolean isNewbe();

    String getName();

    class Builder {

        private Map<String, Object> values = new HashMap<>();

        public Builder money(double money) {
            values.put("money", money);
            return this;
        }

        public Builder prestige(int prestige) {
            values.put("prestige", prestige);
            return this;
        }

        public Builder points(int points) {
            values.put("points", points);
            return this;
        }

        public Builder top(int top) {
            values.put("top", top);
            return this;
        }


        protected <T> T get(String value) {
            return (T) values.get(value);
        }

        public IPlayerData build(String name, boolean newbe) {
            return new PlayerData(name, get("money"), get("prestige"), get("points"), get("top"), newbe);
        }

        public IPlayerData build(String name) {
            return build(name, false);
        }

        public IPlayerData def(String name) {
            return new PlayerData(name, 0, 0, 0, 0, true);
        }

    }
}
