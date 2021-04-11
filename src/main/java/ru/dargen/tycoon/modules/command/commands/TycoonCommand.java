package ru.dargen.tycoon.modules.command.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.dargen.tycoon.modules.chat.Prefix;
import ru.dargen.tycoon.modules.command.Command;
import ru.dargen.tycoon.modules.command.args.DoubleArgument;
import ru.dargen.tycoon.modules.command.args.IntegerArgument;
import ru.dargen.tycoon.modules.command.args.StringArgument;
import ru.dargen.tycoon.modules.command.ctx.CommandContext;
import ru.dargen.tycoon.modules.command.enums.SenderType;
import ru.dargen.tycoon.modules.command.requirements.PermissionRequirement;
import ru.dargen.tycoon.modules.item.IItemModule;
import ru.dargen.tycoon.modules.perk.enums.Perk;
import ru.dargen.tycoon.modules.player.IPlayerData;
import ru.dargen.tycoon.modules.player.IPlayerModule;

import java.util.Arrays;

public class TycoonCommand extends Command {

    public TycoonCommand() {
        super("tycoon", new String[]{"такун", "тайкон"}, "Административные команды");
        setRequirement(PermissionRequirement.of("tycoon.admin"));
        addSubCommand(new SubItems());
        addSubCommand(new SubEco());
        addSubCommand(new SubPerks());
    }

    public void run(CommandContext ctx) {
        sendHelp(ctx.getSender());
    }

    static class SubEco extends Command {

        public SubEco() {
            super("eco", new String[]{"economy"}, "Управление экономикой");
            addArgument(new StringArgument("Тип", true, "prestige", "points", "money"));
            addArgument(new StringArgument("Действие", true, "set", "add", "withdraw"));
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
                case POINTS: {
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
                            ctx.sendMessage(Prefix.SUCCESS + "Престиж игрока §a" + name + " §fувеличен на §a" + sum);
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
            POINTS
        }
    }

    class SubItems extends Command {

        public SubItems() {
            super("items", new String[]{"предметы"}, "Действия с предметами");
            addSubCommand(new Command("get", new String[]{"give"}, "Получить предмет по id") {
                {
                    setSender(SenderType.PLAYER);
                    addArgument(new StringArgument("ID Предмета", true));
                    addArgument(new StringArgument("Игрока", false));
                }

                public void run(CommandContext ctx) {
                    ItemStack item = IItemModule.get().getItem(ctx.getArg(0));
                    if (item == null) {
                        ctx.sendMessage(Prefix.ERR + "Предмета с id §c" + ctx.getArg(0) + " §fне существует");
                        return;
                    }
                    String name;
                    if (!ctx.hasArg(1))
                        if (ctx.getSenderType() == SenderType.CONSOLE) {
                            ctx.sendMessage(Prefix.ERR + "Вы можете выдавать предметы только игрокам");
                            return;
                        } else
                            name = ctx.getSender().getName();
                    else
                        name = ctx.getArg(1);
                    IPlayerData data = IPlayerModule.get().getPlayer(name);
                    if (data == null) {
                        ctx.sendMessage(Prefix.ERR + "Игрок офлайн");
                        return;
                    }
                    data.getPlayer().getInventory().addItem(item);
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

    class SubPerks extends Command {

        public SubPerks() {
            super("setperk", null, "Устанока уровня перков");
            addArgument(new IntegerArgument("Уровень", true));
            addArgument(new StringArgument("Перк", true, "speed", "luck", "drones"));
            addArgument(new StringArgument("Игрок", false));
        }

        public void run(CommandContext ctx) {
            int level = ctx.getArg(0);
            Perk perk = Perk.valueOf(ctx.<String>getArg(1).toUpperCase());
            String name;
            if (!ctx.hasArg(2))
                if (ctx.getSenderType() == SenderType.CONSOLE) {
                    ctx.sendMessage(Prefix.ERR + "Вы можете выдавать предметы только игрокам");
                    return;
                } else
                    name = ctx.getSender().getName();
            else
                name = ctx.getArg(2);
            IPlayerData data = IPlayerModule.get().getPlayer(name);
            if (data == null) {
                ctx.sendMessage(Prefix.ERR + "Игрок офлайн");
                return;
            }
            data.getPerks().setPerk(perk, level);
            ctx.sendMessage(Prefix.SUCCESS + "Игроку §a" + data.getName() + "§f выдан §a" + level + " уровень§f перка §a" + perk.getDisplayName());
        }
    }
}
