package ru.dargen.tycoon.modules.menu.menus;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.dargen.tycoon.modules.menu.Menu;
import ru.dargen.tycoon.modules.menu.item.MenuItem;
import ru.dargen.tycoon.modules.player.IPlayerData;
import ru.dargen.tycoon.modules.player.IPlayerModule;
import ru.dargen.tycoon.utils.ItemBuilder;
import ru.dargen.tycoon.utils.formatter.PrestigeFormatter;

import java.util.Map;

public class MainMenu extends Menu {

    public MainMenu(Player player) {
        super("Меню", 45, player);
        IPlayerData data = IPlayerModule.get().getPlayer(player);
        setDefaultCancel(true);
        MenuItem line = new MenuItem(new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 15).setName(" "));
        for (int i = 1; i < 10; i++) {
            set(i, line);
        }
        for (int i = 37; i < 46; i++) {
            set(i, line);
        }
        MenuItem donate = new MenuItem(new ItemBuilder(Material.DIAMOND)
                .setName("§aДонат")
                .setItemLore("§7Здесь вы можете преобрести", "§7бустеры и особые предметы"));
        set(41, donate);
        MenuItem teleport = new MenuItem(new ItemBuilder(Material.COMPASS)
                .setName("§aТелепортация на завод")
                .addLoreLine("§7Нажмите, чтобы быстро попасть к своему заводу"));
        set(20, teleport);
        ItemBuilder topBuilder = new ItemBuilder(Material.GOLD_BLOCK)
                .setName("§aТоп по престижу");
        Map<String, Integer> topTen = IPlayerModule.get().getTop().getTopTen();
        int i = 0;
        for (String name : topTen.keySet()) {
            i++;
            int prestige = topTen.get(name);
            String num = (i == 1 ? "§c" : (i == 2 ? "§6" : (i > 3 ? "§a" : "§e"))) + i;
            String nick = player.getName().equalsIgnoreCase(name) ? "§a" + name : "§7" + name;
            topBuilder.addLoreLine(num + " §8- " + nick + " §8- " + PrestigeFormatter.format(topTen.get(name)));
        }
        if (!topTen.containsKey(player.getName())) {
            topBuilder.addLoreLine("§7...");
            topBuilder.addLoreLine("§a" + data.getTopPosition() + " §8- §a" + player.getName() + " §8- " + PrestigeFormatter.format(data.getPrestige()));
        }
        MenuItem top = new MenuItem(topBuilder);
        set(22, top);
        MenuItem up = new MenuItem(new ItemBuilder(Material.EXP_BOTTLE)
                .setName("§aПрокачка престижа")
                .setItemLore(
                        "§7Вы можете повысить престиж, если вы",
                        "§7достигли максимальной прокачки завода.",
                        "§7При повышении престижа, вы §cпотеряете§7:",
                        "§7деньги, улучшения завода и доход", "",
                        "§cВаш завод должен быть прокачен на максимум"));
        set(24, up);
        MenuItem perks = new MenuItem(new ItemBuilder(Material.GOLDEN_CARROT)
                .setName("§aПрокачки")
                .setItemLore(
                        "§7Нажмите, чтобы увидеть список доступных",
                        "§7улучшений, за §aочки престижа"));
        set(26, perks);
        open();
    }

}
