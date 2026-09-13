package com.dirt.api;

import org.bukkit.entity.Player;

import java.util.List;

/**
 * Renders and handles one mail type that a satellite plugin owns.
 *
 * <p>DirtCharacters stores every mail item and draws the mailbox list, but it
 * does not know what a {@code NATION_INVITE} or {@code JOB_OFFER} means. Plugins
 * register a handler per type through {@link MailService#registerHandler}; the
 * mailbox delegates both the list entry and the detail screen back to it.
 *
 * <p>When no handler is registered for a type the entry still appears in the
 * mailbox as a plain, un-openable item, so mail is never silently lost when a
 * plugin is removed.
 */
public interface MailHandler {

    /** Coloured label shown in the mailbox list. */
    String getListTitle(MailView mail);

    /** XMaterial name for the list icon, e.g. {@code "PAPER"}. */
    default String getIconMaterial(MailView mail) {
        return "PAPER";
    }

    /** Extra lore lines shown under the list entry. */
    default List<String> getListLore(MailView mail) {
        return List.of();
    }

    /**
     * Opens the detail screen for this mail item.
     *
     * <p>Implementations are responsible for removing the mail item once it has
     * been acted on, and for returning the player to the mailbox afterwards via
     * {@link CharactersService#openMailbox}.
     */
    void open(Player viewer, MailView mail);
}
