package ru.dargen.tycoon.modules.menu.menus;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.dargen.tycoon.modules.chat.Prefix;
import ru.dargen.tycoon.modules.menu.Menu;
import ru.dargen.tycoon.modules.menu.item.MenuItem;
import ru.dargen.tycoon.modules.perk.PlayerPerks;
import ru.dargen.tycoon.modules.perk.enums.Perk;
import ru.dargen.tycoon.modules.player.IPlayerData;
import ru.dargen.tycoon.modules.player.IPlayerModule;
import ru.dargen.tycoon.utils.ItemBuilder;

public class PerksMenu extends Menu {

    public PerksMenu(Player player) {
        super("Улучшения", 27, player);
        IPlayerData data = IPlayerModule.get().getPlayer(player);
        PlayerPerks perks = data.getPerks();
        setDefaultCancel(true);
        int slot = 12;
        for (Perk perk : Perk.values()) {
            ItemBuilder icon = new ItemBuilder(perk.getItem())
                    .setName("§a" + perk.getDisplayName())
                    .setItemLore(perk.getLore())
                    .addLore("", "§fУровень§7: §e" + (perks.getPerk(perk) >= perk.getMax() ? "§cMAX" : perks.getPerk(perk) + "§7/§2" + perk.getMax()));
            if (perks.getPerk(perk) < perk.getMax())
                icon.addLore("", "§fСтоимость прокачки§7: §a1 ❖",
                            "", (data.getPoints() < 1 ? "§c" : "§a") + "Нажмите, чтобы прокачать");
            MenuItem item = new MenuItem(icon);
            item.setClick(e -> {
                if (perks.getPerk(perk) >= perk.getMax()) {
                    player.sendMessage(Prefix.ERR + "Достигнут максимальный уровень");
                    return;
                }
                if (data.getPoints() < 1) {
                    player.sendMessage(Prefix.ERR + "Недостатачно очков престижа для прокачки");
                    return;
                }
                data.withdrawPoints(1);
                perks.upPerk(perk);
                perk.buy(data);
                new PerksMenu(player);
                player.sendMessage(Prefix.SUCCESS + "Вы успешно прокачали улучшение §7- §a" + perk.getDisplayName());
            });
            set(slot, item);
            slot += 2;
        }
        MenuItem back = new MenuItem(new ItemBuilder(Material.ARROW).setName("§cНазад"));
        back.setClick(e -> new MainMenu(player));
        set(19, back);
        open();
    }
}
