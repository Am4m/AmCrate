package com.amam.amcrate.crate;

import com.amam.amcrate.utlis.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class CrateInventory {

    private final int rewardSlot;
    private final CrateType type;
    private final Material overlay;
    private final Material pointerMaterial;
    private final int[] pointerSlot;

    public CrateInventory(@NotNull CrateType type, int rewardSlot,@NotNull Material overlay,@NotNull Material pointerMaterial, int... pointerSlot) {
        this.rewardSlot = rewardSlot;
        this.type = type;
        this.overlay = overlay;
        this.pointerMaterial = pointerMaterial;
        this.pointerSlot = pointerSlot;
    }

    public Inventory getInventory() {
        final int max = Math.max(Arrays.stream(pointerSlot).max().orElse(0), Arrays.stream(type.slots()).max().orElse(6));
        final int row = Math.ceilDiv(max, 9);
        final int size = row * 9;
        Inventory inventory = Bukkit.createInventory(null, size);
        final ItemStack[] contents = new ItemStack[size];
        final ItemStack item = Utils.createItem(overlay, Component.empty());
        Arrays.fill(contents, item);
        inventory.setContents(contents);
        for (int slot : pointerSlot) {
            inventory.setItem(slot, Utils.createItem(pointerMaterial, Component.empty()));
        }
        for (int i: type.slots()) inventory.setItem(i, new ItemStack(Material.AIR));
        return inventory;
    }

    public int getRewardSlot() {
        return rewardSlot;
    }

    public CrateType getType() {
        return type;
    }
}
