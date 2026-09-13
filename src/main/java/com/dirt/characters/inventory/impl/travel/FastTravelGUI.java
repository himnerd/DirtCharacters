package com.dirt.characters.inventory.impl.travel;

import com.cryptomorin.xseries.XMaterial;
import com.dirt.characters.DirtCharacters;
import com.dirt.characters.data.TravelDestination;
import com.dirt.characters.inventory.InventoryButton;
import com.dirt.characters.inventory.InventoryGUI;
import com.dirt.characters.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class FastTravelGUI extends InventoryGUI {
    private final DirtCharacters plugin;
    private final int page;

    public FastTravelGUI(DirtCharacters plugin, int page) {
        this.plugin = plugin;
        this.page = page;
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 54, "§5§lFast Travel");
    }

    @Override
    public void decorate(Player player) {
        fillPagedGui(54, material("PURPLE_STAINED_GLASS_PANE"));

        List<TravelDestination> frequent = plugin.getFastTravelManager().getFrequentDestinations();
        for (int index = 0; index < frequent.size(); index++) {
            TravelDestination destination = frequent.get(index);
            addDestinationButton(index + 2, destination, "§d§lFrequent: §f");
        }

        List<TravelDestination> destinations = plugin.getFastTravelManager().getPublicDestinations();
        int perPage = 36;
        int start = page * perPage;
        int end = Math.min(start + perPage, destinations.size());
        for (int index = start; index < end; index++) {
            addDestinationButton(index - start + 9, destinations.get(index), "§a");
        }

        if (destinations.isEmpty()) {
            addButton(31, new InventoryButton()
                    .creator(viewer -> ItemUtil.buildItem(material("CAMPFIRE"), "§7No public travel points",
                            "§8Right-click a lit campfire with an ender pearl",
                            "§8to create the first one."))
                    .consumer(event -> {}));
        }

        if (page > 0) {
            addButton(45, new InventoryButton()
                    .creator(viewer -> ItemUtil.buildItem(material("ARROW"), "§ePrevious Page"))
                    .consumer(event -> plugin.getGUIManager().openGUI(new FastTravelGUI(plugin, page - 1), (Player) event.getWhoClicked())));
        }
        if (end < destinations.size()) {
            addButton(53, new InventoryButton()
                    .creator(viewer -> ItemUtil.buildItem(material("ARROW"), "§eNext Page"))
                    .consumer(event -> plugin.getGUIManager().openGUI(new FastTravelGUI(plugin, page + 1), (Player) event.getWhoClicked())));
        }

        // Categories contributed by DirtNations / DirtBiz, laid out along the
        // bottom row. Nothing is shown when those plugins are absent.
        List<com.dirt.characters.managers.FastTravelManager.TravelCategory> categories =
                plugin.getFastTravelManager().getExternalCategories(player.getUniqueId());
        int[] categorySlots = {47, 49, 51};
        for (int i = 0; i < categories.size() && i < categorySlots.length; i++) {
            var category = categories.get(i);
            List<String> lore = new java.util.ArrayList<>(category.getLore());
            lore.add("§7" + category.getDestinations().size() + " destination(s)");
            addButton(categorySlots[i], new InventoryButton()
                    .creator(viewer -> ItemUtil.buildItem(
                            XMaterial.matchXMaterial(category.getIconMaterial()).orElse(XMaterial.ENDER_PEARL),
                            category.getName(), lore.toArray(new String[0])))
                    .consumer(event -> plugin.getGUIManager().openGUI(
                            new FastTravelDestinationGUI(plugin, category.getName(), category.getDestinations(), 0),
                            (Player) event.getWhoClicked())));
        }

        super.decorate(player);
    }

    private void addDestinationButton(int slot, TravelDestination destination, String prefix) {
        addButton(slot, new InventoryButton()
                .creator(viewer -> ItemUtil.buildItem(material("ENDER_PEARL"), prefix + plugin.getFastTravelManager().getDestinationName(viewer.getUniqueId(), destination),
                        "§7" + destination.getDescription(),
                        "§dClick to begin fast travel",
                        "§eRight-click to set your personal nickname"))
                .consumer(event -> {
                    Player player = (Player) event.getWhoClicked();
                    if (event.isRightClick() && destination.getFastTravelPointId() != null) {
                        plugin.getChatInputManager().requestInput(player, "§eType a nickname for this fast travel point, or type §ccancel§e.", nickname -> {
                            if (!plugin.getFastTravelManager().setPointNickname(player.getUniqueId(), destination.getFastTravelPointId(), nickname)) {
                                player.sendMessage("§cNicknames must be between 1 and 32 characters.");
                                return;
                            }
                            player.sendMessage("§aFast travel point nickname saved.");
                            plugin.getGUIManager().openGUI(new FastTravelGUI(plugin, page), player);
                        });
                        return;
                    }
                    plugin.getFastTravelManager().beginTravel(player, destination);
                }));
    }

    private XMaterial material(String name) {
        return XMaterial.matchXMaterial(name).orElseThrow(() -> new IllegalStateException("Missing material: " + name));
    }
}