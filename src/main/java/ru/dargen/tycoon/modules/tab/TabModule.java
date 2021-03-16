package ru.dargen.tycoon.modules.tab;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_12_R1.PacketPlayOutScoreboardTeam;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import ru.dargen.tycoon.Tycoon;
import ru.dargen.tycoon.modules.Module;
import ru.dargen.tycoon.modules.player.IPlayerData;
import ru.dargen.tycoon.modules.player.IPlayerModule;
import ru.dargen.tycoon.utils.formatter.PrestigeFormatter;
import ru.dargen.tycoon.utils.reflect.ReflectUtil;
import ru.dream.network.core.service.account.IAccountService;
import ru.dream.network.core.service.account.group.PlayerGroup;
import ru.dream.network.core.service.server.DreamServer;
import ru.dream.network.core.service.server.IServerService;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class TabModule extends Module implements ITabModule {
    // TODO: 016 16.03.21 пофикстить негра
    private @Getter @Setter ITabView view;
    private DreamServer server;
    private final String HEADER = "\n§2§lDream §a§lNetwork\n";
    private final String FOOTER = "\n§2§lСайт §astore.dreamnw.ru\n§2§lОбщий онлайн §a%online%";

    public void enable() throws Exception {
        Bukkit.getScheduler().runTaskLater(Tycoon.getInstance(),
                () -> {server = IServerService.get().getServer("bungee");}, 20);
        runTaskTimer(Tycoon.getInstance(), 0, 40);
        IPlayerModule module = IPlayerModule.get();
        IAccountService accounts = IAccountService.get();
        view = (p) -> {
            PacketPlayOutScoreboardTeam team = new PacketPlayOutScoreboardTeam();
            IPlayerData data = module.getPlayer(p);
            if (data == null) {
                return team;
            }
            PlayerGroup group = accounts.getProfile(p).getGroup();
            int priority = group.getGroupPriority();
            String prefix = priority == 1 ? "§7" : group.getPrefix() + " §7";
            String suffix = data.getPrestige() > 0 ? " §8[" + PrestigeFormatter.format(data.getPrestige()) + "§8]" : "";
            String name = "";
            if (priority < 10) {
                name += "00" + priority;
            } else if(priority < 100) {
                name += "0" + priority;
            } else {
                name += priority;
            }
            String newName = "";
            int a = name.charAt(0);
            newName += (char) ((57 - a) + 48);
            a = name.charAt(1);
            newName += (char) ((57 - a) + 48);
            a = name.charAt(2);
            newName += (char) ((57 - a) + 48);
            name = newName;
            ReflectUtil.setValue(team, "i", 0);
            ReflectUtil.setValue(team, "h", Collections.singletonList(p.getName()));
            ReflectUtil.setValue(team, "e", "ALWAYS");
            ReflectUtil.setValue(team, "d", suffix);
            ReflectUtil.setValue(team, "c", prefix);
            ReflectUtil.setValue(team, "a", name);
            return team;
        };
    }

    public void disable() throws Exception {
        cancel();
        view = null;
        server = null;
    }

    public void applyTab(Player player) {
        player.setPlayerListHeaderFooter(TextComponent.fromLegacyText(HEADER),
                TextComponent.fromLegacyText(FOOTER.replace("%online%", "" + server.getOnline())));
        PacketPlayOutScoreboardTeam team = view.generateTeam(player);
        for (Player p : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(team);
        }
    }

    public void run() {
        Bukkit.getOnlinePlayers().forEach((p) -> CompletableFuture.runAsync(() -> applyTab(p)));
    }

}
