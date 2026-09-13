package com.dirt.characters.data;

import java.util.UUID;

public class ContractSignature {
    private UUID playerUuid;
    private String characterName;
    private String characterCode;
    private String onBehalfOf;
    private UUID onBehalfOfId;
    private String onBehalfOfType;
    private long signedAt;

    public ContractSignature() {
    }

    public UUID getPlayerUuid() {
        return this.playerUuid;
    }

    public void setPlayerUuid(UUID playerUuid) {
        this.playerUuid = playerUuid;
    }

    public String getCharacterName() {
        return this.characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    public String getCharacterCode() {
        return this.characterCode;
    }

    public void setCharacterCode(String characterCode) {
        this.characterCode = characterCode;
    }

    public String getOnBehalfOf() {
        return this.onBehalfOf;
    }

    public void setOnBehalfOf(String onBehalfOf) {
        this.onBehalfOf = onBehalfOf;
    }

    public UUID getOnBehalfOfId() {
        return this.onBehalfOfId;
    }

    public void setOnBehalfOfId(UUID onBehalfOfId) {
        this.onBehalfOfId = onBehalfOfId;
    }

    public String getOnBehalfOfType() {
        return this.onBehalfOfType;
    }

    public void setOnBehalfOfType(String onBehalfOfType) {
        this.onBehalfOfType = onBehalfOfType;
    }

    public long getSignedAt() {
        return this.signedAt;
    }

    public void setSignedAt(long signedAt) {
        this.signedAt = signedAt;
    }
}
