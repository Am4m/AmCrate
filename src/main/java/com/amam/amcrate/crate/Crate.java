package com.amam.amcrate.crate;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class Crate {

    private final String id;
    private Component displayName;
    private final List<Reward> rewards;
    private CrateInventory crateInventory;

    public static Crate createHorizontal(String id, Component displayName) {
        return new Crate(id, displayName, new CrateInventory(CrateType.HORIZONTAL, 13, Material.BLACK_STAINED_GLASS_PANE, Material.GREEN_STAINED_GLASS_PANE, 4, 22));
    }

    public static Crate createCircle(String id, Component displayName) {
        return new Crate(id, displayName, new CrateInventory(CrateType.CIRCLE, 20, Material.BLACK_STAINED_GLASS_PANE, Material.GREEN_STAINED_GLASS_PANE, 19, 21));
    }

    public static Crate createSnake(String id, Component displayName) {
        return new Crate(id, displayName, new CrateInventory(CrateType.SNAKE, 22, Material.BLACK_STAINED_GLASS_PANE, Material.GREEN_STAINED_GLASS_PANE, 21, 23));
    }


    Crate(@NotNull String id, @NotNull Component displayName, @NotNull CrateInventory crateInventory) {
        this.id = id;
        this.displayName = displayName;
        this.rewards = new ArrayList<>();
        this.crateInventory = crateInventory;
    }

    public String getId() {
        return id;
    }

    public Component getDisplayName() {
        return displayName;
    }

    public void setDisplayName(Component displayName) {
        this.displayName = displayName;
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
