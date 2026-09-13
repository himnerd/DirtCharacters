package com.dirt.api;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

/**
 * A button contributed by a satellite plugin to one of DirtCharacters' hub
 * screens ({@code /dc} character management and {@code /menu} dashboard).
 *
 * <p>This is how DirtNations adds "Properties", DirtBiz adds "Job" and
 * "Businesses", and DirtLife adds "Life" without DirtCharacters knowing those
 * plugins exist. Register through {@link DirtApi#registerMenuExtension}.
 */
public interface MenuExtension {

    /** Which hub screen this button belongs on. */
    enum Screen {
        /** {@code /dc} character management. */
        CHARACTER_MENU,
        /** {@code /menu} player dashboard. */
        DASHBOARD,
        /** The operator's per-character admin screen ({@code /dc admin}). */
        ADMIN_CHARACTER
    }

    Screen getScreen();

    /**
     * Preferred inventory slot. If it is already taken the button is placed in
     * the next free slot, so extensions never silently overwrite each other.
     */
    int getPreferredSlot();

    /** XMaterial name for the icon, e.g. {@code "OAK_DOOR"}. */
    String getIconMaterial();

    /** Coloured button title. */
    String getTitle(UUID target, boolean adminView);

    List<String> getLore(UUID target, boolean adminView);

    /** Whether the button should be shown at all for this target. */
    default boolean isVisible(UUID target, boolean adminView) {
        return true;
    }

    /** Whether the button does anything when clicked (admin views are often read-only). */
    default boolean isClickable(UUID target, boolean adminView) {
        return true;
    }

    void onClick(Player viewer, UUID target, boolean adminView);
}
