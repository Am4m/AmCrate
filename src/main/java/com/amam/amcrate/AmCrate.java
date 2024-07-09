package com.amam.amcrate;

import com.amam.amcrate.command.CrateCommand;
import com.amam.amcrate.command.KeyCommand;
import com.amam.amcrate.crate.CrateManager;
import com.amam.amcrate.listener.CrateListener;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class AmCrate extends JavaPlugin {

    private static AmCrate plugin;
    private static ProtocolManager protocolManager;
    private static boolean useProtocolLib;

    public static AmCrate plugin() {
        return plugin;
    }

    public static ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    public static boolean isUseProtocolLib() {
        return useProtocolLib;
    }

    @Override
    public void onEnable() {
        plugin = this;
        if (getServer().getPluginManager().getPlugin("ProtocolLib") != null) {
            protocolManager = ProtocolLibrary.getProtocolManager();
            useProtocolLib = true;
        } else {
            useProtocolLib = false;
            getLogger().warning("ProtocoLib not found !");
        }
        getLogger().info("AmCrate enabled");
        this.getCommand("crate").setExecutor(new CrateCommand());
        this.getCommand("key").setExecutor(new KeyCommand());
        getServer().getPluginManager().registerEvents(new CrateListener(), this);
        CrateManager.loadConfig();
    }

    @Override
    public void onDisable() {
        getLogger().info("AmCrate disabled");
    }
}
