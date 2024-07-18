package com.amam.amcrate.command;

import com.amam.amcrate.crate.CrateManager;
import com.amam.amcrate.utils.Messages;
import com.amam.amcrate.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;

public class KeyCommand implements TabExecutor {

    private final Map<String, BiConsumer<CommandSender, String[]>> subCommands = new HashMap<>();
    private final List<String> subCommandList = new ArrayList<>();
    private final Component PREFIX = Messages.getPrefix(null, null);

    public KeyCommand() {
        addSubCommand((sender, args) -> {
            if (args.length > 3) {
                var crate = CrateManager.getCrate(args[1]);
                if (crate != null) {
                    var p = Bukkit.getPlayerExact(args[2]);
                    if (p != null) {
                        var amount = Integer.parseInt(args[3]);
                        crate.setKeys(p, amount);
                        Utils.saveKeys(p.getName(), crate.getId(), amount);
                        sender.sendMessage(PREFIX.append(Component.text("Set " + p.getName() + " " + crate.getId() + " keys to " + args[3], NamedTextColor.WHITE)));
                    } else sender.sendMessage(PREFIX.append(Component.text("No player named " + p.getName(), NamedTextColor.WHITE)));
                } else sender.sendMessage(PREFIX.append(Component.text("There's no crate named " + args[1], NamedTextColor.WHITE)));
            } else sender.sendMessage("Usage: /key set <crate> <player> <amount>");
        }, "set");

        addSubCommand((sender, args) -> {
            if (args.length > 3) {
                var crate = CrateManager.getCrate(args[1]);
                if (crate != null) {
                    var p = Bukkit.getPlayerExact(args[2]);
                    if (p != null) {
                        var amount = Integer.parseInt(args[3]);
                        crate.sumKeys(p, amount);
                        Utils.saveKeys(p.getName(), crate.getId(), amount);
                        sender.sendMessage(PREFIX.append(Component.text("Sum " + p.getName() + " " + crate.getId() + " keys to " + args[3], NamedTextColor.WHITE)));
                    } else sender.sendMessage(PREFIX.append(Component.text("No player named " + p.getName(), NamedTextColor.WHITE)));
                } else sender.sendMessage(PREFIX.append(Component.text("There's no crate named " + args[1], NamedTextColor.WHITE)));
            } else sender.sendMessage("Usage: /key sum <crate> <player> <amount>");
        }, "sum");

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!sender.hasPermission("amamcrate.key")) return false;
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

        if (args.length == 2)
            return StringUtil.copyPartialMatches(args[1], CrateManager.getCratesId(), matches);

        if (args.length == 3)
            return null;

        return Collections.emptyList();
    }

    private void addSubCommand(BiConsumer<CommandSender, String[]> action, String key) {
        subCommands.put(key, action);
        subCommandList.add(key.split(" ")[0]);
    }

}
