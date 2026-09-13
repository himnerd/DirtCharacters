package com.dirt.characters.inventory.impl;

import com.cryptomorin.xseries.XMaterial;
import com.dirt.characters.DirtCharacters;
import com.dirt.characters.data.CharacterData;
import com.dirt.characters.data.FamilyData;
import com.dirt.characters.data.MailItem;
import com.dirt.characters.data.ContractData;
import com.dirt.characters.inventory.InventoryButton;
import com.dirt.characters.inventory.InventoryGUI;
import com.dirt.characters.util.CurrencyUtil;
import com.dirt.characters.util.ItemUtil;
import com.dirt.characters.inventory.impl.contract.ContractViewGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MailItemGUI extends InventoryGUI {
    private final DirtCharacters plugin;
    private final MailItem mail;

    public MailItemGUI(DirtCharacters plugin, MailItem mail) {
        this.plugin = plugin;
        this.mail = mail;
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 27, "\u00a7bMail");
    }

    @Override
    public void decorate(Player player) {
        fillGlass(27, XMaterial.LIGHT_BLUE_STAINED_GLASS_PANE);

        switch (mail.getType()) {
            case "MARRIAGE_REQUEST" -> decorateMarriageRequest(player);
            case "CHILD_BEARING_REQUEST" -> decorateChildBearingRequest(player);
            case "CHILD_JOIN_REQUEST" -> decorateChildJoinRequest(player);
            case "CONTRACT_OFFER", "CONTRACT_SIGN_REQUEST" -> decorateContractOffer(player);
            case "CONTRACT_BROKEN" -> decorateContractBroken(player);
            case "CONTRACT_MODIFICATION_REQUEST" -> decorateModificationRequest(player);
            // Nation, business and shop mail is rendered by the plugin that owns
            // it; anything with no handler falls back to a plain notification so
            // mail is never stranded when a plugin is uninstalled.
            default -> decorateNotification(player);
        }

        addButton(26, new InventoryButton()
                .creator(p -> ItemUtil.buildItem(XMaterial.ARROW, "\u00a7cBack"))
                .consumer(e -> plugin.getGUIManager().openGUI(new MailGUI(plugin, 0), (Player) e.getWhoClicked()))
        );

        super.decorate(player);
    }

    private void decorateMarriageRequest(Player player) {
        String fromName = mail.getData().getOrDefault("fromName", "Unknown");
        String birthDateStr;
        try {
            long bd = Long.parseLong(mail.getData().getOrDefault("fromBirthDate", "0"));
            birthDateStr = new SimpleDateFormat("MM/dd/yyyy").format(new Date(bd));
        } catch (NumberFormatException ignored) {
            birthDateStr = "Unknown";
        }
        String balanceStr = mail.getData().getOrDefault("fromBalance", "0");
        String formattedBalance;
        try {
            formattedBalance = CurrencyUtil.symbol() + String.format("%.0f", Double.parseDouble(balanceStr));
        } catch (NumberFormatException e) {
            formattedBalance = CurrencyUtil.symbol() + "0";
        }

        final String finalBirthDateStr = birthDateStr;
        final String finalFormattedBalance = formattedBalance;

        addButton(4, new InventoryButton()
                .creator(p -> ItemUtil.buildItem(XMaterial.GOLD_INGOT, "\u00a7dMarriage Request",
                        "\u00a77From: \u00a7f" + fromName,
                        "\u00a77Their birthday: \u00a7f" + finalBirthDateStr,
                        "\u00a77Their balance: \u00a7e" + finalFormattedBalance,
                        "\u00a77Accepting this marriage will cause you to",
                        "\u00a77inherit this character's last name, and give",
                        "\u00a77access to their belongings. As well as create",
                        "\u00a77the ability to have children with this character."))
                .consumer(e -> {})
        );

        addButton(11, new InventoryButton()
                .creator(p -> ItemUtil.buildItem(XMaterial.LIME_WOOL, "\u00a7aAccept Marriage"))
                .consumer(e -> acceptMarriage((Player) e.getWhoClicked()))
        );

        addButton(15, new InventoryButton()
                .creator(p -> ItemUtil.buildItem(XMaterial.RED_WOOL, "\u00a7cDeny"))
                .consumer(e -> {
                    Player p = (Player) e.getWhoClicked();
                    plugin.getCharacterManager().removeMailItem(p.getUniqueId(), mail.getId());
                    p.sendMessage("\u00a7cYou denied the marriage request.");
                    plugin.getGUIManager().openGUI(new MailGUI(plugin, 0), p);
                })
        );
    }

    private void acceptMarriage(Player player) {
        UUID fromUUID = mail.getFromPlayerUuid();
        if (fromUUID == null) { player.sendMessage("\u00a7cInvalid mail data."); return; }

        CharacterData myData = plugin.getCharacterManager().getCharacter(player.getUniqueId());
        CharacterData fromData = plugin.getCharacterManager().getCharacter(fromUUID);

        if (myData == null || fromData == null) {
            player.sendMessage("\u00a7cCharacter data not found.");
            return;
        }

        boolean myBlocked = myData.getFamilyId() != null && !"CHILD".equals(myData.getFamilyRole());
        boolean fromBlocked = fromData.getFamilyId() != null && !"CHILD".equals(fromData.getFamilyRole());

        if (myBlocked) {
            player.sendMessage("\u00a7cYou are already married.");
            plugin.getCharacterManager().removeMailItem(player.getUniqueId(), mail.getId());
            return;
        }
        if (fromBlocked) {
            player.sendMessage("\u00a7cThe sender is already married. Request cancelled.");
            plugin.getCharacterManager().removeMailItem(player.getUniqueId(), mail.getId());
            return;
        }

        FamilyData newFamily = plugin.getFamilyManager().createFamily(fromUUID, player.getUniqueId());

        myData.setLastName(fromData.getLastName());

        if ("CHILD".equals(fromData.getFamilyRole())) {
            fromData.setBirthFamilyId(fromData.getFamilyId());
        }
        fromData.setFamilyId(newFamily.getFamilyId());
        fromData.setFamilyRole("PRIMARY");

        if ("CHILD".equals(myData.getFamilyRole())) {
            myData.setBirthFamilyId(myData.getFamilyId());
        }
        myData.setFamilyId(newFamily.getFamilyId());
        myData.setFamilyRole("SECONDARY");

        plugin.getCharacterManager().saveCharacter(myData);
        plugin.getCharacterManager().saveCharacter(fromData);
        plugin.getCharacterManager().removeMailItem(player.getUniqueId(), mail.getId());

        player.sendMessage("\u00a7aYou are now married to \u00a7e" + fromData.getFirstName() + " " + fromData.getLastName() + "\u00a7a! Your last name is now \u00a7e" + myData.getLastName() + "\u00a7a.");

        Player fromPlayer = Bukkit.getPlayer(fromUUID);
        if (fromPlayer != null) {
            fromPlayer.sendMessage("\u00a7a" + myData.getFirstName() + " " + myData.getLastName() + " accepted your marriage request! You are now married!");
        }

        plugin.getGUIManager().openGUI(new MailGUI(plugin, 0), player);
    }

    private void decorateChildBearingRequest(Player player) {
        String fromName = mail.getData().getOrDefault("fromName", "Unknown");

        addButton(4, new InventoryButton()
                .creator(p -> ItemUtil.buildItem(XMaterial.PINK_DYE, "\u00a7aBear a Child Request",
                        "\u00a77From: \u00a7f" + fromName,
                        "\u00a77Your spouse wants to add a child to your family.",
                        "\u00a77If you accept, your spouse will select a character",
                        "\u00a77to become your child."))
                .consumer(e -> {})
        );

        addButton(11, new InventoryButton()
                .creator(p -> ItemUtil.buildItem(XMaterial.LIME_WOOL, "\u00a7aAccept"))
                .consumer(e -> {
                    Player p = (Player) e.getWhoClicked();
                    UUID fromUUID = mail.getFromPlayerUuid();
                    plugin.getCharacterManager().removeMailItem(p.getUniqueId(), mail.getId());

                    Player fromPlayer = Bukkit.getPlayer(fromUUID);
                    if (fromPlayer != null) {
                        fromPlayer.sendMessage("\u00a7eYour spouse accepted! Please select a child for your family.");
                        plugin.getGUIManager().openGUI(new ChildSelectionGUI(plugin, 0), fromPlayer);
                    } else {
                        CharacterData fromData = plugin.getCharacterManager().getCharacter(fromUUID);
                        if (fromData != null) {
                            fromData.setPendingChildSelection(true);
                            plugin.getCharacterManager().saveCharacter(fromData);
                        }
                    }

                    p.sendMessage("\u00a7aYou accepted the child bearing request.");
                    plugin.getGUIManager().openGUI(new MailGUI(plugin, 0), p);
                })
        );

        addButton(15, new InventoryButton()
                .creator(p -> ItemUtil.buildItem(XMaterial.RED_WOOL, "\u00a7cDeny"))
                .consumer(e -> {
                    Player p = (Player) e.getWhoClicked();
                    plugin.getCharacterManager().removeMailItem(p.getUniqueId(), mail.getId());
                    p.sendMessage("\u00a7cYou denied the child bearing request.");
                    plugin.getGUIManager().openGUI(new MailGUI(plugin, 0), p);
                })
        );
    }

    private void decorateChildJoinRequest(Player player) {
        String fromName = mail.getData().getOrDefault("fromName", "Unknown");
        String familyIdStr = mail.getData().getOrDefault("familyId", "");

        addButton(4, new InventoryButton()
                .creator(p -> ItemUtil.buildItem(XMaterial.HEART_OF_THE_SEA, "\u00a7aFamily Join Request",
                        "\u00a77From: \u00a7f" + fromName,
                        "\u00a77" + fromName + " wants you to become",
                        "\u00a77a child member of their family."))
                .consumer(e -> {})
        );

        addButton(11, new InventoryButton()
                .creator(p -> ItemUtil.buildItem(XMaterial.LIME_WOOL, "\u00a7aAccept"))
                .consumer(e -> {
                    Player p = (Player) e.getWhoClicked();
                    if (familyIdStr.isEmpty()) {
                        p.sendMessage("\u00a7cInvalid family data.");
                        return;
                    }

                    CharacterData myData = plugin.getCharacterManager().getCharacter(p.getUniqueId());
                    if (myData == null) return;
                    if (myData.getFamilyId() != null) {
                        p.sendMessage("\u00a7cYou are already in a family.");
                        plugin.getCharacterManager().removeMailItem(p.getUniqueId(), mail.getId());
                        return;
                    }

                    UUID familyId;
                    try {
                        familyId = UUID.fromString(familyIdStr);
                    } catch (IllegalArgumentException ex) {
                        p.sendMessage("\u00a7cInvalid family ID.");
                        return;
                    }

                    FamilyData family = plugin.getFamilyManager().loadFamily(familyId);
                    if (family == null) {
                        p.sendMessage("\u00a7cThis family no longer exists.");
                        plugin.getCharacterManager().removeMailItem(p.getUniqueId(), mail.getId());
                        return;
                    }

                    family.getChildren().add(p.getUniqueId());
                    plugin.getFamilyManager().saveFamily(family);

                    myData.setFamilyId(familyId);
                    myData.setFamilyRole("CHILD");
                    setDefaultInheritor(family, p.getUniqueId());
                    plugin.getCharacterManager().saveCharacter(myData);
                    plugin.getCharacterManager().removeMailItem(p.getUniqueId(), mail.getId());

                    p.sendMessage("\u00a7aYou have joined the family of \u00a7e" + fromName + "\u00a7a!");

                    Player fromPlayer = Bukkit.getPlayer(mail.getFromPlayerUuid());
                    if (fromPlayer != null) {
                        fromPlayer.sendMessage("\u00a7e" + myData.getFirstName() + " " + myData.getLastName() + " has joined your family as a child!");
                    }

                    plugin.getGUIManager().openGUI(new MailGUI(plugin, 0), p);
                })
        );

        addButton(15, new InventoryButton()
                .creator(p -> ItemUtil.buildItem(XMaterial.RED_WOOL, "\u00a7cDeny"))
                .consumer(e -> {
                    Player p = (Player) e.getWhoClicked();
                    plugin.getCharacterManager().removeMailItem(p.getUniqueId(), mail.getId());
                    p.sendMessage("\u00a7cYou denied the family join request.");
                    plugin.getGUIManager().openGUI(new MailGUI(plugin, 0), p);
                })
        );
    }

    private void setDefaultInheritor(FamilyData family, UUID childUUID) {
        if (family.getSpouse1() != null) {
            CharacterData s1 = plugin.getCharacterManager().getCharacter(family.getSpouse1());
            if (s1 != null && s1.getInheritorUuid() == null) {
                s1.setInheritorUuid(childUUID);
                plugin.getCharacterManager().saveCharacter(s1);
            }
        }
        if (family.getSpouse2() != null) {
            CharacterData s2 = plugin.getCharacterManager().getCharacter(family.getSpouse2());
            if (s2 != null && s2.getInheritorUuid() == null) {
                s2.setInheritorUuid(childUUID);
                plugin.getCharacterManager().saveCharacter(s2);
            }
        }
    }

    private void decorateContractOffer(Player player) {
        String contractIdStr = mail.getData().getOrDefault("contractId", "");
        String fromName = mail.getData().getOrDefault("fromName", "Unknown");
        String summary = mail.getData().getOrDefault("summary", "No summary");

        addButton(4, new InventoryButton()
                .creator(p -> ItemUtil.buildItem(XMaterial.WRITABLE_BOOK, "\u00a7eContract",
                        "\u00a77From: \u00a7f" + fromName,
                        "\u00a77" + summary,
                        "\u00a77Click below to view and sign."))
                .consumer(e -> {})
        );

        addButton(11, new InventoryButton()
                .creator(p -> ItemUtil.buildItem(XMaterial.LIME_WOOL, "\u00a7aView & Sign"))
                .consumer(e -> {
                    Player p = (Player) e.getWhoClicked();
                    if (contractIdStr.isEmpty() || plugin.getContractManager() == null) {
                        p.sendMessage("\u00a7cInvalid contract data."); return;
                    }
                    UUID contractId;
                    try { contractId = UUID.fromString(contractIdStr); } catch (IllegalArgumentException ex) {
                        p.sendMessage("\u00a7cInvalid contract ID."); return;
                    }
                    ContractData contract = plugin.getContractManager().loadContract(contractId);
                    if (contract == null) {
                        p.sendMessage("\u00a7cThis contract no longer exists.");
                        plugin.getCharacterManager().removeMailItem(p.getUniqueId(), mail.getId());
                        plugin.getGUIManager().openGUI(new MailGUI(plugin, 0), p);
                        return;
                    }
                    plugin.getGUIManager().openGUI(new ContractViewGUI(plugin, contractId, mail.getId()), p);
                })
        );

        addButton(13, new InventoryButton()
                .creator(p -> ItemUtil.buildItem(XMaterial.ORANGE_WOOL, "\u00a76Request Modification"))
                .consumer(e -> {
                    Player p = (Player) e.getWhoClicked();
                    p.closeInventory();
                    plugin.getChatInputManager().requestInput(p, "\u00a7eType your modification request:", msg -> {
                        UUID senderUuid = mail.getFromPlayerUuid();
                        MailItem response = new MailItem();
                        response.setId(UUID.randomUUID().toString());
                        response.setType("CONTRACT_MODIFICATION_REQUEST");
                        response.setFromPlayerUuid(p.getUniqueId());
                        response.setTimestamp(System.currentTimeMillis());
                        CharacterData myChar = plugin.getCharacterManager().getCharacter(p.getUniqueId());
                        String myName = myChar != null ? myChar.getFirstName() + " " + myChar.getLastName() : p.getName();
                        java.util.Map<String, String> data = new java.util.HashMap<>();
                        data.put("fromName", myName);
                        data.put("contractId", contractIdStr);
                        data.put("message", msg);
                        response.setData(data);
                        plugin.getCharacterManager().addMailItem(senderUuid, response);
                        plugin.getCharacterManager().removeMailItem(p.getUniqueId(), mail.getId());
                        p.sendMessage("\u00a7aModification request sent.");
                        plugin.getPlatformScheduler().runAtEntity(p, () -> plugin.getGUIManager().openGUI(new MailGUI(plugin, 0), p));
                    });
                })
        );

        addButton(15, new InventoryButton()
                .creator(p -> ItemUtil.buildItem(XMaterial.RED_WOOL, "\u00a7cDeny"))
                .consumer(e -> {
                    Player p = (Player) e.getWhoClicked();
                    plugin.getCharacterManager().removeMailItem(p.getUniqueId(), mail.getId());
                    if (!contractIdStr.isEmpty() && plugin.getContractManager() != null) {
                        try {
                            UUID cid = UUID.fromString(contractIdStr);
                            ContractData c = plugin.getContractManager().loadContract(cid);
                            if (c != null) {
                                c.setStatus("DENIED");
                                plugin.getContractManager().saveContract(c);
                            }
                        } catch (IllegalArgumentException ignored) {}
                    }
                    p.sendMessage("\u00a7cContract denied.");
                    plugin.getGUIManager().openGUI(new MailGUI(plugin, 0), p);
                })
        );
    }

    private void decorateContractBroken(Player player) {
        String fromName = mail.getData().getOrDefault("fromName", "Unknown");
        String contractSummary = mail.getData().getOrDefault("summary", "A contract");

        addButton(4, new InventoryButton()
                .creator(p -> ItemUtil.buildItem(XMaterial.BARRIER, "\u00a7cContract Broken",
                        "\u00a77" + fromName + " has broken the contract:",
                        "\u00a77" + contractSummary))
                .consumer(e -> {})
        );

        addButton(13, new InventoryButton()
                .creator(p -> ItemUtil.buildItem(XMaterial.RED_WOOL, "\u00a7cDismiss"))
                .consumer(e -> {
                    Player p = (Player) e.getWhoClicked();
                    plugin.getCharacterManager().removeMailItem(p.getUniqueId(), mail.getId());
                    plugin.getGUIManager().openGUI(new MailGUI(plugin, 0), p);
                })
        );
    }

    private void decorateModificationRequest(Player player) {
        String fromName = mail.getData().getOrDefault("fromName", "Unknown");
        String message = mail.getData().getOrDefault("message", "No message");

        addButton(4, new InventoryButton()
                .creator(p -> ItemUtil.buildItem(XMaterial.WRITABLE_BOOK, "\u00a76Modification Request",
                        "\u00a77From: \u00a7f" + fromName,
                        "\u00a77Message: \u00a7f" + message))
                .consumer(e -> {})
        );

        addButton(13, new InventoryButton()
                .creator(p -> ItemUtil.buildItem(XMaterial.RED_WOOL, "\u00a7cDismiss"))
                .consumer(e -> {
                    Player p = (Player) e.getWhoClicked();
                    plugin.getCharacterManager().removeMailItem(p.getUniqueId(), mail.getId());
                    plugin.getGUIManager().openGUI(new MailGUI(plugin, 0), p);
                })
        );
    }

    /**
     * Fallback renderer for mail types DirtCharacters does not own. Shows every
     * payload entry as lore plus a dismiss button, so an uninstalled plugin
     * leaves readable — and clearable — mail behind rather than a dead item.
     */
    private void decorateNotification(Player player) {
        String title = mail.getData().getOrDefault("title", prettyType(mail.getType()));
        String from = mail.getData().getOrDefault("fromName", null);
        String timeStr = new SimpleDateFormat("MM/dd/yyyy HH:mm").format(new Date(mail.getTimestamp()));

        List<String> lore = new java.util.ArrayList<>();
        if (from != null) lore.add("\u00a77From: \u00a7f" + from);
        lore.add("\u00a77Received: \u00a7f" + timeStr);
        for (Map.Entry<String, String> entry : mail.getData().entrySet()) {
            String key = entry.getKey();
            if (key.equals("title") || key.equals("fromName")) continue;
            if (entry.getValue() == null || entry.getValue().isEmpty()) continue;
            lore.add("\u00a77" + prettyType(key) + ": \u00a7f" + entry.getValue());
        }

        addButton(4, new InventoryButton()
                .creator(p -> ItemUtil.buildItem(XMaterial.PAPER, "\u00a79" + title, lore.toArray(new String[0])))
                .consumer(e -> {})
        );

        addButton(13, new InventoryButton()
                .creator(p -> ItemUtil.buildItem(XMaterial.RED_WOOL, "\u00a7cDismiss"))
                .consumer(e -> {
                    Player p = (Player) e.getWhoClicked();
                    plugin.getCharacterManager().removeMailItem(p.getUniqueId(), mail.getId());
                    plugin.getGUIManager().openGUI(new MailGUI(plugin, 0), p);
                })
        );
    }

    /** {@code "NATION_INVITE"} / {@code "propertyName"} -> {@code "Nation Invite"} / {@code "Property Name"}. */
    static String prettyType(String raw) {
        if (raw == null || raw.isEmpty()) return "Message";
        String spaced = raw.replace('_', ' ').replaceAll("([a-z])([A-Z])", "$1 $2");
        StringBuilder out = new StringBuilder(spaced.length());
        boolean newWord = true;
        for (char c : spaced.toCharArray()) {
            if (c == ' ') {
                newWord = true;
                out.append(c);
            } else {
                out.append(newWord ? Character.toUpperCase(c) : Character.toLowerCase(c));
                newWord = false;
            }
        }
        return out.toString();
    }
}
