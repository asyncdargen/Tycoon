package ru.dargen.tycoon.modules.perk.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import ru.dargen.tycoon.modules.player.IPlayerData;
import ru.dargen.tycoon.utils.ItemBuilder;

import java.util.function.Consumer;

@AllArgsConstructor
public enum Perk {

    LUCK("Удача", 50, new ItemBuilder(Material.GOLD_NUGGET), null,
            new String[]{" §7Повышает шанс на выпадение ящика,", " §7каждый уровень §a+NaN%"}),
    SPEED("Скорость", 15, new ItemBuilder(Material.SUGAR), IPlayerData::checkPerks,
            new String[]{" §7Ваша скорость передвижения выше,", " §7каждый уровень §a+25%"}),
    DRONES("Дроны", 666, new ItemBuilder(Material.REDSTONE_COMPARATOR), IPlayerData::checkPerks,
            new String[]{" §7Акишпетри дурик, ничего не рассказал"});

    private final @Getter String displayName;
    private final @Getter int max;
    private final @Getter ItemStack item;
    private final Consumer<IPlayerData> onBuy;
    private final @Getter String[] lore;

    public void buy(IPlayerData data) {
        if (onBuy != null && data != null)
            onBuy.accept(data);
    }
}
