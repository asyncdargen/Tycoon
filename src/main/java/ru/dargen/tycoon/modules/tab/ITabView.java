package ru.dargen.tycoon.modules.tab;

import net.minecraft.server.v1_12_R1.PacketPlayOutScoreboardTeam;
import org.bukkit.entity.Player;

public interface ITabView {

     PacketPlayOutScoreboardTeam generateTeam(Player player);

}
