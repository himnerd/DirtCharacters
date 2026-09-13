package com.dirt.api;

/**
 * Supplies chunk claims owned by a plugin other than DirtNations.
 *
 * <p>Register with {@link DirtApi#registerTerritoryProvider}. DirtNations calls
 * {@link #claimAt} on every block break/place, container open, interact and
 * region-enter check.
 */
public interface TerritoryProvider {

    /**
     * @param chunkKey {@code "world|chunkX|chunkZ"}
     * @return the claim covering that chunk, or null when this provider owns nothing there
     */
    TerritoryClaim claimAt(String chunkKey);
}
