package com.dirt.api;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/** The DirtCharacters integration surface. Obtain it via {@link DirtApi#characters()}. */
public interface CharactersService {

    boolean hasCharacter(UUID playerUuid);

    /** The player's living character, or null. */
    CharacterView getCharacter(UUID playerUuid);

    /** Every living character on the server. */
    List<CharacterView> getAllCharacters();

    /**
     * Creates a new living character for a player UUID. The profile is validated
     * and normalized before it is persisted.
     */
    CharacterMutationResult createCharacter(UUID playerUuid, CharacterProfile profile);

    /** Updates a living character's identity fields after validating the profile. */
    CharacterMutationResult updateCharacter(UUID playerUuid, CharacterProfile profile);

    /** Permanently removes a living character without running the death pipeline. */
    CharacterMutationResult deleteCharacter(UUID playerUuid);

    /** Updates a character's bio when character bios are enabled. */
    CharacterMutationResult updateCharacterBio(UUID playerUuid, String bio);

    /**
     * Best available name for a player: their character's first + last name, the
     * player's Minecraft name if they have no character, or the UUID as a last
     * resort. Never null — safe to use directly in messages and item lore.
     */
    String getDisplayName(UUID playerUuid);

    /** Like {@link #getDisplayName} but including the middle name. */
    String getFullName(UUID playerUuid);

    MailService mail();

    /** Opens {@code owner}'s mailbox for {@code viewer}. */
    void openMailbox(Player viewer, UUID owner);

    /** Opens the character-management screen for {@code target}. */
    void openCharacterMenu(Player viewer, UUID target);

    /** Opens the contract list for a business, if contracts are enabled. */
    void openBusinessContracts(Player viewer, UUID businessId);

    /** Whether the contract system is enabled. */
    boolean isContractsEnabled();

    /**
     * Ends a character permanently: balance and property inheritance, family
     * dissolution, the inheritance chest, then a fresh character-creation
     * screen. DirtLife calls this from the death screen.
     *
     * @param chestLocation where to place the inheritance chest, or null to skip it
     */
    void executeDeath(Player player, Location chestLocation);

    /**
     * Asks the player to right-click a block, then hands the location to the
     * callback. Used to place the inheritance chest.
     */
    void requestLocation(Player player, String prompt, Consumer<Location> callback);

    /** Whether the player is mid-way through a location selection. */
    boolean hasPendingChatInput(UUID playerUuid);
}
