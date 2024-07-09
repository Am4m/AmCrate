package com.amam.amcrate.listener;

import com.amam.amcrate.AmCrate;
import com.amam.amcrate.crate.CrateManager;
import com.amam.amcrate.crate.CratePosition;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class CrateListener implements Listener {

    @EventHandler
    public void onCrateClick(InventoryClickEvent e) {
        if (CrateManager.isPlayerOpeningCrate((Player) e.getWhoClicked())) e.setCancelled(true);
    }

    @EventHandler
    public void onCrateClose(InventoryCloseEvent e) {
        if (CrateManager.isPlayerOpeningCrate((Player) e.getPlayer())) {
            Bukkit.getScheduler().runTaskLater(AmCrate.plugin(), () -> e.getPlayer().openInventory(e.getInventory()), 1L);
        }
    }

    @EventHandler
    public void onCrateRightClick(PlayerInteractEvent e) {
        if (e.getAction().isRightClick() && !e.getClickedBlock().isEmpty() && e.getHand() == (EquipmentSlot.HAND)) {
            var crate = CrateManager.getCrate(CratePosition.fromLocation(e.getClickedBlock().getLocation()));
            if (crate != null) {
                var player = e.getPlayer();
                e.setCancelled(true);
                CrateManager.openCrate(crate, player);
            }
        }
    }
}
