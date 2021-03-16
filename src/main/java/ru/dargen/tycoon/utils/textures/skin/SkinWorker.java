package ru.dargen.tycoon.utils.textures.skin;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_12_R1.EntityPlayer;

public class SkinWorker {

    public static GameProfile applySkin(GameProfile profile, String name){
        return getSkin(name).applyToProfile(profile);
    }

    public static EntityPlayer applySkin(EntityPlayer player, String skin){
        return getSkin(skin).applyToEntityPlayer(player);
    }

    public static Skin getSkin(String name){
        return new Skin(name);
    }
}
