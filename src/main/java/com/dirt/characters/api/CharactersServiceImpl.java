package com.dirt.characters.api;

import com.dirt.api.CharacterView;
import com.dirt.api.CharacterMutationResult;
import com.dirt.api.CharacterProfile;
import com.dirt.api.CharactersService;
import com.dirt.api.MailService;
import com.dirt.api.events.CharacterCreatedEvent;
import com.dirt.characters.DirtCharacters;
import com.dirt.characters.data.CharacterData;
import com.dirt.characters.inventory.impl.CharacterManagementGUI;
import com.dirt.characters.inventory.impl.MailGUI;
import com.dirt.characters.inventory.impl.contract.ContractListGUI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public final class CharactersServiceImpl implements CharactersService {

    private final DirtCharacters plugin;
    private final MailServiceImpl mailService;

    public CharactersServiceImpl(DirtCharacters plugin) {
        this.plugin = plugin;
        this.mailService = new MailServiceImpl(plugin);
    }

    @Override
    public boolean hasCharacter(UUID playerUuid) {
        return playerUuid != null && plugin.getCharacterManager().hasCharacter(playerUuid);
    }

    @Override
    public CharacterView getCharacter(UUID playerUuid) {
        if (playerUuid == null) return null;
        CharacterData data = plugin.getCharacterManager().getCharacter(playerUuid);
        return data != null ? new CharacterViewImpl(data) : null;
    }

    @Override
    public List<CharacterView> getAllCharacters() {
        List<CharacterView> views = new ArrayList<>();
        for (CharacterData data : plugin.getCharacterManager().getAllCharacters()) {
            views.add(new CharacterViewImpl(data));
        }
        return views;
    }

    @Override
    public CharacterMutationResult createCharacter(UUID playerUuid, CharacterProfile profile) {
        CharacterMutationResult result = plugin.getCharacterManager().createCharacter(playerUuid, profile);
        if (result.isSuccess()) {
            Bukkit.getPluginManager().callEvent(new CharacterCreatedEvent(playerUuid));
        }
        return result;
    }

    @Override
    public CharacterMutationResult updateCharacter(UUID playerUuid, CharacterProfile profile) {
        return plugin.getCharacterManager().updateCharacter(playerUuid, profile);
    }

    @Override
    public CharacterMutationResult deleteCharacter(UUID playerUuid) {
        return plugin.getCharacterManager().removeCharacter(playerUuid);
    }

    @Override
    public CharacterMutationResult updateCharacterBio(UUID playerUuid, String bio) {
        if (!plugin.getSettings().isCharacterBiosEnabled()) {
            return CharacterMutationResult.failure(CharacterMutationResult.Status.INVALID_ARGUMENT);
        }
        return plugin.getCharacterManager().updateCharacterBio(playerUuid, bio);
    }

    @Override
    public String getDisplayName(UUID playerUuid) {
        if (playerUuid == null) return "Unknown";
        CharacterData data = plugin.getCharacterManager().getCharacter(playerUuid);
        if (data != null) {
            String name = new CharacterViewImpl(data).getDisplayName();
            if (!name.isBlank()) return name;
        }
        String playerName = Bukkit.getOfflinePlayer(playerUuid).getName();
        return playerName != null ? playerName : playerUuid.toString();
    }

    @Override
    public String getFullName(UUID playerUuid) {
        if (playerUuid == null) return "Unknown";
        CharacterData data = plugin.getCharacterManager().getCharacter(playerUuid);
        if (data != null) {
            String name = new CharacterViewImpl(data).getFullName();
            if (!name.isBlank()) return name;
        }
        return getDisplayName(playerUuid);
    }

    @Override
    public MailService mail() {
        return mailService;
    }

    /** DirtCharacters' own screens need the concrete type to look handlers up. */
    public MailServiceImpl mailImpl() {
        return mailService;
    }

    @Override
    public void openMailbox(Player viewer, UUID owner) {
        if (viewer == null) return;
        if (owner == null || owner.equals(viewer.getUniqueId())) {
            plugin.getGUIManager().openGUI(new MailGUI(plugin, 0), viewer);
        } else {
            plugin.getGUIManager().openGUI(new MailGUI(plugin, 0, owner), viewer);
        }
    }

    @Override
    public void openCharacterMenu(Player viewer, UUID target) {
        if (viewer == null) return;
        if (target == null || target.equals(viewer.getUniqueId())) {
            plugin.getGUIManager().openGUI(new CharacterManagementGUI(plugin), viewer);
        } else {
            plugin.getGUIManager().openGUI(new CharacterManagementGUI(plugin, target), viewer);
        }
    }

    @Override
    public void openBusinessContracts(Player viewer, UUID businessId) {
        if (viewer == null || businessId == null) return;
        if (!plugin.isContractsEnabled()) {
            viewer.sendMessage("§cContracts are disabled on this server.");
            return;
        }
        plugin.getGUIManager().openGUI(new ContractListGUI(plugin, businessId, null, 0), viewer);
    }

    @Override
    public boolean isContractsEnabled() {
        return plugin.isContractsEnabled();
    }

    @Override
    public void executeDeath(Player player, Location chestLocation) {
        plugin.executeDeath(player, chestLocation);
    }

    @Override
    public void requestLocation(Player player, String prompt, Consumer<Location> callback) {
        plugin.getLocationSelectionManager().requestLocation(player, prompt, callback);
    }

    @Override
    public boolean hasPendingChatInput(UUID playerUuid) {
        return plugin.getChatInputManager().hasPendingInput(playerUuid);
    }
}
