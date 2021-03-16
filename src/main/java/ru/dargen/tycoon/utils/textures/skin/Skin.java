package ru.dargen.tycoon.utils.textures.skin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.server.v1_12_R1.DataWatcherRegistry;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URL;

@ToString
public class Skin {

    private @Getter String name, signature, value;
    private @Getter boolean stable = true;

    public Skin(String name, String signature, String value) {
        this.name = name;
        this.signature = signature;
        this.value = value;
    }

    public Skin(String skin){
        String id = "";

        if (skin.length() > 16)
            id = skin;
        else {
            try {
                id = (String) ((JSONObject) new JSONParser()
                        .parse(IOUtils.
                                toString(new URL("https://api.mojang.com/users/profiles/minecraft/" + skin))))
                        .get("id");
            } catch (ParseException | IOException e) {
                stable = false;
                System.err.println("Error: Skin id not defined");
                return;
            }
        }

        JSONObject profileJSON;

        try {
            profileJSON = ((JSONObject) new JSONParser()
                    .parse(IOUtils.
                            toString(new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + id + "?unsigned=false"))));
        } catch (ParseException | IOException e) {
            stable = false;
            System.err.println("Error: Skin not defined");
            return;
        }

        JSONObject properties = (JSONObject) ((JSONArray) profileJSON.get("properties")).get(0);
        signature = (String) properties.get("signature");
        value = (String) properties.get("value");
    }

    public GameProfile applyToProfile(GameProfile profile){
        if (!stable)
            return profile;
        profile.getProperties().put("textures", new Property("textures", value, signature));
        return profile;
    }

    public EntityPlayer applyToEntityPlayer(EntityPlayer player){
        if (!stable)
            return player;
        Byte b = ~0x01 | ~0x02 | ~0x04 | ~0x08 | ~0x10 | ~0x20 | ~0x40;
        player.getDataWatcher().set(DataWatcherRegistry.a.a(13), (byte) b);
        applyToProfile(player.getProfile());
        return player;
    }
}
