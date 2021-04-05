package ru.dargen.tycoon.modules.command.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.dargen.tycoon.modules.chat.Prefix;
import ru.dargen.tycoon.modules.command.Command;
import ru.dargen.tycoon.modules.command.args.DoubleArgument;
import ru.dargen.tycoon.modules.command.args.StringArgument;
import ru.dargen.tycoon.modules.command.ctx.CommandContext;
import ru.dargen.tycoon.modules.command.enums.SenderType;
import ru.dargen.tycoon.modules.command.requirements.PermissionRequirement;
import ru.dargen.tycoon.modules.item.IItemModule;
import ru.dargen.tycoon.modules.player.IPlayerData;
import ru.dargen.tycoon.modules.player.IPlayerModule;

public class TycoonCommand extends Command {

    public TycoonCommand() {
        super("tycoon", new String[]{"такун", "тайкон"}, "Административные команды");
        setRequirement(PermissionRequirement.of("tycoon.admin"));
        addSubCommand(new SubItems());
    }

    public void run(CommandContext ctx) {
        sendHelp(ctx.getSender());
    }

    static class SubEco extends Command {

        public SubEco() {
            super("eco", new String[]{"economy"}, "Управление экономикой");
            addArgument(new StringArgument("prestige/points/money", true, "prestige", "points", "money"));
            addArgument(new StringArgument("set/add/withdraw", true, "set", "add", "withdraw"));
            addArgument(new DoubleArgument("Сумма", true));
            addArgument(new StringArgument("Ник", false));
        }

        public void run(CommandContext ctx) {
            Type type = Type.valueOf(ctx.<String>getArg(0).toUpperCase());
            String method = ctx.<String>getArg(1).toLowerCase();
            double sum = ctx.getArg(2);
            String name;
            if (!ctx.hasArg(3))
                if (ctx.getSenderType() == SenderType.CONSOLE) {
                    ctx.sendMessage(Prefix.ERR + "Вы не можете изменять баланс консоли");
                    return;
                } else
                    name = ctx.getSender().getName();
            else
                name = ctx.getArg(3);
            IPlayerData data = IPlayerModule.get().getPlayer(name);
            if (data == null) {
                ctx.sendMessage(Prefix.ERR + "Игрок офлайн");
                return;
            }
            switch (type) {
                case MONEY: {
                    switch (method) {
                        case "set": {
                            data.setMoney(sum);
                            ctx.sendMessage(Prefix.SUCCESS + "Баланс игрока §a" + name + " §fустановлен на §a" + sum + "$");
                            return;
                        }
                        case "add": {
                            data.addMoney(sum);
                            ctx.sendMessage(Prefix.SUCCESS + "Баланс игрока §a" + name + " §fпополнен на §a" + sum + "$");
                            return;
                        }
                        case "withdraw": {
                            data.withdrawMoney(sum);
                            ctx.sendMessage(Prefix.SUCCESS + "Баланс игрока §a" + name + " §fуменьшен на §c" + sum + "$");
                            return;
                        }
                    }
                }
                case POINS: {
                    switch (method) {
                        case "set": {
                            data.setPoints((int) sum);
                            ctx.sendMessage(Prefix.SUCCESS + "Очки игрока §a" + name + " §fустановлены на §a" + sum);
                            return;
                        }
                        case "add": {
                            data.addPoints((int) sum);
                            ctx.sendMessage(Prefix.SUCCESS + "Очки игрока §a" + name + " §fпополнены на §a" + sum);
                            return;
                        }
                        case "withdraw": {
                            data.withdrawPoints((int) sum);
                            ctx.sendMessage(Prefix.SUCCESS + "Очки игрока §a" + name + " §fуменьшены на §c" + sum);
                            return;
                        }
                    }
                }
                case PRESTIGE: {
                    switch (method) {
                        case "set": {
                            data.setPrestige((int) sum);
                            ctx.sendMessage(Prefix.SUCCESS + "Престиж игрока §a" + name + " §fустановлен на §a" + sum);
                            return;
                        }
                        case "add": {
                            data.addPrestige((int) sum);
                            ctx.sendMessage(Prefix.SUCCESS + "Престиж игрока §a" + name + " §fквеличен на §a" + sum);
                            return;
                        }
                        case "withdraw": {
                            data.withdrawPrestige((int) sum);
                            ctx.sendMessage(Prefix.SUCCESS + "Престиж игрока §a" + name + " §fуменьшен на §c" + sum);
                            return;
                        }
                    }
                }

            }

        }

        enum Type {
            PRESTIGE,
            MONEY,
            POINS;
        }
    }

    class SubItems extends Command {

        public SubItems() {
            super("items", new String[]{"предметы"}, "Действия с предметами");
            addSubCommand(new Command("get", new String[]{"give"}, "Получить предмет по id") {
                {
                    setSender(SenderType.PLAYER);
                    addArgument(new StringArgument("ID Предмета", true));
                    addArgument(new StringArgument("Ник игрока", false));
                }
                public void run(CommandContext ctx) {
                    ItemStack item = IItemModule.get().getItem(ctx.getArg(0));
                    if (item == null) {
                        ctx.sendMessage(Prefix.ERR + "Предмета с id §c" + ctx.getArg(0) + " §fне существует");
                        return;
                    }
                    Player player = (Player) ctx.getSender();
                    String playerName;
                    if (ctx.hasArg(1)) {
                        playerName = ctx.getArg(1);
                        player = Bukkit.getPlayer(playerName);
                        if (player == null && !player.isOnline()) {
                            ctx.sendMessage(Prefix.ERR + "Игрок офлайн");
                            return;
                        }
                    }
                    player.getInventory().addItem(item);
                    ctx.sendMessage(Prefix.SUCCESS + "Предмет выдан §7- §a" + ctx.getArg(0));
                }
            });
            addSubCommand(new Command("list", null, "Просмотреть список доступных предметов") {
                public void run(CommandContext ctx) {
                    ctx.sendMessage("§fСписок предметов §7(§a" + IItemModule.get().getItems().size() + "§7)§f: §2" + String.join("§7, §2", IItemModule.get().getItems().keySet()));
                }
            });
        }

        public void run(CommandContext ctx) {
            sendHelp(ctx.getSender());
        }
    }
}
