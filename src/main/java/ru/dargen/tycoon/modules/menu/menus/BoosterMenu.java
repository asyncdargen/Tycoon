package ru.dargen.tycoon.modules.menu.menus;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.dargen.tycoon.modules.booster.Booster;
import ru.dargen.tycoon.modules.booster.IBoosterModule;
import ru.dargen.tycoon.modules.booster.enums.Type;
import ru.dargen.tycoon.modules.menu.Menu;
import ru.dargen.tycoon.modules.menu.item.MenuItem;
import ru.dargen.tycoon.modules.player.IPlayerData;
import ru.dargen.tycoon.modules.player.IPlayerModule;
import ru.dargen.tycoon.utils.ItemBuilder;
import ru.dargen.tycoon.utils.formatter.DoubleFormatter;
import ru.dargen.tycoon.utils.formatter.TimeFormatter;

import java.util.List;

public class BoosterMenu extends Menu {

    private IPlayerData data;

    public BoosterMenu(Player player) {
        super("Бустеры", 45, player);
        data = IPlayerModule.get().getPlayer(player);
        setDefaultCancel(true);
        MenuItem line = new MenuItem(new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 15).setName(" "));
        set(10, line);
        set(12, line);
        set(28, line);
        set(30, line);

        for (int i = 1; i < 10; i++) {
            set(i, line);
        }

        for (int i = 18; i < 28; i++) {
            set(i, line);
        }

        for (int i = 36; i < 46; i++) {
            set(i, line);
        }
        open();
        update();
    }

    public void update() {
        load(Type.CASE, 29, 31, 32, 33, 34, 35);
        load(Type.INCOME, 11, 13, 14, 15, 16, 17);
        super.update();
    }

    private void load(Type type, int slot, int... slots) {
        double multiplier = IBoosterModule.get().getMultiplier(data, type);
        List<Booster> boosters = IBoosterModule.get().getBoosters(data, type);
        MenuItem boost = new MenuItem(new ItemBuilder(type.getIcon()).setName("§aБустеры " + type.getName()).setItemLore(
                " §fМножитель§7: §ax" + DoubleFormatter.format(multiplier),
                " §fАктивные§7: §a" + boosters.size() + " шт."));
        set(slot, boost);
        slot = 0;
        for (Booster booster : boosters) {
            MenuItem b = new MenuItem(new ItemBuilder(booster.getSource().getIcon())
                .setName("§aБустер " + type.getName())
                .setItemLore(
                        " §fТип§7: §a" + booster.getSpread().getName(),
                        " §fИсточник§7: §a" + booster.getSource().getName(),
                        " §fВладелец§7: §a" + booster.getOwner(),
                        " §fМножитель§7: §ax" + DoubleFormatter.format(booster.getMultiplier()), "",
                        " §fДлительность§7: §a" + (booster.isInfinity() ? "Бесконечность" : TimeFormatter.format(booster.getLeft()))
                ));
            set(slots[slot++], b);
        }
        for (; slot < slots.length; slot++) {
            set(slots[slot], new MenuItem(new ItemBuilder(Material.AIR)));
        }
    }
}
