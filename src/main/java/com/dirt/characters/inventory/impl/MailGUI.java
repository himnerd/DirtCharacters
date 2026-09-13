package com.dirt.characters.inventory.impl;

import com.cryptomorin.xseries.XMaterial;
import com.dirt.api.MailHandler;
import com.dirt.characters.DirtCharacters;
import com.dirt.characters.api.MailViewImpl;
import com.dirt.characters.data.CharacterData;
import com.dirt.characters.data.MailItem;
import com.dirt.characters.inventory.InventoryButton;
import com.dirt.characters.inventory.InventoryGUI;
import com.dirt.characters.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MailGUI extends InventoryGUI {
    private final DirtCharacters plugin;
    private final int page;
    private final UUID targetUuid;
    private final boolean adminMode;

    public MailGUI(DirtCharacters plugin, int page) {
        this.plugin = plugin;
        this.page = page;
        this.targetUuid = null;
        this.adminMode = false;
    }

    public MailGUI(DirtCharacters plugin, int page, UUID targetUuid) {
        this.plugin = plugin;
        this.page = page;
        this.targetUuid = targetUuid;
        this.adminMode = true;
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 54, adminMode ? "§4Admin: Mailbox" : "§bMailbox");
    }

    @Override
    public void decorate(Player player) {
        UUID effectiveUuid = targetUuid != null ? targetUuid : player.getUniqueId();
        CharacterData data = plugin.getCharacterManager().getCharacter(effectiveUuid);
        List<MailItem> mailbox = data != null ? data.getMailbox() : Collections.emptyList();

        fillPagedGui(54, XMaterial.LIGHT_BLUE_STAINED_GLASS_PANE);

        int perPage = 45;
        int start = page * perPage;
        int end = Math.min(start + perPage, mailbox.size());

        for (int i = start; i < end; i++) {
            MailItem mail = mailbox.get(i);
            int slot = i - start;
            MailViewImpl view = new MailViewImpl(mail, effectiveUuid);
            MailHandler handler = plugin.getMailHandler(mail.getType());
            addButton(slot, new InventoryButton()
                    .creator(p -> {
                        String title = handler != null ? handler.getListTitle(view) : getMailTitle(mail.getType());
                        String sender = mail.getData().getOrDefault("fromName", "Unknown");
                        String dateStr = new SimpleDateFormat("MM/dd/yyyy HH:mm").format(new Date(mail.getTimestamp()));

                        List<String> lore = new ArrayList<>();
                        lore.add("§7From: §f" + sender);
                        lore.add("§7Date: §f" + dateStr);
                        if (handler != null) lore.addAll(handler.getListLore(view));
                        lore.add(adminMode ? "§7Admin view (read-only)" : "§7Click to view");

                        XMaterial icon = XMaterial.matchXMaterial(
                                handler != null ? handler.getIconMaterial(view) : "PAPER").orElse(XMaterial.PAPER);
                        return ItemUtil.buildItem(icon, title, lore.toArray(new String[0]));
                    })
                    .consumer(e -> {
                        if (adminMode) return;
                        Player clicker = (Player) e.getWhoClicked();
                        // Mail owned by another plugin (nation invites, job offers)
                        // is opened by that plugin's handler.
                        if (handler != null) {
                            handler.open(clicker, view);
                        } else {
                            plugin.getGUIManager().openGUI(new MailItemGUI(plugin, mail), clicker);
                        }
                    })
            );
        }

        if (page > 0) {
            int prevPage = page - 1;
            addButton(45, new InventoryButton()
                    .creator(p -> ItemUtil.buildItem(XMaterial.ARROW, "§ePrevious Page"))
                    .consumer(e -> {
                        Player p = (Player) e.getWhoClicked();
                        if (adminMode) plugin.getGUIManager().openGUI(new MailGUI(plugin, prevPage, effectiveUuid), p);
                        else plugin.getGUIManager().openGUI(new MailGUI(plugin, prevPage), p);
                    })
            );
        }

        if (end < mailbox.size()) {
            int nextPage = page + 1;
            addButton(53, new InventoryButton()
                    .creator(p -> ItemUtil.buildItem(XMaterial.ARROW, "§eNext Page"))
                    .consumer(e -> {
                        Player p = (Player) e.getWhoClicked();
                        if (adminMode) plugin.getGUIManager().openGUI(new MailGUI(plugin, nextPage, effectiveUuid), p);
                        else plugin.getGUIManager().openGUI(new MailGUI(plugin, nextPage), p);
                    })
            );
        }

        int totalPages = Math.max(1, (int) Math.ceil((double) mailbox.size() / perPage));
        addPageIndicator(48, page, totalPages);

        addButton(49, new InventoryButton()
                .creator(p -> ItemUtil.buildItem(XMaterial.ARROW, "§cBack"))
                .consumer(e -> {
                    Player p = (Player) e.getWhoClicked();
                    if (adminMode) plugin.getGUIManager().openGUI(new CharacterManagementGUI(plugin, effectiveUuid), p);
                    else plugin.getGUIManager().openGUI(new CharacterManagementGUI(plugin), p);
                })
        );

        super.decorate(player);
    }

    /** Titles for the mail types DirtCharacters owns; the rest come from their handler. */
    private String getMailTitle(String type) {
        return switch (type) {
            case "MARRIAGE_REQUEST" -> "§dMarriage Request";
            case "CHILD_BEARING_REQUEST" -> "§aBear a Child Request";
            case "CHILD_JOIN_REQUEST" -> "§aFamily Join Request";
            case "CONTRACT_OFFER" -> "§eContract to Sign";
            case "CONTRACT_SIGN_REQUEST" -> "§eContract Signing Request";
            case "CONTRACT_BROKEN" -> "§cContract Broken";
            case "CONTRACT_MODIFICATION_REQUEST" -> "§6Modification Request";
            default -> "§7" + MailItemGUI.prettyType(type);
        };
    }
}