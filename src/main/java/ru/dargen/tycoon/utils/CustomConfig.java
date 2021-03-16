package ru.dargen.tycoon.utils;

import lombok.Getter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import ru.dargen.tycoon.Tycoon;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

public abstract class CustomConfig {

    public @Getter static Map<String, CustomConfig> configMap = new HashMap<>();

    private File configFile;
    private YamlConfiguration config;

    public CustomConfig(String path) {
        configMap.put(path.toLowerCase(), this);
        configFile = new File(Tycoon.getInstance().getDataFolder()+ "/" + path.toLowerCase() + ".yml");
        load();
    }

    public void load() {
        try {
            if(!configFile.exists()){
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();
                config = YamlConfiguration.loadConfiguration(configFile);
                setDefault();
                config.load(configFile);
                onLoad();
                return;
            }
            config = YamlConfiguration.loadConfiguration(configFile);
            config.load(configFile);
            onLoad();
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public YamlConfiguration get() {
        return config;
    }

    public CustomConfig set(String path, Object obj) {
        config.set(path, obj);
        return this;
    }

    public static CustomConfig getConfig(String path) {
        return configMap.get(path.toLowerCase());
    }

    public static CustomConfig createConfig(String path,
                                            Consumer<CustomConfig> def,
                                            Consumer<CustomConfig> load) {
        return new CustomConfig(path) {

            public void setDefault() {
                if (def != null)
                    def.accept(this);
            }

            public void onLoad() {
                if (load != null)
                    load.accept(this);
            }

        };
    }
    public abstract void setDefault();

    public abstract void onLoad();

}
