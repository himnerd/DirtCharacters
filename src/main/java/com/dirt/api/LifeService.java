package com.dirt.api;

import org.bukkit.entity.Player;

import java.util.UUID;

/** The DirtLife integration surface. Obtain it via {@link DirtApi#life()}. */
public interface LifeService {

    boolean isLifeSystemEnabled();

    boolean isLifeTokensEnabled();

    int getTokens(UUID playerUuid);

    void addTokens(UUID playerUuid, int amount);

    /** Opens the life-management screen (status, create a death). */
    void openLifeMenu(Player viewer);

    /** True while a death is being processed, so other screens stay out of the way. */
    boolean isProcessingDeath(UUID playerUuid);
}
