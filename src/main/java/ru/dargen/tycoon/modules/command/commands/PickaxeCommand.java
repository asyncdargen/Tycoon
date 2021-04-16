package ru.dargen.tycoon.modules.command.commands;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.dargen.tycoon.modules.chat.Prefix;
import ru.dargen.tycoon.modules.command.Command;
import ru.dargen.tycoon.modules.command.ctx.CommandContext;
import ru.dargen.tycoon.modules.command.enums.SenderType;
import ru.dargen.tycoon.modules.command.requirements.DonateRequirement;
import ru.dargen.tycoon.modules.item.IItemModule;

public class PickaxeCommand extends Command {

    public PickaxeCommand() {
        super("pickaxe", new String[]{"кирка", "донат-кирка", "donate-pickaxe"}, "Выдаёт донат кирку");
        setRequirement(new DonateRequirement("tycoon.donate.pickaxe"));
        setSender(SenderType.PLAYER);
    }

    public void run(CommandContext ctx) {
        IItemModule items = IItemModule.get();
        Player p = (Player) ctx.getSender();
        for (ItemStack item : p.getInventory()) {
            if (item != null && item.getType() != Material.AIR) {
                if (items.getItemName(item).equals("pickaxe_donate")) {
                    ctx.sendMessage(Prefix.ERR + "У вас уже есть §6Легендарная §fкирка");
                    return;
                }
            }
        }
        p.getInventory().addItem(items.getItem("pickaxe_donate"));
        ctx.sendMessage(Prefix.SUCCESS + "Вы получили §6Легендарную §fкирку");
    }
}
