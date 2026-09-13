package com.dirt.api;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/** The DirtBiz integration surface. Obtain it via {@link DirtApi#biz()}. */
public interface BizService {

    BusinessRef getBusiness(UUID businessId);

    BusinessRef getBusinessByName(String name);

    List<BusinessRef> getAllBusinesses();

    List<BusinessRef> getBusinessesByOwner(UUID playerUuid);

    List<BusinessRef> getBusinessesByEmployee(UUID playerUuid);

    /** Businesses the player may act on behalf of, for contract signing. */
    List<BusinessRef> getBusinessesActingFor(UUID playerUuid);

    PropertyRef getProperty(UUID propertyId);

    /** @param chunkKey {@code "world|chunkX|chunkZ"} */
    PropertyRef getPropertyByChunk(String chunkKey);

    List<PropertyRef> getAllProperties();

    /** Creates a business property from an approved chunk selection. */
    PropertyRef createProperty(UUID businessId, String name, List<String> chunkKeys);

    /** A business as seen from another plugin. */
    interface BusinessRef {
        UUID getBusinessId();

        String getName();

        UUID getOwnerUuid();

        List<UUID> getEmployeeUuids();

        /** True for the owner and every current employee. */
        boolean isStaff(UUID playerUuid);

        /** @param permission a {@code BusinessPermission} name, e.g. {@code "MANAGE_CONTRACTS"} */
        boolean hasPermission(UUID playerUuid, String permission);

        double getTreasuryBalance();

        void depositToTreasury(double amount);

        boolean withdrawFromTreasury(double amount);

        /** Role id → display name, for permission screens driven by DirtNations. */
        Map<UUID, String> getRoleNames();
    }

    /** A business-owned territory claim as seen from another plugin. */
    interface PropertyRef {
        UUID getPropertyId();

        UUID getBusinessId();

        String getName();

        void setName(String name);

        List<String> getChunks();

        void addChunks(List<String> chunkKeys);

        List<UUID> getRoomIds();

        void addRoom(UUID roomId);

        void removeRoom(UUID roomId);

        /** Persists any mutation made through this reference. */
        void save();
    }

    /** Opens the "my businesses" screen. */
    void openBusinessesMenu(Player viewer, UUID target);

    /** Opens the job-status screen. */
    void openJobMenu(Player viewer, UUID target);
}
