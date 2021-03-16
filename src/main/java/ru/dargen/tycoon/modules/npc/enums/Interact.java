package ru.dargen.tycoon.modules.npc.enums;

import net.minecraft.server.v1_12_R1.PacketPlayInUseEntity;

public enum Interact {

    ATTACK, CLICK;

    public static Interact getByEntityUseAction(PacketPlayInUseEntity.EnumEntityUseAction action){
        if (action == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK)
            return ATTACK;
        return CLICK;
    }

}
