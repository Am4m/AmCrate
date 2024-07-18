package com.amam.amcrate;

import com.amam.amcrate.command.CrateCommand;
import com.amam.amcrate.command.KeyCommand;
import com.amam.amcrate.crate.CrateManager;
import com.amam.amcrate.listener.CrateListener;
import com.amam.amcrate.utils.Messages;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class AmCrate extends JavaPlugin {

    private static AmCrate plugin;

    public static AmCrate plugin() {
        return plugin;
    }


    @Override
    public void onEnable() {
        plugin = this;

        getLogger().info("AmCrate enabled");
        getServer().getPluginManager().registerEvents(new CrateListener(), this);
        CrateManager.loadConfig();
        saveResource("messages.yml", false);
        Messages.loadMessages();

        this.getCommand("crate").setExecutor(new CrateCommand());
        this.getCommand("key").setExecutor(new KeyCommand());
    }

    @Override
    public void onDisable() {
        getLogger().info("AmCrate disabled");
    }
}
