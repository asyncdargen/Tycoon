package ru.dargen.tycoon.modules.booster.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Spread {
    LOCAL("Локальный"),
    GLOBAL("Глобальный");

    private @Getter final String name;
}
