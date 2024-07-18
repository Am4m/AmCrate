package com.amam.amcrate.crate;

import com.amam.amcrate.AmCrate;
import com.amam.amcrate.crate.inventory.CratePreset;
import com.amam.amcrate.utils.Messages;
import com.amam.amcrate.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CrateManager {

    public static final Set<Crate> CRATES = new HashSet<>();
    private static final Map<CratePosition, Crate> CRATE_BLOCKS = new HashMap<>();
    private static final Map<Player, Crate> playerOpeningCrate = new HashMap<>();
    private static final Set<CratePosition> CRATES_LOCATION = CRATE_BLOCKS.keySet();

    public static Set<String> getCratesId() {
        Set<String> cratesId = new HashSet<>();
        CRATES.forEach(crate -> cratesId.add(crate.getId()));
        return cratesId;
    }

    public static Crate getCrate(String id) {
        for (Crate crate : CRATES) {
            if (crate.getId().equalsIgnoreCase(id)) {
                return crate;
            }
        }
        return null;
    }

    public static Set<CratePosition> getCratesPosition(){
        return CRATES_LOCATION;
    }

    public static Crate getCrate(CratePosition position) {
        return CRATE_BLOCKS.get(position);
    }

    public static void addCratePosition(Crate crate) {
        CRATE_BLOCKS.put(crate.getPosition(), crate);
    }

    public static boolean isPlayerOpeningCrate(Player player) {
        return playerOpeningCrate.containsKey(player);
    }

    public static void removeCrate(String id) {
        CRATES.remove(id);
    }

    public static boolean openCrate(Crate crate, Player player) {
        if (!crate.hasKey(player)) {
            player.sendMessage(Messages.getPrefix(Component.text(crate.getId()), Component.empty()).append(Messages.getNoKeys(Component.text(crate.getId()), Component.empty())));
            return false;
        }
        playerOpeningCrate.put(player, crate);
        Inventory inventory = crate.getCrateInventory().getInventory();
        int[] slots = crate.getCrateInventory().getType().slots();

        ItemStack[] rewards = new ItemStack[slots.length * 2];
        for (int i = 0; i < rewards.length; i++)
            rewards[i] = getRandomReward(crate.getRewards()).itemStack();
        player.openInventory(inventory);
        startRecursiveTask(player, inventory, rewards, slots, 0, 1L, crate.getCrateInventory().getRewardSlot());
        return true;
    }

    private static void startRecursiveTask(Player player, Inventory inventory, ItemStack[] rewards, int[] slots, int count, long delay, int rewardSlot) {

        if (!player.isOnline()) return;
        if (delay > 9) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                var reward = inventory.getItem(rewardSlot);
                player.getInventory().addItem(reward);
                var crate = playerOpeningCrate.get(player);
                crate.sumKeys(player, -1);
                Utils.saveKeys(player.getName(), crate.getId(), crate.getKeys(player));
                playerOpeningCrate.remove(player);
                player.closeInventory();
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                player.sendMessage(Messages.getPrefix(Component.text(crate.getId()), reward.displayName())
                        .append(Messages.getReward(Component.text(crate.getId()), reward.displayName())));
            });
            return;
        }

        final int slotsLength = slots.length;

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

        Bukkit.getScheduler().runTaskLaterAsynchronously(AmCrate.plugin(), () -> startRecursiveTask(player, inventory, rewards, slots, finalCount, finalDelay, rewardSlot), finalDelay);
    }

    private static final Plugin plugin = AmCrate.plugin();

    private static Reward getRandomReward(List<Reward> rewards) {

        int totalWeight = rewards.stream().mapToInt(Reward::chance).sum();
        int randomIndex = (int)(Math.random() * totalWeight);

        int currentWeightSum = 0;
        for (Reward reward : rewards) {
            currentWeightSum += reward.chance();

            if (currentWeightSum > randomIndex) {
                return reward;
            }
        }
        return rewards.get((int) (Math.random() * rewards.size()));
    }

    public static void createConfig(Crate crate) {
        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdir();
        var file = new File(plugin.getDataFolder() + File.separator + "crates", crate.getId() +".yml");
        if (!file.getParentFile().exists()) file.getParentFile().mkdir();
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (IOException ignored) {}
        }
        var config = YamlConfiguration.loadConfiguration(file);

        config.set("id", crate.getId());
        config.set("display", LegacyComponentSerializer.legacyAmpersand().serialize(crate.getCrateInventory().getDisplay()));
        config.set("preset", "horizontal");
        try {
            config.save(file);
        } catch (IOException ignored) {}

    }

    public static void loadConfig() {
        File cratesFolder = new File(plugin.getDataFolder(), "crates");
        if (!cratesFolder.exists() || !cratesFolder.isDirectory()) {
            plugin.getLogger().warning("Crates folder does not exist or is not a directory.");
            plugin.getLogger().warning("Creating a new one !");
            cratesFolder.mkdir();
            return;
        }

        File[] files = cratesFolder.listFiles();
        if (files == null) return;
        CRATES.clear();
        CRATES_LOCATION.clear();
        for (File file : files) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);

            String id = config.getString("id");
            String displayStr = config.getString("display");
            String presetStr = config.getString("preset", CratePreset.Type.HORIZONTAL.name()).toUpperCase();
            Component display = (displayStr != null) ? LegacyComponentSerializer.legacyAmpersand().deserialize(displayStr) : null;

            if (id == null || display == null) {
                plugin.getLogger().warning("Invalid configuration in " + file.getName() + ": missing id or display.");
                continue;
            }

            CratePreset.Type preset;
            try {
                preset = CratePreset.Type.valueOf(presetStr);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid preset type in " + file.getName() + ": " + presetStr);
                preset = CratePreset.Type.HORIZONTAL;
            }

            Crate crate = switch (preset) {
                case CIRCLE -> Crate.createCircle(id, display);
                case SNAKE -> Crate.createSnake(id, display);
                default -> Crate.createHorizontal(id, display);
            };

            try {
                var rewardSection = config.getConfigurationSection("rewards");
                if (rewardSection != null) {
                    for (String rewardId : rewardSection.getKeys(false)) {
                        var item = config.getItemStack("rewards." + rewardId + ".items");
                        int chance = config.getInt("rewards." + rewardId + ".chance");
                        if (item != null) {
                            crate.addReward(new Reward(item, chance));
                        } else {
                            plugin.getLogger().warning("Missing item for reward " + rewardId + " in " + file.getName());
                        }
                    }
                } else {
                    plugin.getLogger().warning("No rewards section found in " + file.getName());
                }

                String worldName = config.getString("block.world");
                int x = config.getInt("block.x");
                int y = config.getInt("block.y");
                int z = config.getInt("block.z");

                if (worldName != null) {
                    World world = Bukkit.getWorld(worldName);
                    if (world != null) {
                        CratePosition position = new CratePosition(world, x, y, z);
                        crate.setPosition(position);
                        CRATE_BLOCKS.put(position, crate);
                    } else {
                        plugin.getLogger().warning("World not found: " + worldName);
                    }
                } else {
                    plugin.getLogger().warning("Missing block world in " + file.getName());
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load crate data from " + file.getName() + ": " + e.getMessage());
            }
            CRATES.add(crate);
            addCratePosition(crate);
        }
    }

    public static FileConfiguration getConfig(String id) {
        var file = new File(plugin.getDataFolder() + File.separator + "crates", id +".yml");
        if (file.exists()) {
            return YamlConfiguration.loadConfiguration(file);
        }
        return null;
    }

    public static void saveConfig(Crate crate){

        var rewards = crate.getRewards();
        var size = rewards.size();
        var position = crate.getPosition();
        var config = getConfig(crate.getId());

        if(config == null) createConfig(crate);
        if (position != null) {
            config.set("block.world", position.world().getName());
            config.set("block.x", position.blockX());
            config.set("block.y", position.blockY());
            config.set("block.z", position.blockZ());
        } else plugin.getLogger().warning("Cant save position");

        config.set("rewards", null);
        for (int i = 0; i < size; i++) {
            Reward reward = rewards.get(i);
            config.set("rewards."+ i +".items", reward.itemStack());
            config.set("rewards."+ i +".chance", reward.chance());
        }
        try {
            config.save(new File(AmCrate.plugin().getDataFolder() + File.separator + "crates", crate.getId() +".yml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
