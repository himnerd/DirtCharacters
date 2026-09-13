package com.dirt.characters.data;

import java.util.UUID;

public class FastTravelPoint {
    private UUID pointId;
    private String world;
    private int x;
    private int y;
    private int z;
    private UUID creatorUuid;
    private long createdAt;
    private long visits;

    public FastTravelPoint() {
    }

    public UUID getPointId() {
        return this.pointId;
    }

    public void setPointId(UUID pointId) {
        this.pointId = pointId;
    }

    public String getWorld() {
        return this.world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return this.z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public UUID getCreatorUuid() {
        return this.creatorUuid;
    }

    public void setCreatorUuid(UUID creatorUuid) {
        this.creatorUuid = creatorUuid;
    }

    public long getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getVisits() {
        return this.visits;
    }

    public void setVisits(long visits) {
        this.visits = visits;
    }
}
