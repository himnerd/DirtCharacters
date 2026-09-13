package com.dirt.characters.api;

import com.dirt.api.CharacterView;
import com.dirt.characters.data.CharacterData;

import java.util.UUID;

/** Wraps a {@link CharacterData} record as the read-only API view. */
public final class CharacterViewImpl implements CharacterView {

    private final CharacterData data;

    public CharacterViewImpl(CharacterData data) {
        this.data = data;
    }

    @Override
    public UUID getPlayerUuid() {
        return data.getPlayerUuid();
    }

    @Override
    public String getFirstName() {
        return orEmpty(data.getFirstName());
    }

    @Override
    public String getMiddleName() {
        return orEmpty(data.getMiddleName());
    }

    @Override
    public String getLastName() {
        return orEmpty(data.getLastName());
    }

    @Override
    public String getDisplayName() {
        return join(getFirstName(), getLastName());
    }

    @Override
    public String getFullName() {
        return join(getFirstName(), getMiddleName(), getLastName());
    }

    @Override
    public String getGender() {
        return data.getGender() != null ? data.getGender() : "MALE";
    }

    @Override
    public long getBirthDate() {
        return data.getBirthDate();
    }

    @Override
    public long getFirstJoinDate() {
        return data.getFirstJoinDate();
    }

    @Override
    public boolean isAlive() {
        return data.isAlive();
    }

    @Override
    public UUID getFamilyId() {
        return data.getFamilyId();
    }

    @Override
    public String getBio() {
        return orEmpty(data.getBio());
    }

    private static String orEmpty(String s) {
        return s != null ? s : "";
    }

    private static String join(String... parts) {
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part == null || part.isBlank()) continue;
            if (sb.length() > 0) sb.append(' ');
            sb.append(part);
        }
        return sb.toString();
    }
}
