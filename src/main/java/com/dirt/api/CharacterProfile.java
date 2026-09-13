package com.dirt.api;

/** Immutable identity fields accepted when creating or updating a character. */
public final class CharacterProfile {

    private final String firstName;
    private final String middleName;
    private final String lastName;
    private final String gender;

    public CharacterProfile(String firstName, String middleName, String lastName, String gender) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.gender = gender;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGender() {
        return gender;
    }
}