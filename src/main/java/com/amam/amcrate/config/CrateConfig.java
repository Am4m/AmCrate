package com.amam.amcrate.config;

import com.amam.amcrate.AmCrate;
import com.amam.amcrate.crate.*;
import com.amam.amcrate.crate.inventory.CratePreset;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

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
        config.set("display", LegacyComponentSerializer.legacyAmpersand().serialize(crate.getCrateInventory().getDisplay()));
        config.set("preset", "horizontal");
        try {
            config.save(file);
        } catch (IOException ignored) {}

    }

    // probably breaking
    public void loadConfig(Plugin plugin) {
        final var files = new File(plugin.getDataFolder() + File.separator + "crates").listFiles();
        if (files == null) return;
        FileConfiguration config;
        String id;
        Component display;
        CratePreset.Type preset;
        Crate crate;
        for (File file : files) {
            config = YamlConfiguration.loadConfiguration(file);
            id = config.getString("id");
            display = LegacyComponentSerializer.legacySection().deserialize(config.getString("display"));
            preset = CratePreset.Type.valueOf(config.getString("preset").toUpperCase());
            switch (preset) {
                case CratePreset.Type.CIRCLE -> crate = Crate.createCircle(id, display);
                case CratePreset.Type.SNAKE -> crate = Crate.createSnake(id, display);
                default -> crate = Crate.createHorizontal(id, display);
            }
            var ids = config.getConfigurationSection("rewards").getKeys(false);
            if (ids.isEmpty()) return;
            for (String rewardId: ids) {
                var item = config.getItemStack("rewards." + rewardId + ".items");
                var chance = config.getInt("rewards." + rewardId + ".chance");
                crate.addReward(new Reward(item, chance));
            }
            CrateManager.addCrate(crate.getId(), crate);
        }

    }

    public FileConfiguration getConfig(String id) {
        var file = new File(plugin.getDataFolder() + File.separator + "crates", id +".yml");
        if (file.exists()) {
            return YamlConfiguration.loadConfiguration(file);
        }
        return null;
    }

    public void saveConfig(String id){
        var crate = CrateManager.getCrate(id);
        var rewards = crate.getRewards();
        var size = rewards.size();
        var config = AmCrate.getCrateConfig().getConfig(id);
        config.set("rewards", null);
        for (int i = 0; i < size; i++) {
            Reward reward = rewards.get(i);
            config.set("rewards."+ i +".items", reward.itemStack());
            config.set("rewards."+ i +".chance", reward.chance());
            try {
                config.save(new File(AmCrate.plugin.getDataFolder() + File.separator + "crates", id +".yml"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
