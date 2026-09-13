package com.dirt.characters.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CharacterData {
    private UUID playerUuid;
    private String firstName;
    private String middleName;
    private String lastName;
    private String gender; // "MALE" or "FEMALE"
    private long birthDate;
    private boolean alive = true;
    private UUID familyId;
    private UUID birthFamilyId;
    private String familyRole; // "PRIMARY", "SECONDARY", or "CHILD"
    private long firstJoinDate;
    private UUID inheritorUuid;
    private boolean pendingChildSelection = false;
    private List<UUID> previousFamilyIds = new ArrayList<>();
    private List<MailItem> mailbox = new ArrayList<>();
    private long lastDailyReward = 0;
    private String homeLocation;
    private String bio = "";

    public CharacterData() {
    }

    public UUID getPlayerUuid() {
        return this.playerUuid;
    }

    public void setPlayerUuid(UUID playerUuid) {
        this.playerUuid = playerUuid;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return this.middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public long getBirthDate() {
        return this.birthDate;
    }

    public void setBirthDate(long birthDate) {
        this.birthDate = birthDate;
    }

    public boolean isAlive() {
        return this.alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public UUID getFamilyId() {
        return this.familyId;
    }

    public void setFamilyId(UUID familyId) {
        this.familyId = familyId;
    }

    public UUID getBirthFamilyId() {
        return this.birthFamilyId;
    }

    public void setBirthFamilyId(UUID birthFamilyId) {
        this.birthFamilyId = birthFamilyId;
    }

    public long getFirstJoinDate() {
        return this.firstJoinDate;
    }

    public void setFirstJoinDate(long firstJoinDate) {
        this.firstJoinDate = firstJoinDate;
    }

    public UUID getInheritorUuid() {
        return this.inheritorUuid;
    }

    public void setInheritorUuid(UUID inheritorUuid) {
        this.inheritorUuid = inheritorUuid;
    }

    public boolean isPendingChildSelection() {
        return this.pendingChildSelection;
    }

    public void setPendingChildSelection(boolean pendingChildSelection) {
        this.pendingChildSelection = pendingChildSelection;
    }

    public List<UUID> getPreviousFamilyIds() {
        return this.previousFamilyIds;
    }

    public void setPreviousFamilyIds(List<UUID> previousFamilyIds) {
        this.previousFamilyIds = previousFamilyIds;
    }

    public List<MailItem> getMailbox() {
        return this.mailbox;
    }

    public void setMailbox(List<MailItem> mailbox) {
        this.mailbox = mailbox;
    }

    public long getLastDailyReward() {
        return this.lastDailyReward;
    }

    public void setLastDailyReward(long lastDailyReward) {
        this.lastDailyReward = lastDailyReward;
    }



    public String getHomeLocation() {
        return this.homeLocation;
    }

    public void setHomeLocation(String homeLocation) {
        this.homeLocation = homeLocation;
    }

    public String getBio() {
        return this.bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getGender() {
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFamilyRole() {
        return this.familyRole;
    }

    public void setFamilyRole(String familyRole) {
        this.familyRole = familyRole;
    }
}
