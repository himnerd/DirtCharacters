package com.dirt.api;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

/** The DirtShops integration surface. Obtain it via {@link DirtApi#shops()}. */
public interface ShopsService {

    /** Number of shops owned by a business. */
    int countShopsOfBusiness(UUID businessId);

    /** Number of stockrooms owned by a business. */
    int countStockroomsOfBusiness(UUID businessId);

    boolean isOnlineShopEnabled(UUID businessId);

    List<UUID> getOnlineShopBusinessIds();

    /** Opens the shops-and-stock screen for a business. */
    void openBusinessShops(Player viewer, UUID businessId);

    /** Removes every shop and stockroom belonging to a business that was deleted. */
    void purgeBusiness(UUID businessId);
}
