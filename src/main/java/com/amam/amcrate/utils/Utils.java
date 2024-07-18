package com.amam.amcrate.utils;

import com.amam.amcrate.AmCrate;
import com.amam.amcrate.crate.Crate;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;

public final class Utils {

    public static ItemStack createItem(Material material, Component name) {
        final ItemStack item = new ItemStack(material);
        final ItemMeta meta = item.getItemMeta();
        meta.displayName(name);
        item.setItemMeta(meta);
        return item;
    }

    public static Component replaceIfNotNull(Component component, String placeholder, Component replacement) {
        if (replacement != null) {
            return component.replaceText(builder -> builder.matchLiteral(placeholder).replacement(replacement));
        }
        return component;
    }

    public static void saveKeys(String userName, String crateId, int amount){
        var file = new File(AmCrate.plugin().getDataFolder(), "keys.yml");
        if (file.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            config.set(userName + "." + crateId, amount);
            try {
                config.save(file);
            } catch (IOException ignored) {}
        } else {
            try {
                file.createNewFile();
                saveKeys(userName, crateId, amount);
            } catch (IOException ignored) {
                ignored.printStackTrace();
            }
        }
    }
}
