package ru.dargen.tycoon.modules.booster;

import lombok.Getter;
import ru.dargen.tycoon.modules.booster.enums.Source;
import ru.dargen.tycoon.modules.booster.enums.Spread;
import ru.dargen.tycoon.modules.booster.enums.Type;

public class Booster {

    private @Getter long start;
    private @Getter long duration;
    private @Getter Spread spread;
    private @Getter Source source;
    private @Getter Type type;
    private @Getter String owner;
    private @Getter double multiplier;

    public Booster(long start, long duration, Spread spread, Source source, Type type, String owner, double multiplier) {
        this.start = start;
        this.duration = duration;
        this.spread = spread;
        this.source = source;
        this.type = type;
        this.owner = owner;
        this.multiplier = multiplier;
    }

    public boolean isExpired() {
        return !isInfinity() && getLeft() < 0;
    }

    public boolean isInfinity() {
        return duration == 0;
    }

    public long getLeft() {
        return duration - getPassed();
    }

    public long getPassed() {
        return System.currentTimeMillis() - start;
    }

}
