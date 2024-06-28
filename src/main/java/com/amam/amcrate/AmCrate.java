package com.amam.amcrate;

import com.amam.amcrate.command.CrateCommand;
import com.amam.amcrate.config.CrateConfig;
import org.bukkit.plugin.java.JavaPlugin;

public final class AmCrate extends JavaPlugin {

    public static AmCrate plugin;
    private static CrateConfig config;

    @Override
    public void onEnable() {
        plugin = this;
        getLogger().info("AmCrate enabled");
        this.getCommand("crate").setExecutor(new CrateCommand());
        config = new CrateConfig(this);
//        Crate crate = Crate.createCircle("tes", Component.text("text"));
//        CrateManager.addCrate(crate.getId(), crate);
//        CrateManager.getCrate("tes").addReward(new Reward(new ItemStack(Material.DIAMOND), 50));
//        CrateManager.getCrate("tes").addReward(new Reward(new ItemStack(Material.STICK), 80));
//        CrateManager.getCrate("tes").addReward(new Reward(new ItemStack(Material.NETHERITE_INGOT), 20));
    }

    @Override
    public void onDisable() {
        getLogger().info("AmCrate disabled");
    }

    public static CrateConfig getCrateConfig() {
        return config;
    }
}
