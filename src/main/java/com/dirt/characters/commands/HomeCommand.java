package com.dirt.characters.commands;

import com.dirt.api.DirtApi;
import com.dirt.api.NationsService;
import com.dirt.characters.DirtCharacters;
import com.dirt.characters.data.CharacterData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class HomeCommand implements CommandExecutor, TabCompleter {
    private final DirtCharacters plugin;

    public HomeCommand(DirtCharacters plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("\u00a7cOnly players can use this command.");
            return true;
        }

        CharacterData character = plugin.getCharacterManager().getCharacter(player.getUniqueId());
        if (character == null) {
            player.sendMessage("\u00a7cYou need an active character to use this.");
            return true;
        }

        if (label.equalsIgnoreCase("sethome")) {
            return handleSetHome(player, character);
        } else {
            return handleHome(player, character);
        }
    }

    private boolean handleSetHome(Player player, CharacterData character) {
        Location loc = player.getLocation();

        // Territory rules belong to DirtNations. Without it there is no territory
        // to restrict, so /sethome is unrestricted on a standalone install.
        NationsService nations = DirtApi.nations();
        if (nations != null) {
            String denial = nations.getSetHomeDenial(player, loc);
            if (denial != null) {
                player.sendMessage(denial);
                return true;
            }
        }

        String homeStr = loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ()
                + "," + loc.getYaw() + "," + loc.getPitch();
        character.setHomeLocation(homeStr);
        plugin.getCharacterManager().saveCharacter(character);
        player.sendMessage("\u00a7aHome set at your current location.");
        return true;
    }

    private boolean handleHome(Player player, CharacterData character) {
        String homeStr = character.getHomeLocation();
        if (homeStr == null || homeStr.isEmpty()) {
            player.sendMessage("\u00a7cYou haven't set a home yet. Use \u00a7e/sethome\u00a7c first.");
            return true;
        }

        try {
            String[] parts = homeStr.split(",");
            World world = Bukkit.getWorld(parts[0]);
            if (world == null) {
                player.sendMessage("\u00a7cHome world no longer exists.");
                return true;
            }
            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double z = Double.parseDouble(parts[3]);
            float yaw = parts.length > 4 ? Float.parseFloat(parts[4]) : 0;
            float pitch = parts.length > 5 ? Float.parseFloat(parts[5]) : 0;
            plugin.getPlatformScheduler().teleport(player, new Location(world, x, y, z, yaw, pitch),
                    () -> player.sendMessage("\u00a7aTeleported to your home."));
        } catch (Exception e) {
            player.sendMessage("\u00a7cFailed to teleport to home. Try setting it again with \u00a7e/sethome\u00a7c.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}