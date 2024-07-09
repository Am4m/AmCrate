package com.amam.amcrate.crate;

import org.bukkit.Location;
import org.bukkit.World;

public record CratePosition(World world, int blockX, int blockY, int blockZ) {

    public static CratePosition fromLocation(Location location) {
        return new CratePosition(location.getWorld(), location.blockX(), location.blockY(), location.blockZ());
    }

    public Location toLocation() {
        return new Location(world, blockX, blockY, blockZ);
    }
}
