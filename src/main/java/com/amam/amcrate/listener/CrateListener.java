package com.amam.amcrate.listener;

import com.amam.amcrate.AmCrate;
import com.amam.amcrate.crate.CrateManager;
import com.amam.amcrate.crate.CratePosition;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.io.File;

public class CrateListener implements Listener {

    private AmCrate plugin = AmCrate.plugin();
    private FileConfiguration config;

    public CrateListener() {
        var file = new File(plugin.getDataFolder(), "keys.yml");
        if (!file.exists()) {
            return;
        }
        config = YamlConfiguration.loadConfiguration(file);
    }


    @EventHandler
    public void onCrateClick(InventoryClickEvent e) {
        if (CrateManager.isPlayerOpeningCrate((Player) e.getWhoClicked())) e.setCancelled(true);
    }

    @EventHandler
    public void onCrateClose(InventoryCloseEvent e) {
        if (CrateManager.isPlayerOpeningCrate((Player) e.getPlayer())) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (((Player) e.getPlayer()).isOnline())
                    e.getPlayer().openInventory(e.getInventory());
            });
        }
    }

    @EventHandler
    public void onCrateRightClick(PlayerInteractEvent e) {
        if (e.getAction().isRightClick()) {
            if(e.getClickedBlock() != null && e.getHand() == (EquipmentSlot.HAND)) {
                var crate = CrateManager.getCrate(CratePosition.fromLocation(e.getClickedBlock().getLocation()));
                if (crate == null) return;
                var player = e.getPlayer();
                e.setCancelled(true);
                if (!CrateManager.openCrate(crate, player)) {
                    player.setVelocity(player.getLocation().getDirection().multiply(-1));
                };
            }
        } else {
            if(e.getClickedBlock() != null && e.getHand() == (EquipmentSlot.HAND)) {
                var crate = CrateManager.getCrate(CratePosition.fromLocation(e.getClickedBlock().getLocation()));
                if (crate == null) return;

            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
            Player player = e.getPlayer();
            var playerSection = config.getConfigurationSection(player.getName());
            if (playerSection != null) {
                for (String crate : playerSection.getKeys(false)) {
                    var amount = playerSection.getInt(crate);
                    CrateManager.getCrate(crate).setKeys(player, amount);
                    e.getPlayer().sendMessage(String.valueOf(amount));
                }
            }

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {

    }
}
