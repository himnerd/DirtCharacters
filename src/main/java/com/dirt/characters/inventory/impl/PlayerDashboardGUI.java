package com.dirt.characters.inventory.impl;

import com.cryptomorin.xseries.XMaterial;
import com.dirt.api.DirtApi;
import com.dirt.api.MenuExtension;
import com.dirt.characters.DirtCharacters;
import com.dirt.characters.data.CharacterData;
import com.dirt.characters.inventory.InventoryButton;
import com.dirt.characters.inventory.InventoryGUI;
import com.dirt.characters.util.CurrencyUtil;
import com.dirt.characters.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlayerDashboardGUI extends InventoryGUI {
    private final DirtCharacters plugin;

    public PlayerDashboardGUI(DirtCharacters plugin) {
        this.plugin = plugin;
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 45, "§6Your Dashboard");
    }

    @Override
    public void decorate(Player player) {
        CharacterData data = plugin.getCharacterManager().getCharacter(player.getUniqueId());
        if (data == null) return;

        fillGlass(45, material("ORANGE_STAINED_GLASS_PANE"));
        double balance = plugin.getEconomy().getBalance(player);
        addButton(4, new InventoryButton()
                .creator(p -> ItemUtil.buildItem(material("NAME_TAG"), "§6§l" + data.getFirstName() + " " + data.getLastName(),
                        "§7Balance: §a" + CurrencyUtil.symbol() + String.format("%,.0f", balance)))
                .consumer(event -> {}));

        addButton(13, new InventoryButton()
                .creator(p -> ItemUtil.buildItem(material("CLOCK"), "§e§lDaily Reward",
                        data.getLastDailyReward() > 0 ? "§aClaimed at least once" : "§6Available on your next eligible login",
                        "§7Daily rewards are delivered automatically.",
                        "§7Return after 24 hours to claim again."))
                .consumer(event -> {}));

        boolean homeSet = data.getHomeLocation() != null && !data.getHomeLocation().isBlank();
        addButton(15, new InventoryButton()
                .creator(p -> ItemUtil.buildItem(material("COMPASS"), "§e§lHome Waypoint",
                        homeSet ? "§aHome waypoint configured" : "§cNo home waypoint set",
                        homeSet ? "§7Use §e/home §7to travel home." : "§7Stand in eligible nation territory.",
                        homeSet ? "§7" : "§aClick to try §e/sethome"))
                .consumer(event -> {
                    Player clicker = (Player) event.getWhoClicked();
                    if (homeSet) {
                        clicker.sendMessage("§eUse §6/home §eto travel to your saved waypoint.");
                        return;
                    }
                    clicker.closeInventory();
                    clicker.performCommand("sethome");
                }));

        renderExtensions(player);

        addButton(40, new InventoryButton()
                .creator(p -> ItemUtil.buildItem(material("PAPER"), "§f§lQuick Help",
                        "§e/dc §7Character and balance",
                        "§e/did §7Character ID",
                        "§e/menu §7This dashboard",
                        DirtApi.nations() != null ? "§e/dn §7Nations  §8•  §e/dr §7Regions" : "§8Nations not installed",
                        DirtApi.biz() != null ? "§e/db §7Businesses" : "§8Businesses not installed",
                        DirtApi.shops() != null ? "§e/ds §7Shops" : "§8Shops not installed"))
                .consumer(event -> {}));

        super.decorate(player);
    }

    private XMaterial material(String name) {
        return XMaterial.matchXMaterial(name).orElseThrow();
    }

    @Override
    public String getBedrockTitle() {
        return "Your Dashboard";
    }

    /** Renders dashboard buttons contributed by the rest of the suite. */
    private void renderExtensions(Player viewer) {
        Set<Integer> used = new HashSet<>(java.util.List.of(4, 13, 15, 40));
        List<MenuExtension> extensions = DirtApi.getMenuExtensions(MenuExtension.Screen.DASHBOARD);
        for (MenuExtension extension : extensions) {
            if (!extension.isVisible(viewer.getUniqueId(), false)) continue;

            int slot = -1;
            for (int offset = 0; offset < 45; offset++) {
                int candidate = (extension.getPreferredSlot() + offset) % 45;
                if (candidate % 9 == 0 || candidate % 9 == 8) continue;
                if (used.add(candidate)) {
                    slot = candidate;
                    break;
                }
            }
            if (slot < 0) continue;

            addButton(slot, new InventoryButton()
                    .creator(p -> ItemUtil.buildItem(
                            XMaterial.matchXMaterial(extension.getIconMaterial()).orElse(XMaterial.PAPER),
                            extension.getTitle(viewer.getUniqueId(), false),
                            extension.getLore(viewer.getUniqueId(), false).toArray(new String[0])))
                    .consumer(event -> extension.onClick((Player) event.getWhoClicked(), viewer.getUniqueId(), false))
            );
        }
    }
}
