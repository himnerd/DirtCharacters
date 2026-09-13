package com.dirt.api;

import java.util.UUID;

/** Immutable character information supplied to ability providers. */
public final class CharacterAbilityContext {

    private final UUID characterUuid;
    private final CharacterView character;

    public CharacterAbilityContext(UUID characterUuid, CharacterView character) {
        this.characterUuid = characterUuid;
        this.character = character;
    }

    public UUID getCharacterUuid() {
        return characterUuid;
    }

    public CharacterView getCharacter() {
        return character;
    }
}