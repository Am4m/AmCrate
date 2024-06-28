package com.amam.amcrate.config;

import com.amam.amcrate.crate.Crate;
import com.amam.amcrate.crate.Reward;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

public final class CrateConfig {

    private final Plugin plugin;

    public CrateConfig(Plugin plugin) {
        this.plugin = plugin;
    }

    public void createConfig(Crate crate) {
        if (!plugin.getDataFolder().exists())plugin.getDataFolder().mkdir();
        var file = new File(plugin.getDataFolder() + File.separator + "crates", crate.getId() +".yml");
        if (!file.getParentFile().exists()) file.getParentFile().mkdir();
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (IOException ignored) {}
        }
        var config = YamlConfiguration.loadConfiguration(file);

        config.set("id", crate.getId());
        config.set("display", LegacyComponentSerializer.legacyAmpersand().serialize(crate.getDisplayName()));
        //config.set("rewards", crate.getRewards());
        config.set("preset", "horizontal");
        try {
            config.save(file);
        } catch (IOException ignored) {}

    }

    // probably broke
    public void loadConfig(Plugin plugin) {
        final var files = new File(plugin.getDataFolder() + File.separator + "crates").listFiles();
        FileConfiguration config;
        String id;
        Component display;
        List<Reward> rewards;
        for (File file : files) {
            config = YamlConfiguration.loadConfiguration(file);
            id = config.getString("id");
            display = LegacyComponentSerializer.legacySection().deserialize(config.getString("display"));
            rewards = (List<Reward>) config.getList("test");
        }

    }

    public FileConfiguration getConfig(String id) {
        var file = new File(plugin.getDataFolder() + File.separator + "crates", id +".yml");
        if (file.exists()) {
            return YamlConfiguration.loadConfiguration(file);
        }
        return null;
    }

    public void saveConfig(String id) {
        var file = new File(plugin.getDataFolder() + File.separator + "crates", id +".yml");
        try {
            YamlConfiguration.loadConfiguration(file).save(file);
        } catch (IOException ignored) {}

    }

}
