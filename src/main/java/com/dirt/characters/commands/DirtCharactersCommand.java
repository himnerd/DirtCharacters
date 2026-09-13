package com.dirt.characters.commands;

import com.dirt.characters.DirtCharacters;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** {@code /dirtcharacters} — admin surface for this plugin. */
public class DirtCharactersCommand implements CommandExecutor, TabCompleter {

    private final DirtCharacters plugin;

    public DirtCharactersCommand(DirtCharacters plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("dirtcharacters.reload")) {
                sender.sendMessage("§cYou don't have permission to do that.");
                return true;
            }
            plugin.reloadData();
            sender.sendMessage("§aDirtCharacters configuration and caches reloaded.");
            return true;
        }

        sender.sendMessage("§eUsage: §f/" + label + " reload");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && sender.hasPermission("dirtcharacters.reload")) {
            List<String> options = new ArrayList<>(List.of("reload"));
            options.removeIf(s -> !s.startsWith(args[0].toLowerCase()));
            return options;
        }
        return Collections.emptyList();
    }
}
