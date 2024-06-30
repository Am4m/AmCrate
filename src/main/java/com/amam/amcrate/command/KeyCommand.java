package com.amam.amcrate.command;

import com.amam.amcrate.crate.Crate;
import com.amam.amcrate.crate.CrateManager;
import net.kyori.adventure.text.Component;
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

    public KeyCommand() {
        addSubCommand((sender, args) -> {
            if (args.length > 1) {
                var crate = CrateManager.getCrate(args[1]);
                if (crate != null) {
                    var p = Bukkit.getPlayerExact(args[2]);
                    if (p != null) {
                        crate.setKeys(p, Integer.parseInt(args[3]));
                        sender.sendMessage("added");
                    } else sender.sendMessage("null");
                }
            }
        }, "add");

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
