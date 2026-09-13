package com.dirt.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

/** Fired by DirtCharacters once a new character has been created and saved. */
public class CharacterCreatedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final UUID playerUuid;

    public CharacterCreatedEvent(Player player) {
        this.player = player;
        this.playerUuid = player.getUniqueId();
    }

    /** Creates the event for a character created while its player is offline. */
    public CharacterCreatedEvent(UUID playerUuid) {
        this.player = null;
        this.playerUuid = playerUuid;
    }

    public Player getPlayer() {
        return player;
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
