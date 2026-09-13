package com.dirt.characters.data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MailItem {
    private String id;
    private String type; // MARRIAGE_REQUEST, CHILD_BEARING_REQUEST, CHILD_JOIN_REQUEST
    private UUID fromPlayerUuid;
    private long timestamp;
    private Map<String, String> data = new HashMap<>();

    public MailItem() {
    }

    public MailItem(String id, UUID fromPlayerUuid, long timestamp, Map<String, String> data) {
        this.id = id;
        this.fromPlayerUuid = fromPlayerUuid;
        this.timestamp = timestamp;
        this.data = data;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UUID getFromPlayerUuid() {
        return this.fromPlayerUuid;
    }

    public void setFromPlayerUuid(UUID fromPlayerUuid) {
        this.fromPlayerUuid = fromPlayerUuid;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, String> getData() {
        return this.data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
