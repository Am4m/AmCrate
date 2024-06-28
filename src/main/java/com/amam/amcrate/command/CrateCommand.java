package com.amam.amcrate.command;

import com.amam.amcrate.AmCrate;
import com.amam.amcrate.crate.Crate;
import com.amam.amcrate.crate.CrateManager;
import com.amam.amcrate.crate.Reward;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
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

    public CrateCommand() {

        addSubCommand((sender, args) -> {
            if (args.length > 1) {
                if (CrateManager.getCrate(args[1]) == null) {
                    var crate = Crate.createHorizontal(args[1], Component.text(args[1]));
                    CrateManager.addCrate(args[1].toLowerCase(), crate);
                    sender.sendMessage("Crate " + args[1] + " created !");
                    AmCrate.getCrateConfig().createConfig(crate);
                } else sender.sendMessage("Crate already exist !");
            } else sender.sendMessage("Usage: /crate create [name]");

        }, "create");

        addSubCommand((sender, args) -> {
            if (args.length > 1) {
                if (CrateManager.getCrate(args[1]) != null) {
                    CrateManager.removeCrate(args[1].toLowerCase());
                    sender.sendMessage("Crate " + args[1] + " deleted !");
                } else sender.sendMessage("There's no crate named " + args[1]);
            } else sender.sendMessage("Usage: /crate delete [name]");

        }, "remove");

        addSubCommand((sender, args) -> {
            sender.sendMessage("Crate List:");
            for (Crate crate : CrateManager.getCrates()) {
                sender.sendMessage("- " + crate.getId() + ": " + ((TextComponent) crate.getDisplayName()).content());
            }
        }, "list");

        addSubCommand((sender, args) -> {
            if (sender instanceof Player p) {
                if (args.length > 1) {
                    Crate crate = CrateManager.getCrate(args[1]);
                    if (crate != null) {
                        if (!crate.getRewards().isEmpty()) CrateManager.openCrate(crate, p);
                        else
                            p.sendMessage("Please add reward /crate reward add <name> <chance> (It will add your holding item>");
                    } else p.sendMessage("There's no crate named " + args[1]);
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
                                    p.sendMessage("Reward added");
                                    return;
                                }
                                p.sendMessage("Please hold an item before executing this command !");
                                return;
                            }
                            if (args[2].equalsIgnoreCase("remove") && NumberUtils.isDigits(args[3])) {
                                crate.removeReward(Integer.parseInt(args[3]));
                                p.sendMessage("Reward removed");
                            }
                        } else {
                            List<Reward> rewards = crate.getRewards();
                            for (int i = 0; i < rewards.size(); i++) {
                                p.sendMessage(Component.text("- " + i + ": ").append(rewards.get(i).itemStack().displayName()).append(Component.text(" x" + rewards.get(i).itemStack().getAmount())));
                            }
                        }
                    } else sender.sendMessage("There's no crate named " + args[1]);
                } else sender.sendMessage("Usage: /crate reward [name]");
            } else sender.sendMessage("This command can only be run by a player.");


        }, "reward");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!sender.hasPermission("amamcrate")) return false;
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
        if (!sender.hasPermission("amamcrate")) return Collections.emptyList();
        List<String> matches = new ArrayList<>();
        if (args.length == 1)
            return StringUtil.copyPartialMatches(args[0], subCommandList, matches);

        if (args.length == 2 && (args[0].equalsIgnoreCase("reward") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("open"))) {
            List<String> crateList = new ArrayList<>();
            for (Crate crate : CrateManager.getCrates()) crateList.add(crate.getId());
            return StringUtil.copyPartialMatches(args[1], crateList, matches);
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
