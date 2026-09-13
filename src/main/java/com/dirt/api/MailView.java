package com.dirt.api;

import java.util.Map;
import java.util.UUID;

/** Read-only view of a single mailbox entry. */
public interface MailView {

    String getId();

    /** The mail type, e.g. {@code "NATION_INVITE"}. Never null. */
    String getType();

    /** UUID of the sending player, or null for system mail. */
    UUID getFromPlayerUuid();

    /** UUID of the mailbox owner. */
    UUID getOwnerUuid();

    long getTimestamp();

    /** Arbitrary string payload attached by the sender. Never null. */
    Map<String, String> getData();

    /** Convenience for {@code getData().getOrDefault(key, fallback)}. */
    String get(String key, String fallback);
}
