package com.amam.amcrate.utils;

import com.amam.amcrate.AmCrate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

import static com.amam.amcrate.utils.Utils.replaceIfNotNull;

public class Messages {

    private static Component PREFIX;
    private static Component NO_KEYS;
    private static Component REWARD;

    private static final Plugin plugin = AmCrate.plugin();

    public static void loadMessages() {
        var file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            plugin.getLogger().warning("messages.yml does not exist!");
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        PREFIX = LegacyComponentSerializer.legacyAmpersand().deserialize(config.getString("prefix", "&c[ &rAmCrate &c] "));
        NO_KEYS = LegacyComponentSerializer.legacyAmpersand().deserialize(config.getString("no-keys", "&c You do not have %crate% keys !"));
        REWARD = LegacyComponentSerializer.legacyAmpersand().deserialize(config.getString("reward", "&a You get %reward% from %crate%"));
    }

    public static Component getPrefix(Component crate, Component reward) {
        Component prefix = replaceIfNotNull(PREFIX, "%crate%", crate);
        prefix = replaceIfNotNull(prefix, "%reward%", reward);
        return prefix;
    }

    public static Component getNoKeys(Component crate, Component reward) {
        Component noKeys = replaceIfNotNull(NO_KEYS, "%crate%", crate);
        noKeys = replaceIfNotNull(noKeys, "%reward%", reward);
        return noKeys;
    }

    public static Component getReward(Component crate, Component reward) {
        Component rewardText = replaceIfNotNull(REWARD, "%crate%", crate);
        rewardText = replaceIfNotNull(rewardText, "%reward%", reward);
        return rewardText;
    }
}
