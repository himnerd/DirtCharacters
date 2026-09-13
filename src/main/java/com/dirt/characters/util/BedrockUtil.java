package com.dirt.characters.util;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.profiles.builder.XSkull;
import com.cryptomorin.xseries.profiles.objects.Profileable;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public final class BedrockUtil {

    // Floodgate is reached reflectively so this class carries no compile-time
    // dependency on it, and so a missing Floodgate is a cached false rather than
    // a NoClassDefFoundError that no try/catch(Exception) would have caught.
    private static Boolean floodgatePresent;
    private static java.lang.reflect.Method isFloodgatePlayer;
    private static Object floodgateApi;

    private BedrockUtil() {}

    public static boolean isFloodgateAvailable() {
        if (floodgatePresent == null) {
            try {
                Class<?> api = Class.forName("org.geysermc.floodgate.api.FloodgateApi");
                floodgateApi = api.getMethod("getInstance").invoke(null);
                isFloodgatePlayer = api.getMethod("isFloodgatePlayer", UUID.class);
                floodgatePresent = floodgateApi != null;
            } catch (Throwable e) {
                floodgatePresent = false;
            }
        }
        return floodgatePresent;
    }

    public static boolean isBedrock(Player player) {
        return isBedrock(player.getUniqueId());
    }

    public static boolean isBedrock(UUID uuid) {
        if (!isFloodgateAvailable()) return false;
        try {
            return Boolean.TRUE.equals(isFloodgatePlayer.invoke(floodgateApi, uuid));
        } catch (Throwable e) {
            return false;
        }
    }

    public static ItemStack createPlayerHead(Player player) {
        return createPlayerHead(player.getUniqueId());
    }

    public static ItemStack createPlayerHead(OfflinePlayer player) {
        return createPlayerHead(player.getUniqueId());
    }

    public static ItemStack createPlayerHead(UUID uuid) {
        try {
            if (isBedrock(uuid)) {
                return XMaterial.matchXMaterial("PLAYER_HEAD").map(XMaterial::parseItem)
                        .orElse(new ItemStack(org.bukkit.Material.BARRIER));
            }
            OfflinePlayer offline = Bukkit.getOfflinePlayer(uuid);
            String name = offline.getName();
            if (name == null || !isValidMinecraftUsername(name)) {
                return XMaterial.matchXMaterial("PLAYER_HEAD").map(XMaterial::parseItem)
                        .orElse(new ItemStack(org.bukkit.Material.BARRIER));
            }
            return XSkull.createItem().profile(Profileable.of(offline)).apply();
        } catch (Exception e) {
            return XMaterial.matchXMaterial("PLAYER_HEAD").map(XMaterial::parseItem)
                    .orElse(new ItemStack(org.bukkit.Material.BARRIER));
        }
    }

    private static boolean isValidMinecraftUsername(String name) {
        if (name.length() < 3 || name.length() > 16) return false;
        for (char c : name.toCharArray()) {
            if (!Character.isLetterOrDigit(c) && c != '_') return false;
        }
        return true;
    }
}