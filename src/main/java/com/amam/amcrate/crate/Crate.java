package com.amam.amcrate.crate;

import com.amam.amcrate.crate.inventory.CrateInventory;
import com.amam.amcrate.crate.inventory.CratePreset;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class Crate {

    private final String id;
    private final List<Reward> rewards;
    private CrateInventory crateInventory;
    // player name
    private final Map<UUID, Integer> playerKeys;

    public static Crate createHorizontal(String id, Component displayName) {
        return new Crate(id, new CrateInventory(displayName, CratePreset.Type.HORIZONTAL.getPreset(), 13, Material.BLACK_STAINED_GLASS_PANE, Material.GREEN_STAINED_GLASS_PANE, 4, 22));
    }

    public static Crate createCircle(String id, Component displayName) {
        return new Crate(id, new CrateInventory(displayName,CratePreset.Type.CIRCLE.getPreset(), 20, Material.BLACK_STAINED_GLASS_PANE, Material.GREEN_STAINED_GLASS_PANE, 19, 21));
    }

    public static Crate createSnake(String id, Component displayName) {
        return new Crate(id, new CrateInventory(displayName, CratePreset.Type.SNAKE.getPreset(), 22, Material.BLACK_STAINED_GLASS_PANE, Material.GREEN_STAINED_GLASS_PANE, 21, 23));
    }


    Crate(@NotNull String id, @NotNull CrateInventory crateInventory) {
        this.id = id;
        this.rewards = new ArrayList<>();
        this.crateInventory = crateInventory;

        this.playerKeys = new HashMap<>();
    }

    public boolean hasKey(UUID uuid) {
        return playerKeys.getOrDefault(uuid, 0) > 0;
    }

    public void setKeys(UUID uuid, int keys) {
        playerKeys.put(uuid, keys);
    }

    public String getId() {
        return id;
    }

    public void addReward(Reward reward) {
        rewards.add(reward);
    }

    public List<Reward> getRewards() {
        return rewards;
    }

    public void removeReward(int index) {
        rewards.remove(index);
    }

    public CrateInventory getCrateInventory() {
        return crateInventory;
    }

    public void setCrateInventory(CrateInventory inventory) {
        this.crateInventory = inventory;
    }
}
