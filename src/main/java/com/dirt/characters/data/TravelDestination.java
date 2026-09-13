package com.dirt.characters.data;

import org.bukkit.Location;

import java.util.UUID;

public class TravelDestination {
    private final String name;
    private final String description;
    private final Location location;
    private final UUID fastTravelPointId;

    public TravelDestination(String name, String description, Location location, UUID fastTravelPointId) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.fastTravelPointId = fastTravelPointId;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public Location getLocation() {
        return this.location;
    }

    public UUID getFastTravelPointId() {
        return this.fastTravelPointId;
    }
}
