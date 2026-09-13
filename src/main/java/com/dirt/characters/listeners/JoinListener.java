package com.dirt.characters.listeners;

import com.dirt.characters.DirtCharacters;
import com.dirt.characters.data.CharacterData;
import com.dirt.characters.inventory.impl.CharacterCreationGUI;
import com.dirt.characters.inventory.impl.ChildSelectionGUI;
import com.dirt.characters.util.CurrencyUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    private final DirtCharacters plugin;

    public JoinListener(DirtCharacters plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();
        var uuid = player.getUniqueId();

        plugin.getPlatformScheduler().runAtEntityLater(player, () -> {
            if (!plugin.getCharacterManager().hasCharacter(uuid)) {
                plugin.getCharacterManager().initPendingCreation(uuid);
                plugin.getCharacterManager().addForcedCreation(uuid);
                plugin.getGUIManager().openGUI(new CharacterCreationGUI(plugin), player);
            } else {
                CharacterData data = plugin.getCharacterManager().getCharacter(uuid);
                boolean changed = false;
                if (data != null && data.getFirstJoinDate() == 0) {
                    data.setFirstJoinDate(System.currentTimeMillis());
                    changed = true;
                }
                if (data != null && data.isPendingChildSelection()) {
                    player.sendMessage("§eYour spouse accepted your child bearing request. Please select a child for your family!");
                    plugin.getGUIManager().openGUI(new ChildSelectionGUI(plugin, 0), player);
                }
                if (data != null) {
                    changed |= handleDailyReward(player, data);
                    if (changed) {
                        plugin.getCharacterManager().saveCharacter(data);
                    }
                }
            }
        }, plugin.getSettings().getJoinDelayTicks());
    }

    private boolean handleDailyReward(Player player, CharacterData data) {
        double reward = plugin.getSettings().getDailyLoginReward();
        if (reward <= 0) return false;
        long now = System.currentTimeMillis();
        long last = data.getLastDailyReward();
        long oneDayMs = 24L * 60L * 60L * 1000L;
        if (now - last < oneDayMs) return false;
        data.setLastDailyReward(now);
        plugin.getEconomy().depositPlayer(player, reward);
        player.sendMessage("§a§lDaily Reward! §e" + CurrencyUtil.symbol() + String.format("%.0f", reward) + " §ahas been added to your balance.");
        return true;
    }

}