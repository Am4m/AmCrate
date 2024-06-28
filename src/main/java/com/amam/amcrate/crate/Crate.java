package com.amam.amcrate.crate;

import com.amam.amcrate.AmCrate;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class Crate {

    private final String id;
    private Component displayName;
    private final List<Reward> rewards;
    private CrateInventory crateInventory;

    public static Crate createHorizontal(String id, Component displayName) {
        return new Crate(id, displayName, new CrateInventory(CratePreset.Type.HORIZONTAL.getPreset(), 13, Material.BLACK_STAINED_GLASS_PANE, Material.GREEN_STAINED_GLASS_PANE, 4, 22));
    }

    public static Crate createCircle(String id, Component displayName) {
        return new Crate(id, displayName, new CrateInventory(CratePreset.Type.CIRCLE.getPreset(), 20, Material.BLACK_STAINED_GLASS_PANE, Material.GREEN_STAINED_GLASS_PANE, 19, 21));
    }

    public static Crate createSnake(String id, Component displayName) {
        return new Crate(id, displayName, new CrateInventory(CratePreset.Type.SNAKE.getPreset(), 22, Material.BLACK_STAINED_GLASS_PANE, Material.GREEN_STAINED_GLASS_PANE, 21, 23));
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
        setConfigRewards();
    }

    public List<Reward> getRewards() {
        return rewards;
    }

    public void removeReward(int index) {
        rewards.remove(index);
        setConfigRewards();
    }

    public CrateInventory getCrateInventory() {
        return crateInventory;
    }

    public void setCrateInventory(CrateInventory inventory) {
        this.crateInventory = inventory;
    }

    private void setConfigRewards(){
        var size = rewards.size();
        var config = AmCrate.getCrateConfig().getConfig(id);
        config.set("rewards", null);
        for (int i = 0; i < size; i++) {
            Reward reward = rewards.get(i);
            config.set("rewards."+ i +".items", reward.itemStack());
            config.set("rewards."+ i +".chance", reward.chance());
            try {
                config.save(new File(AmCrate.plugin.getDataFolder() + File.separator + "crates", id +".yml"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
