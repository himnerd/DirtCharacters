package com.dirt.characters.commands;

import com.dirt.characters.DirtCharacters;
import com.dirt.characters.data.CharacterData;
import com.dirt.characters.inventory.impl.PlayerDashboardGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class MenuCommand implements CommandExecutor, TabCompleter {
    private final DirtCharacters plugin;

    public MenuCommand(DirtCharacters plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        CharacterData data = plugin.getCharacterManager().getCharacter(player.getUniqueId());
        if (data == null) {
            player.sendMessage("§cCreate an active character before opening your dashboard.");
            return true;
        }

        plugin.getPlatformScheduler().runAtEntity(player, () -> {
            if (player.isOnline()) {
                plugin.getGUIManager().openGUI(new PlayerDashboardGUI(plugin), player);
            }
        });
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}