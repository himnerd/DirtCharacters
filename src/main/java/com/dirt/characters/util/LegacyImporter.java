package com.dirt.characters.util;

import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.stream.Stream;

/**
 * One-time import of this plugin's data out of the old combined
 * {@code plugins/DirtEconomy} folder.
 *
 * <p>Runs on first enable only: it copies rather than moves, so the original
 * DirtEconomy folder stays intact as a backup, and it skips any destination that
 * already holds data so a re-run can never clobber live records. A marker file
 * records that the import happened.
 */
public final class LegacyImporter {

    private static final String MARKER = ".legacy-import-complete";

    /** old DirtEconomy-relative path -> new plugin-relative path */
    private static final String[][] PATHS = {
            {"data/MTC/characters", "data/characters"},
            {"data/MTC/families", "data/families"},
            {"data/MTC/ids", "data/ids"},
            {"data/MTN/contracts", "data/contracts"},
            {"data/FastTravel", "data/fasttravel"},
            {"data/protected_chests.yml", "data/protected_chests.yml"}
    };

    private LegacyImporter() {
    }

    public static void run(Plugin plugin) {
        File dataFolder = plugin.getDataFolder();
        File marker = new File(dataFolder, MARKER);
        if (marker.exists()) return;

        File legacyRoot = new File(dataFolder.getParentFile(), "DirtEconomy");
        if (!legacyRoot.isDirectory()) {
            writeMarker(plugin, marker);
            return;
        }

        int imported = 0;
        for (String[] entry : PATHS) {
            File source = new File(legacyRoot, entry[0]);
            if (!source.exists()) continue;

            File destination = new File(dataFolder, entry[1]);
            if (source.isDirectory()) {
                String[] existing = destination.list();
                if (existing != null && existing.length > 0) continue;
                imported += copyTree(plugin, source.toPath(), destination.toPath());
            } else {
                if (destination.exists()) continue;
                destination.getParentFile().mkdirs();
                if (copyFile(plugin, source.toPath(), destination.toPath())) imported++;
            }
        }

        if (imported > 0) {
            plugin.getLogger().info("[LegacyImporter] Imported " + imported
                    + " file(s) from plugins/DirtEconomy. The original folder was left untouched as a backup.");
        }
        writeMarker(plugin, marker);
    }

    private static int copyTree(Plugin plugin, Path source, Path destination) {
        int copied = 0;
        try (Stream<Path> walk = Files.walk(source)) {
            for (Path path : walk.sorted(Comparator.naturalOrder()).toList()) {
                Path target = destination.resolve(source.relativize(path).toString());
                if (Files.isDirectory(path)) {
                    Files.createDirectories(target);
                } else {
                    Files.createDirectories(target.getParent());
                    if (copyFile(plugin, path, target)) copied++;
                }
            }
        } catch (IOException e) {
            plugin.getLogger().warning("[LegacyImporter] Failed to import " + source + ": " + e.getMessage());
        }
        return copied;
    }

    private static boolean copyFile(Plugin plugin, Path source, Path target) {
        try {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            plugin.getLogger().warning("[LegacyImporter] Failed to copy " + source + ": " + e.getMessage());
            return false;
        }
    }

    private static void writeMarker(Plugin plugin, File marker) {
        try {
            marker.getParentFile().mkdirs();
            Files.writeString(marker.toPath(),
                    "Legacy DirtEconomy import completed. Delete this file to run the import again.\n");
        } catch (IOException e) {
            plugin.getLogger().warning("[LegacyImporter] Could not write the import marker: " + e.getMessage());
        }
    }
}
