package com.dirt.characters.commands;

import com.dirt.characters.DirtCharacters;
import com.dirt.characters.inventory.impl.travel.FastTravelGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FastTravelCommand implements CommandExecutor {
    private final DirtCharacters plugin;

    public FastTravelCommand(DirtCharacters plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use fast travel.");
            return true;
        }

        plugin.getGUIManager().openGUI(new FastTravelGUI(plugin, 0), player);
        return true;
    }
}