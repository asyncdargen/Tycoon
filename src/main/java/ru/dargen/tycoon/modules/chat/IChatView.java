package ru.dargen.tycoon.modules.chat;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public interface IChatView {

    TextComponent getFormat(Player player, String message);

    boolean canView(Player sender, Player view);

}
