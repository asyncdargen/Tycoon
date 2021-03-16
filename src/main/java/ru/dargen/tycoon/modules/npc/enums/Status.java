package ru.dargen.tycoon.modules.npc.enums;

import lombok.Getter;

public enum Status {

    DAMAGE(2),
    KILL(3),
    USE_TOTEM(35),
    WATER_DAMAGE(36);

    private @Getter byte value;

    Status(int value) {
        this.value = (byte) value;
    }
}
