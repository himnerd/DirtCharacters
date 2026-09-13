package com.dirt.characters.api;

import com.dirt.api.MailHandler;
import com.dirt.api.MailService;
import com.dirt.api.MailView;
import com.dirt.characters.DirtCharacters;
import com.dirt.characters.data.CharacterData;
import com.dirt.characters.data.MailItem;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class MailServiceImpl implements MailService {

    private final DirtCharacters plugin;
    private final Map<String, MailHandler> handlers = new ConcurrentHashMap<>();

    public MailServiceImpl(DirtCharacters plugin) {
        this.plugin = plugin;
    }

    @Override
    public String send(UUID recipient, String type, UUID from, Map<String, String> data) {
        if (recipient == null || type == null) return null;
        if (!plugin.getCharacterManager().hasCharacter(recipient)) return null;

        MailItem item = new MailItem();
        item.setId(UUID.randomUUID().toString());
        item.setType(type);
        item.setFromPlayerUuid(from);
        item.setTimestamp(System.currentTimeMillis());
        item.setData(data != null ? new LinkedHashMap<>(data) : new HashMap<>());

        plugin.getCharacterManager().addMailItem(recipient, item);
        return item.getId();
    }

    @Override
    public void remove(UUID recipient, String mailId) {
        if (recipient == null || mailId == null) return;
        plugin.getCharacterManager().removeMailItem(recipient, mailId);
    }

    @Override
    public MailView get(UUID recipient, String mailId) {
        CharacterData data = plugin.getCharacterManager().getCharacter(recipient);
        if (data == null) return null;
        for (MailItem item : data.getMailbox()) {
            if (item.getId().equals(mailId)) return new MailViewImpl(item, recipient);
        }
        return null;
    }

    @Override
    public int count(UUID recipient) {
        CharacterData data = plugin.getCharacterManager().getCharacter(recipient);
        return data != null ? data.getMailbox().size() : 0;
    }

    @Override
    public void registerHandler(String type, MailHandler handler) {
        if (type == null || handler == null) return;
        handlers.put(type, handler);
    }

    @Override
    public void unregisterHandler(String type) {
        handlers.remove(type);
    }

    /** The handler for a mail type, or null when nothing owns it. */
    public MailHandler getHandler(String type) {
        return type != null ? handlers.get(type) : null;
    }

    /** Drops every handler registered by a plugin that is shutting down. */
    public void unregisterAll(ClassLoader owner) {
        handlers.values().removeIf(h -> h.getClass().getClassLoader() == owner);
    }
}
