package com.dirt.characters.inventory.impl.travel;

import com.cryptomorin.xseries.XMaterial;
import com.dirt.characters.DirtCharacters;
import com.dirt.characters.inventory.InventoryButton;
import com.dirt.characters.inventory.InventoryGUI;
import com.dirt.characters.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class FastTravelTransitionGUI extends InventoryGUI {
    private final DirtCharacters plugin;
    private final String destination;

    public FastTravelTransitionGUI(DirtCharacters plugin, String destination) {
        this.plugin = plugin;
        this.destination = destination;
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 27, "§5§lFast Travel");
    }

    @Override
    public void decorate(Player player) {
        fillGlass(27, material("PURPLE_STAINED_GLASS_PANE"));
        addButton(13, new InventoryButton()
                .creator(viewer -> ItemUtil.buildItem(material("ENDER_PEARL"), "§d§lTeleporting...",
                        "§7Destination: §f" + destination,
                        "§7Stabilizing the travel path."))
                .consumer(event -> {}));
        super.decorate(player);
    }

    private XMaterial material(String name) {
        return XMaterial.matchXMaterial(name).orElseThrow(() -> new IllegalStateException("Missing material: " + name));
    }
}