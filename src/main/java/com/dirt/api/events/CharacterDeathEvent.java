package com.dirt.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

/**
 * Fired by DirtCharacters when a character permanently dies, before its record
 * is deleted.
 *
 * <p>This is where satellite plugins hand the deceased's holdings on: DirtNations
 * transfers owned properties to the inheritor, DirtBiz reassigns businesses. The
 * character still exists while listeners run, so lookups against
 * {@code CharactersService} still resolve.
 */
public class CharacterDeathEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final UUID deceasedUuid;
    private final UUID inheritorUuid;

    public CharacterDeathEvent(Player player, UUID deceasedUuid, UUID inheritorUuid) {
        this.player = player;
        this.deceasedUuid = deceasedUuid;
        this.inheritorUuid = inheritorUuid;
    }

    public Player getPlayer() {
        return player;
    }

    public UUID getDeceasedUuid() {
        return deceasedUuid;
    }

    /** The living character who inherits, or null when there is nobody to inherit. */
    public UUID getInheritorUuid() {
        return inheritorUuid;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
