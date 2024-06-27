package com.amam.amcrate.crate;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public record Reward(@NotNull ItemStack itemStack, int chance){
}
