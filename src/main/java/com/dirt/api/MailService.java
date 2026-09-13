package com.dirt.api;

import java.util.Map;
import java.util.UUID;

/** Mailbox operations exposed to satellite plugins. */
public interface MailService {

    /**
     * Delivers a mail item to a character's mailbox.
     *
     * @param recipient mailbox owner
     * @param type      mail type; register a {@link MailHandler} for it to make it openable
     * @param from      sending player, or null for system mail
     * @param data      payload copied into the mail item; may be null
     * @return the generated mail id, or null if the recipient has no character
     */
    String send(UUID recipient, String type, UUID from, Map<String, String> data);

    /** Removes a mail item. No-op when the id is unknown. */
    void remove(UUID recipient, String mailId);

    /** Returns the mail item, or null when it no longer exists. */
    MailView get(UUID recipient, String mailId);

    /** Number of items in a character's mailbox. */
    int count(UUID recipient);

    /**
     * Registers the renderer/handler for one mail type. Call this in
     * {@code onEnable}; re-registering a type replaces the previous handler.
     */
    void registerHandler(String type, MailHandler handler);

    /** Removes a previously registered handler. */
    void unregisterHandler(String type);
}
