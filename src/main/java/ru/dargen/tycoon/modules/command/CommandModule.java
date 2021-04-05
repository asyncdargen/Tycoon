package ru.dargen.tycoon.modules.command;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.inventory.ItemStack;
import ru.dargen.tycoon.modules.Module;
import ru.dargen.tycoon.modules.chat.Prefix;
import ru.dargen.tycoon.modules.command.args.Argument;
import ru.dargen.tycoon.modules.command.args.StringArgument;
import ru.dargen.tycoon.modules.command.commands.MenuCommand;
import ru.dargen.tycoon.modules.command.commands.TycoonCommand;
import ru.dargen.tycoon.modules.command.ctx.CommandContext;
import ru.dargen.tycoon.modules.command.enums.SenderType;
import ru.dargen.tycoon.modules.command.requirements.PermissionRequirement;
import ru.dargen.tycoon.modules.item.IItemModule;
import ru.dargen.tycoon.utils.reflect.ReflectUtil;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class CommandModule extends Module implements ICommandModule {

    private final String TAG = "tycoon";
    private @Getter Map<String, CommandRegister> registeredCommands = new HashMap<>();
    private Map<UUID, Long> cooldowns;
    private @Getter CommandMap commandMap;
    private Map<String, org.bukkit.command.Command> knownCommands;

    public void enable() throws Exception {
        commandMap = ReflectUtil.getFieldValue(Bukkit.getServer(), Bukkit.getServer().getClass().getDeclaredField("commandMap"));
        registeredCommands = new HashMap<>();
        knownCommands = ReflectUtil.getFieldValue(commandMap, commandMap.getClass().getDeclaredField("knownCommands"));
        cooldowns = new HashMap<>();
        registerListener();
        unregisterBlocked();
        registerDefaultCommands();
    }

    private void registerDefaultCommands() {
        registerCommand(new TycoonCommand());
        registerCommand(new MenuCommand());
        registerCommand(new Command("help", new String[]{"?", "помощь", "помогите"}, "Выводит список доступных команд") {
            public void run(CommandContext ctx) {
                ctx.sendMessage(getHelp(ctx.getSender()));
            }
        });
    }

    public String getHelp(CommandSender sender) {
        BiConsumer<Command, StringBuilder> add = (c,s) -> {
            s.append("   /").append(c.getName()).append(" ");
            if (!c.getArguments().isEmpty())
                for (Argument arg : c.getArguments()) {
                    boolean rq = arg.isRequired();
                    s.append((rq ? "§7[§a" : "§7<§a") + arg.getName().toUpperCase() + (rq ? "§7] §r" : "§7> §r"));
                }
            s.append("§7- §f").append(c.getDescription()).append(" §r\n");
        };
        StringBuilder help = new StringBuilder()
            .append("§eПомощь по командам\n")
            .append("§6================================================§r\n");
        for (Command command : getRegisteredCommands().values().stream().map(CommandRegister::getCommand).collect(Collectors.toList())) {
            boolean canExec = true;
            if (command.getRequirement() !=  null)
                canExec = command.getRequirement().canExecute(sender);
            if (canExec)
                add.accept(command, help);
        }
        help.append("§6================================================");
        return help.toString();
    }

    public void disable() throws Exception {
        unRegisterListener();
        cooldowns.clear();
        registeredCommands.clear();
    }

    public void registerCommand(Command command) {
        CommandRegister register;
        registeredCommands.put(command.getName(), register = new CommandRegister(command));
        commandMap.register(TAG, register);
    }

    public void unregisterCommand(String name) {
        commandMap.getCommand(name.toLowerCase()).unregister(commandMap);
    }

    public void unregisterBlocked() {
        String[] blocked = {"pl", "plugins", "ver", "version", "about", "spigot", "tps", "restart", "rl", "reload", "help", "?"};
        List<String> toRemove = new ArrayList<>(blocked.length * 3);
        toRemove.addAll(Arrays.asList(blocked));
        toRemove.addAll(Arrays.stream(blocked).map("bukkit:"::concat).collect(Collectors.toList()));
        toRemove.addAll(Arrays.stream(blocked).map("minecraft:"::concat).collect(Collectors.toList()));
        toRemove.addAll(Arrays.stream(blocked).map("spigot:"::concat).collect(Collectors.toList()));
        knownCommands.keySet().removeAll(toRemove);
    }

    public boolean cooldown(UUID uuid) {
        if (cooldowns.containsKey(uuid))
            return true;
        cooldowns.put(uuid, System.currentTimeMillis());
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            cooldowns.remove(uuid);
        });
        return false;
    }

    @EventHandler
    public void command(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled())
            return;
        if (!event.getPlayer().hasPermission("command.bypass"))
            if (cooldown(event.getPlayer().getUniqueId())) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(Prefix.ERR + "Подождите ещё §c"
                        + String.format("%.1f", (1500 - (System.currentTimeMillis() - cooldowns.get(event.getPlayer().getUniqueId()))) / 1000d)
                        + "§f секунд, прежде чем писать команды");
            }
    }

    @EventHandler
    public void tab(TabCompleteEvent e) {
        if (e.getSender().hasPermission("command.complete"))
            return;
        List<String> complete = new ArrayList<>();
        registeredCommands.values().forEach((c) -> {
            complete.add("/" + c.getName());
            complete.addAll(c.getAliases().stream().map("/"::concat).collect(Collectors.toList()));
        });
        e.setCompletions(complete);
    }
}
