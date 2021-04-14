package ru.dargen.tycoon.modules.perk;

import lombok.Getter;
import ru.dargen.tycoon.modules.perk.enums.Perk;

import java.util.EnumMap;
import java.util.concurrent.atomic.AtomicReference;

public class PlayerPerks extends EnumMap<Perk, Integer> {

    public PlayerPerks() {
        super(Perk.class);
    }

    public void upPerk(Perk perk) {
        put(perk, getPerk(perk) + 1);
    }

    public int getPerk(Perk perk) {
        return getOrDefault(perk, 0);
    }

    public void setPerk(Perk perk, int set) {
        put(perk, set);
    }

    public PlayerPerks parse(String parse) {
        for (String p : parse.split(";")) {
            try {
                setPerk(Perk.valueOf(p.split(":")[0].toUpperCase()),
                        Integer.parseInt(p.split(":")[1]));
            } catch (Exception e) {}
        }
        return this;
    }

    public String toString() {
        AtomicReference<String> str = new AtomicReference<>("");
        forEach((p, l) -> str.set(str.get() + p + ":" + l + ";"));
        return str.get();
    }
}
