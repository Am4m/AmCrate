package com.amam.amcrate.crate.inventory;

import com.amam.amcrate.utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class CrateInventory {

    private final int rewardSlot;
    private final CratePreset type;
    private final Material overlay;
    private final Material pointerMaterial;
    private final int[] pointerSlot;
    private final Component display;
    final int max;
    final int row;
    final int size;

    public CrateInventory(@NotNull Component display, @NotNull CratePreset type, int rewardSlot, @NotNull Material overlay, @NotNull Material pointerMaterial, int... pointerSlot) {
        this.rewardSlot = rewardSlot;
        this.type = type;
        this.overlay = overlay;
        this.pointerMaterial = pointerMaterial;
        this.display = display;
        this.pointerSlot = pointerSlot;

        max = Math.max(Arrays.stream(pointerSlot).max().orElse(0), Arrays.stream(type.slots()).max().orElse(6));
        row = Math.ceilDiv(max, 9);
        size = row * 9;
    }

    public Inventory getInventory() {

        Inventory inventory = Bukkit.createInventory(null, size, display);
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

    public CratePreset getType() {
        return type;
    }

    public Component getDisplay() {
        return display;
    }
}
