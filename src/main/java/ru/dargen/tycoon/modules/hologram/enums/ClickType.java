package ru.dargen.tycoon.modules.hologram.enums;

import net.minecraft.server.v1_12_R1.PacketPlayInUseEntity;

public enum ClickType {

    LEFT, RIGHT;

    public static ClickType getByEntityUseAction(PacketPlayInUseEntity.EnumEntityUseAction action) {
        if (action == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK)
            return LEFT;
        return RIGHT;
    }

}
