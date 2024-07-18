package com.amam.amcrate.command;

import com.amam.amcrate.crate.Crate;
import com.amam.amcrate.crate.CrateManager;
import com.amam.amcrate.crate.CratePosition;
import com.amam.amcrate.crate.Reward;
import com.amam.amcrate.utils.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;

public class CrateCommand implements TabExecutor {

    private final Map<String, BiConsumer<CommandSender, String[]>> subCommands = new HashMap<>();
    private final List<String> subCommandList = new ArrayList<>();
    private final Component PREFIX = Messages.getPrefix(Component.empty(), Component.empty());

    public CrateCommand() {


        addSubCommand((sender, args) -> {
            Messages.loadMessages();
            CrateManager.loadConfig(); 
            sender.sendMessage(PREFIX.append(Component.text(" Reload Complete", NamedTextColor.WHITE)));
        }, "reload");

        addSubCommand((sender, args) -> {
            if (sender instanceof Player player) {
                if (args.length > 1) {
                    var crate = CrateManager.getCrate(args[1]);
                    if (crate != null) {
                        crate.setPosition(CratePosition.fromLocation(player.getLocation()));
                        player.sendMessage(PREFIX.append(Component.text(" Set crate block at your current location !", NamedTextColor.WHITE)));
                        CrateManager.addCratePosition(crate);
                        CrateManager.saveConfig(crate);
                    } else sender.sendMessage(PREFIX.append(Component.text(" There's no crate named " + args[1], NamedTextColor.WHITE)));
                } else sender.sendMessage("Usage: /crate setblock [name]");
            } else sender.sendMessage("This command can only be run by a player.");
        }, "setblock");

        addSubCommand((sender, args) -> {
            if (args.length > 1) {
                if (CrateManager.getCrate(args[1]) == null) {
                    var crate = Crate.createHorizontal(args[1], Component.text(args[1]));
                    CrateManager.CRATES.add(crate);
                    sender.sendMessage(PREFIX.append(Component.text(" Crate " + args[1] + " created !", NamedTextColor.WHITE)));
                    CrateManager.createConfig(crate);
                } else sender.sendMessage(PREFIX.append(Component.text(" Crate already exist !", NamedTextColor.WHITE)));
            } else sender.sendMessage("Usage: /crate create [name]");

        }, "create");

        addSubCommand((sender, args) -> {
            if (args.length > 1) {
                var crate = CrateManager.getCrate(args[1]);
                if (crate != null) {
                    CrateManager.removeCrate(args[1].toLowerCase());
                    sender.sendMessage(PREFIX.append(Component.text("Crate " + args[1] + " removed !", NamedTextColor.WHITE)));
                } else sender.sendMessage(PREFIX.append(Component.text("There's no crate named " + args[1], NamedTextColor.WHITE)));
            } else sender.sendMessage("Usage: /crate remove [name]");

        }, "remove");

        addSubCommand((sender, args) -> {
            sender.sendMessage("Crate List:");
            for (Crate crate : CrateManager.CRATES) {
                sender.sendMessage(Component.text("-" + crate.getId() + ": ").append(crate.getCrateInventory().getDisplay()));
            }
        }, "list");

        addSubCommand((sender, args) -> {
            if (sender instanceof Player p) {
                if (args.length > 1) {
                    Crate crate = CrateManager.getCrate(args[1]);
                    if (crate != null) {
                        if (!crate.getRewards().isEmpty()) CrateManager.openCrate(crate, p);
                        else
                            p.sendMessage("Please add reward /crate reward <name> add <chance> (It will add your holding item>");
                    } else p.sendMessage(PREFIX.append(Component.text("There's no crate named " + args[1], NamedTextColor.WHITE)));
                } else sender.sendMessage("Usage: /crate open <name>");
            } else sender.sendMessage("This command can only be run by a player.");

        }, "open");

        addSubCommand((sender, args) -> {
            if (sender instanceof Player p) {
                // /crate reward <name>
                if (args.length > 1) {
                    Crate crate = CrateManager.getCrate(args[1]);
                    if (crate != null) {
                        // /crate reward <name> <add|remove> <int>
                        if (args.length > 3) {
                            if (args[2].equalsIgnoreCase("add") && NumberUtils.isDigits(args[3])) {
                                var hand = p.getInventory().getItemInMainHand();
                                if (!hand.getType().isEmpty()) {
                                    crate.addReward(new Reward(p.getInventory().getItemInMainHand(), Integer.parseInt(args[3])));
                                    CrateManager.saveConfig(crate);
                                    p.sendMessage("Reward added");
                                    return;
                                }
                                p.sendMessage(PREFIX.append(Component.text("Please hold an item before executing this command !", NamedTextColor.WHITE)));
                                return;
                            }
                            if (args[2].equalsIgnoreCase("remove") && NumberUtils.isDigits(args[3])) {
                                crate.removeReward(Integer.parseInt(args[3]));
                                CrateManager.saveConfig(crate);
                                p.sendMessage(PREFIX.append(Component.text("Reward removed", NamedTextColor.WHITE)));
                            }
                        } else {
                            List<Reward> rewards = crate.getRewards();
                            for (int i = 0; i < rewards.size(); i++) {
                                p.sendMessage(Component.text("- " + i + ": ").append(rewards.get(i).itemStack().displayName()).append(Component.text(" x" + rewards.get(i).itemStack().getAmount())));
                            }
                        }
                    } else sender.sendMessage(PREFIX.append(Component.text("There's no crate named " + args[1], NamedTextColor.WHITE)));
                } else sender.sendMessage("Usage: /crate reward [name]");
            } else sender.sendMessage("This command can only be run by a player.");
        }, "reward");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!sender.hasPermission("amcrate.crate")) return false;
        if (args.length > 0) {
            BiConsumer<CommandSender, String[]> action = subCommands.get(args[0]);
            if (action != null) {
                action.accept(sender, args);
                return true;
            }
        }
        sender.sendMessage("Usage: /" + label + " <subcommand> [args]");
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        List<String> matches = new ArrayList<>();
        if (args.length == 1)
            return StringUtil.copyPartialMatches(args[0], subCommandList, matches);

        if (args.length == 2 && (args[0].equalsIgnoreCase("reward") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("open") || args[0].equalsIgnoreCase("setblock"))) {
            return StringUtil.copyPartialMatches(args[1], CrateManager.getCratesId(), matches);
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("reward"))
            return StringUtil.copyPartialMatches(args[2], List.of("add", "remove"), matches);

        return Collections.emptyList();
    }

    private void addSubCommand(BiConsumer<CommandSender, String[]> action, String key) {
        subCommands.put(key, action);
        subCommandList.add(key.split(" ")[0]);
    }
}
