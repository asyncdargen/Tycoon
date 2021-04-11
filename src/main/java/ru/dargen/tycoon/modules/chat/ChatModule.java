package ru.dargen.tycoon.modules.chat;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import ru.dargen.tycoon.Tycoon;
import ru.dargen.tycoon.modules.Module;
import ru.dargen.tycoon.modules.player.IPlayerData;
import ru.dargen.tycoon.modules.player.IPlayerModule;
import ru.dargen.tycoon.utils.formatter.DoubleFormatter;
import ru.dargen.tycoon.utils.formatter.PrestigeFormatter;
import ru.dream.network.core.service.account.IAccountService;
import ru.dream.network.core.service.account.PlayerProfile;
import ru.dream.network.core.service.account.group.PlayerGroup;

import java.util.HashMap;
import java.util.Map;

public class ChatModule extends Module implements IChatModule {

    private ConsoleCommandSender console;
    private Map<String, IChatView> views;

    public void enable(Tycoon tycoon) throws Exception {
        views = new HashMap<>();
        console = Bukkit.getConsoleSender();
        registerListener();
        registerDefaultChats();
    }

    public void disable() throws Exception {
        views.clear();
        unRegisterListener();
    }

    public void addChat(String prefix, IChatView view) {
        views.put(prefix.toLowerCase(), view);
    }

    private void registerDefaultChats() {
        IPlayerModule module = IPlayerModule.get();
        IChatView def = new IChatView() {
            public TextComponent getStats(Player player) {
                IPlayerData data = module.getPlayer(player);
                TextComponent statsText = new TextComponent("§7[§aСтатистика§7]");
                statsText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        TextComponent.fromLegacyText(new StringBuilder()
                                .append("§fПозиция в топе: §a" + ((data.getTopPosition() <= 0 || data.getTopPosition() > 500) ? "§c>500" : data.getTopPosition()) + "\n")
                                .append("§fБаланс: §a" + DoubleFormatter.format(data.getMoney()) + "$\n")
                                .append("§fДоход: §a" + DoubleFormatter.format(data.getIncome()) + "$§7/§aсек\n")
                                .append("§fПрестиж: " + PrestigeFormatter.format(data.getPrestige()))
                                .toString())));
                return statsText;
            }

            public TextComponent getFormat(Player player, String message) {
                if (player.hasPermission("chat.color"))
                    message = message.replace("&", "§");
                IPlayerData data = module.getPlayer(player);
                if (data == null)
                    return new TextComponent("");
                int p = data.getPrestige();
                PlayerProfile profile = IAccountService.get().getProfile(player);
                PlayerGroup group = profile.getGroup();
                TextComponent msg = new TextComponent(group.getPrefix() + (group == PlayerGroup.player ? "" : " §r"));
                TextComponent nick = new TextComponent("§7" + player.getName());
                nick.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§aНажмите, чтобы написать сообщение")));
                nick.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + player.getName() + " "));
                msg.addExtra(nick);
                if (p > 0) {
                    TextComponent prestige = new TextComponent(" §8| §r" + PrestigeFormatter.format(p));
                    prestige.setClickEvent(null);
                    prestige.setHoverEvent(null);
                    msg.addExtra(prestige);
                }
                TextComponent end = new TextComponent("§7:§f");
                end.setClickEvent(null);
                end.setHoverEvent(null);
                msg.addExtra(end);
                if (message.contains("[stats]")) {
                    TextComponent last = null;
                    String[] splited = message.split(" ");
                    for (String s : splited) {
                        if (s.isEmpty())
                            continue;
                        msg.addExtra(" ");
                        if (s.equalsIgnoreCase("[stats]"))
                            msg.addExtra(getStats(player));
                        else
                            msg.addExtra(s);
                    }
                } else {
                    msg.addExtra(" " + message);
                }
                return msg;
            }

            public boolean canView(Player sender, Player view) {
                return true;
            }
        };
        addChat("!", new IChatView() {
            public TextComponent getFormat(Player player, String message) {
                TextComponent prefix = new TextComponent("§7(§a§lG§7) §f");
                prefix.addExtra(def.getFormat(player, message));
                return prefix;
            }

            public boolean canView(Player sender, Player view) {
                return true;
            }
        });
        addChat("", new IChatView() {
            public TextComponent getFormat(Player player, String message) {
                TextComponent prefix = new TextComponent("§7(§e§lL§7) §f");
                prefix.addExtra(def.getFormat(player, message));
                return prefix;
            }

            public boolean canView(Player sender, Player view) {
                return module.getPlayer(sender).getPrestige() == module.getPlayer(view).getPrestige();
            }
        });
    }

    public void removeChat(String prefix) {
        views.remove(prefix.toLowerCase());
    }

    public boolean message(Player player, String msg) {
        IChatView chat = null;
        String prefix = "";
        for (String s : views.keySet()) {
            if (msg.toLowerCase().startsWith(s) && !s.equals(prefix)) {
                chat = views.get(s);
                prefix = s;
                break;
            }
        }
        if (chat == null)
            if ((chat = views.get("")) == null)
                return false;
        msg = msg.substring(prefix.length());
        TextComponent messageComponent = chat.getFormat(player, msg);
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            if (chat.canView(player, viewer))
                viewer.sendMessage(messageComponent);
        }
        console.sendMessage(messageComponent);
        return true;
    }

    @EventHandler
    public void chatMessage(AsyncPlayerChatEvent event) {
        if (event.isCancelled())
            return;
        event.setCancelled(message(event.getPlayer(), event.getMessage()));
    }

}
