package com.dirt.characters.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ContractData {
    private UUID contractId;
    private String body = "";
    private String title = "";
    private UUID creatorPlayerUuid;
    private String creatorCharacterName;
    private long createdAt;
    private int durationMinecraftDays;
    private long expiresAt;
    private String status = "DRAFT";
    private List<ContractSignature> signatures = new ArrayList<>();
    private UUID pendingRecipientUuid;
    private UUID secondRecipientUuid;
    private List<UUID> linkedPlayerUuids = new ArrayList<>();
    private List<UUID> linkedBusinessIds = new ArrayList<>();
    private List<UUID> linkedNationIds = new ArrayList<>();

    public ContractData() {
    }

    public UUID getContractId() {
        return this.contractId;
    }

    public void setContractId(UUID contractId) {
        this.contractId = contractId;
    }

    public String getBody() {
        return this.body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public UUID getCreatorPlayerUuid() {
        return this.creatorPlayerUuid;
    }

    public void setCreatorPlayerUuid(UUID creatorPlayerUuid) {
        this.creatorPlayerUuid = creatorPlayerUuid;
    }

    public String getCreatorCharacterName() {
        return this.creatorCharacterName;
    }

    public void setCreatorCharacterName(String creatorCharacterName) {
        this.creatorCharacterName = creatorCharacterName;
    }

    public long getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public int getDurationMinecraftDays() {
        return this.durationMinecraftDays;
    }

    public void setDurationMinecraftDays(int durationMinecraftDays) {
        this.durationMinecraftDays = durationMinecraftDays;
    }

    public long getExpiresAt() {
        return this.expiresAt;
    }

    public void setExpiresAt(long expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ContractSignature> getSignatures() {
        return this.signatures;
    }

    public void setSignatures(List<ContractSignature> signatures) {
        this.signatures = signatures;
    }

    public UUID getPendingRecipientUuid() {
        return this.pendingRecipientUuid;
    }

    public void setPendingRecipientUuid(UUID pendingRecipientUuid) {
        this.pendingRecipientUuid = pendingRecipientUuid;
    }

    public UUID getSecondRecipientUuid() {
        return this.secondRecipientUuid;
    }

    public void setSecondRecipientUuid(UUID secondRecipientUuid) {
        this.secondRecipientUuid = secondRecipientUuid;
    }

    public List<UUID> getLinkedPlayerUuids() {
        return this.linkedPlayerUuids;
    }

    public void setLinkedPlayerUuids(List<UUID> linkedPlayerUuids) {
        this.linkedPlayerUuids = linkedPlayerUuids;
    }

    public List<UUID> getLinkedBusinessIds() {
        return this.linkedBusinessIds;
    }

    public void setLinkedBusinessIds(List<UUID> linkedBusinessIds) {
        this.linkedBusinessIds = linkedBusinessIds;
    }

    public List<UUID> getLinkedNationIds() {
        return this.linkedNationIds;
    }

    public void setLinkedNationIds(List<UUID> linkedNationIds) {
        this.linkedNationIds = linkedNationIds;
    }
}
