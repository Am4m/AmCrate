package com.amam.amcrate.crate.inventory;

import com.amam.amcrate.AmCrate;
import com.amam.amcrate.crate.CrateManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class CrateListener implements Listener {

    @EventHandler
    public void onCrateClick(InventoryClickEvent e) {
        if (CrateManager.isPlayerOpeningCrate(e.getWhoClicked().getName()))e.setCancelled(true);
    }

    @EventHandler
    public void onCrateClose(InventoryCloseEvent e) {
        if (CrateManager.isPlayerOpeningCrate(e.getPlayer().getName()) && !((Player) e.getPlayer()).isOnline()) {
            Bukkit.getScheduler().runTaskLater(AmCrate.plugin, () -> {
                e.getPlayer().openInventory(e.getInventory());
            }, 1L);
        }
    }

}
