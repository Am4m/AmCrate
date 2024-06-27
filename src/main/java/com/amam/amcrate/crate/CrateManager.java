package com.amam.amcrate.crate;

import com.amam.amcrate.AmCrate;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrateManager {

    private static final Map<String, Crate> CRATES = new HashMap<>();

    public static Crate[] getCrates() {
        return CRATES.values().toArray(new Crate[0]);
    }

    public static Crate getCrate(String id) {
        return CRATES.get(id);
    }

    public static void addCrate(String id, Crate crate) {
        CRATES.putIfAbsent(id, crate);
    }

    public static void removeCrate(String id) {
        CRATES.remove(id);
    }

    public static void openCrate(Crate crate, Player player) {
        Inventory inventory = crate.getCrateInventory().getInventory();
        int[] slots = crate.getCrateInventory().getType().slots();

        ItemStack[] rewards = new ItemStack[slots.length * 2];
        for (int i = 0; i < rewards.length; i++)
            rewards[i] = getRandomReward(crate.getRewards()).itemStack();
        player.openInventory(inventory);
        startRecursiveTask(player, inventory, rewards, slots, 0, 1L);
    }

    private static void startRecursiveTask(Player player, Inventory inventory, ItemStack[] rewards, int[] slots, int count, long delay) {
        final int slotsLength = slots.length;
        if (delay > 9) return;

        for (int i = 0; i <= count; i++) {
            ItemStack item = rewards[count - i];
            int slot = slots[i % slotsLength];
            inventory.setItem(slot, item);
        }
        player.playSound(player.getLocation(), Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON, 1.0f, 1.4f);
        count++;
        if (delay >= 4) delay++;

        if (count >= slotsLength * 2) {
            count = slotsLength;
            delay = delay < 4 ? delay + 1: delay;
        }

        final int finalCount = count;
        final long finalDelay = delay;

        Bukkit.getScheduler().runTaskLaterAsynchronously(AmCrate.plugin, () -> startRecursiveTask(player, inventory, rewards, slots, finalCount, finalDelay), finalDelay);
    }

    private static Reward getRandomReward(List<Reward> rewards) {
        // Calculate Total chance
        int totalWeight = rewards.stream().mapToInt(Reward::chance).sum();

        // Between 0 - totalWeight
        int randomIndex = (int)(Math.random() * totalWeight);

        int currentWeightSum = 0;
        for (Reward reward : rewards) {
            currentWeightSum += reward.chance();
            // This comparison determines the chance
            if (currentWeightSum > randomIndex) {
                return reward;
            }
        }
        return rewards.get((int) (Math.random() * rewards.size()));
    }
}
