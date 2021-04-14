package ru.dargen.tycoon.modules.booster.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

@AllArgsConstructor
public enum Type {

    INCOME("Дохода", Material.DIAMOND_BLOCK),
    CASE("Кейсов", Material.EMERALD_BLOCK);

    private @Getter final String name;
    private @Getter final Material icon;
}
