package com.dirt.api;

import java.util.UUID;

/**
 * Read-only view of a living character, handed to other plugins by
 * {@link CharactersService}. Implemented by DirtCharacters.
 */
public interface CharacterView {

    UUID getPlayerUuid();

    String getFirstName();

    String getMiddleName();

    String getLastName();

    /** First + last name, e.g. {@code "Ada Lovelace"}. Never null. */
    String getDisplayName();

    /** First + middle + last name. Never null. */
    String getFullName();

    /** {@code "MALE"} or {@code "FEMALE"}. */
    String getGender();

    long getBirthDate();

    long getFirstJoinDate();

    boolean isAlive();

    UUID getFamilyId();

    /** Character background supplied by its owner, or an empty string when unset. */
    String getBio();
}
