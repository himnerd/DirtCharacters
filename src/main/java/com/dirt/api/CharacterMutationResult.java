package com.dirt.api;

/** Result of a character-management request. */
public final class CharacterMutationResult {

    public enum Status {
        SUCCESS,
        INVALID_ARGUMENT,
        INVALID_PROFILE,
        INVALID_BIO,
        ALREADY_EXISTS,
        NOT_FOUND
    }

    private final Status status;
    private final CharacterView character;

    private CharacterMutationResult(Status status, CharacterView character) {
        this.status = status;
        this.character = character;
    }

    public static CharacterMutationResult success(CharacterView character) {
        return new CharacterMutationResult(Status.SUCCESS, character);
    }

    public static CharacterMutationResult failure(Status status) {
        return new CharacterMutationResult(status, null);
    }

    public Status getStatus() {
        return status;
    }

    public CharacterView getCharacter() {
        return character;
    }

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }
}