package com.dirt.api;

import java.util.UUID;

/**
 * A claim over a single chunk contributed by a plugin other than DirtNations —
 * today that means business properties from DirtBiz.
 *
 * <p>DirtNations consults every registered {@link TerritoryProvider} when
 * deciding whether a player may build, open containers or interact, so foreign
 * claims are protected by the same listener that protects nation regions.
 */
public interface TerritoryClaim {

    /** Stable id of the claim (the business property id, for DirtBiz). */
    UUID getClaimId();

    /** Name shown to players entering the claim, e.g. the property name. */
    String getDisplayName();

    /** Name of the owning organisation, e.g. the business name. */
    String getOwnerName();

    boolean canBuild(UUID playerUuid);

    boolean canUseContainers(UUID playerUuid);

    boolean canInteract(UUID playerUuid);

    boolean canEnter(UUID playerUuid);

    /** Whether the given mob type is blocked from spawning in this claim. */
    default boolean deniesMob(String mobType) {
        return false;
    }

    /** Called when a player walks into the claim, so the owner can be notified. */
    default void onPlayerEnter(UUID playerUuid) {
    }
}
