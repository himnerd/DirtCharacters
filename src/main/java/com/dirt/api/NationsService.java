package com.dirt.api;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

/** The DirtNations integration surface. Obtain it via {@link DirtApi#nations()}. */
public interface NationsService {

    /** Name of the nation the player belongs to, or null. */
    String getNationNameOf(UUID playerUuid);

    UUID getNationIdOf(UUID playerUuid);

    /** True when both players are citizens of the same nation. */
    boolean isSameNation(UUID a, UUID b);

    /** Nation name for a chunk, or null when the chunk is unclaimed. */
    String getNationNameAt(String chunkKey);

    /** Region name for a chunk, or null when the chunk is unclaimed. */
    String getRegionNameAt(String chunkKey);

    /** Builds the {@code "world|chunkX|chunkZ"} key DirtNations indexes territory by. */
    String chunkKey(String world, int chunkX, int chunkZ);

    /** Convenience for {@link #chunkKey(String, int, int)} from a location. */
    String chunkKeyOf(Location location);

    /**
     * Whether the player may set their home here.
     *
     * @return null when allowed, otherwise a player-facing reason to deny
     */
    String getSetHomeDenial(Player player, Location location);

    /** Nations the player may act on behalf of, for contract signing. */
    List<NationRef> getNationsActingFor(UUID playerUuid);

    /** Starts the chunk-selection flow that claims a business property. */
    void startBusinessPropertySelection(Player player, UUID businessId);

    /** Starts the chunk-selection flow that adds chunks to an existing business property. */
    void startBusinessPropertyExtension(Player player, UUID businessId, UUID propertyId);

    /** Opens the room-management screen for a business property. */
    void openBusinessPropertyRooms(Player player, UUID businessId, UUID propertyId);

    /** Whether nation-style custom property permissions are enabled. */
    boolean isPropertyPermissionsEnabled();

    /** Whether per-property mob spawn control is enabled. */
    boolean isMobPermissionsEnabled();

    /** Every nation on the server, for pickers and payment screens. */
    List<NationRef> getAllNations();

    void depositToTreasury(UUID nationId, double amount);

    double getTreasuryBalance(UUID nationId);

    /** Lightweight nation identity used across plugin boundaries. */
    interface NationRef {
        UUID getNationId();

        String getName();

        String getColor();
    }
}
