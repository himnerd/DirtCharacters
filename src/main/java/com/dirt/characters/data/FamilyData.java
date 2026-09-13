package com.dirt.characters.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FamilyData {
    private UUID familyId;
    private UUID spouse1;
    private UUID spouse2;
    private List<UUID> children = new ArrayList<>();

    public FamilyData() {
    }

    public UUID getFamilyId() {
        return this.familyId;
    }

    public void setFamilyId(UUID familyId) {
        this.familyId = familyId;
    }

    public UUID getSpouse1() {
        return this.spouse1;
    }

    public void setSpouse1(UUID spouse1) {
        this.spouse1 = spouse1;
    }

    public UUID getSpouse2() {
        return this.spouse2;
    }

    public void setSpouse2(UUID spouse2) {
        this.spouse2 = spouse2;
    }

    public List<UUID> getChildren() {
        return this.children;
    }

    public void setChildren(List<UUID> children) {
        this.children = children;
    }
}
