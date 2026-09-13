package com.dirt.api;

import org.bukkit.Location;

import java.util.List;
import java.util.UUID;

/**
 * Contributes a category of fast-travel destinations owned by another plugin —
 * DirtNations adds owned properties and governed regions, DirtBiz adds
 * workplaces.
 *
 * <p>Register with {@link DirtApi#registerTravelDestinationProvider}. Categories
 * with no destinations for a player are hidden, so the fast-travel screen only
 * shows what that player can actually use.
 */
public interface TravelDestinationProvider {

    /** Coloured category label, e.g. {@code "§aPersonal Properties"}. */
    String getCategoryName();

    /** XMaterial name for the category icon. */
    String getIconMaterial();

    /** Lore shown under the category button. */
    default List<String> getCategoryLore() {
        return List.of();
    }

    List<Destination> getDestinations(UUID playerUuid);

    /** One travel target. */
    interface Destination {
        String getName();

        String getDescription();

        Location getLocation();
    }

    /** Plain immutable {@link Destination}. */
    final class SimpleDestination implements Destination {
        private final String name;
        private final String description;
        private final Location location;

        public SimpleDestination(String name, String description, Location location) {
            this.name = name;
            this.description = description;
            this.location = location;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public Location getLocation() {
            return location;
        }
    }
}
