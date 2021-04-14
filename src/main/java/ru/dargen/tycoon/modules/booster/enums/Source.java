package ru.dargen.tycoon.modules.booster.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

@AllArgsConstructor
public enum Source {

    DONATE("Донат", Material.PAPER),
    CASE("Кейс", Material.CHEST),
    COMMAND("Команда", Material.REDSTONE),
    PRESTIGE("Престиж", Material.NETHER_STAR),
    GROUP("Донат группа", Material.CONCRETE);

    private @Getter final String name;
    private @Getter final Material icon;
}
