package com.dirt.characters.api;

import com.dirt.api.MailView;
import com.dirt.characters.data.MailItem;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

/** Wraps a stored {@link MailItem} as the read-only API view. */
public final class MailViewImpl implements MailView {

    private final MailItem item;
    private final UUID owner;

    public MailViewImpl(MailItem item, UUID owner) {
        this.item = item;
        this.owner = owner;
    }

    /** The underlying record, for DirtCharacters' own screens. */
    public MailItem getItem() {
        return item;
    }

    @Override
    public String getId() {
        return item.getId();
    }

    @Override
    public String getType() {
        return item.getType() != null ? item.getType() : "TEXT";
    }

    @Override
    public UUID getFromPlayerUuid() {
        return item.getFromPlayerUuid();
    }

    @Override
    public UUID getOwnerUuid() {
        return owner;
    }

    @Override
    public long getTimestamp() {
        return item.getTimestamp();
    }

    @Override
    public Map<String, String> getData() {
        Map<String, String> data = item.getData();
        return data != null ? Collections.unmodifiableMap(data) : Collections.emptyMap();
    }

    @Override
    public String get(String key, String fallback) {
        String value = getData().get(key);
        return value != null ? value : fallback;
    }
}
