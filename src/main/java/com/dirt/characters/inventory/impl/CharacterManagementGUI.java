package com.dirt.characters.inventory.impl;

import com.cryptomorin.xseries.XMaterial;
import com.dirt.characters.DirtCharacters;
import com.dirt.characters.data.CharacterData;
import com.dirt.characters.inventory.InventoryButton;
import com.dirt.characters.inventory.InventoryGUI;
import com.dirt.characters.util.BedrockUtil;
import com.dirt.characters.util.CurrencyUtil;
import com.dirt.characters.util.ItemUtil;
import com.dirt.api.DirtApi;
import com.dirt.api.MenuExtension;
import com.dirt.characters.inventory.impl.contract.ContractListGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CharacterManagementGUI extends InventoryGUI {
    private final DirtCharacters plugin;
    private final UUID targetUuid;
    private final boolean adminMode;

    public CharacterManagementGUI(DirtCharacters plugin) {
        this.plugin = plugin;
        this.targetUuid = null;
        this.adminMode = false;
    }

    public CharacterManagementGUI(DirtCharacters plugin, UUID targetUuid) {
        this.plugin = plugin;
        this.targetUuid = targetUuid;
        this.adminMode = true;
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 27, adminMode ? "§4Admin: Character Mgmt" : "§6Character Management");
    }

    @Override
    public void decorate(Player player) {
        UUID effectiveUuid = targetUuid != null ? targetUuid : player.getUniqueId();
        CharacterData data = plugin.getCharacterManager().getCharacter(effectiveUuid);

        fillGlass(27, XMaterial.ORANGE_STAINED_GLASS_PANE);

        addButton(4, new InventoryButton()
                .creator(p -> {
                    ItemStack skull = BedrockUtil.createPlayerHead(effectiveUuid);
                    if (data != null) {
                        ItemMeta meta = skull.getItemMeta();
                        if (meta != null) {
                            ItemUtil.setDisplayName(meta, "§e" + data.getFirstName() + " " + data.getMiddleName() + " " + data.getLastName());
                            String dateStr = new SimpleDateFormat("MM/dd/yyyy").format(new Date(data.getBirthDate()));
                            String owner = Bukkit.getOfflinePlayer(effectiveUuid).getName();
                            if (adminMode) {
                                ItemUtil.setLore(meta, Arrays.asList(
                                        "§7Owner: §f" + (owner != null ? owner : effectiveUuid.toString()),
                                        "§7Gender: §f" + data.getGender(),
                                        "§7Born: §f" + dateStr,
                                        "§7Status: " + (data.isAlive() ? "§aAlive" : "§cDead"),
                                        "",
                                        "§4§lADMIN VIEW"
                                ));
                            } else {
                                ItemUtil.setLore(meta, Arrays.asList(
                                        "§7Gender: §f" + data.getGender(),
                                        "§7Born: §f" + dateStr
                                ));
                            }
                            skull.setItemMeta(meta);
                        }
                    }
                    return skull;
                })
                .consumer(e -> {})
        );

        if (adminMode) {
            addButton(10, new InventoryButton()
                    .creator(p -> ItemUtil.buildItem(XMaterial.WRITABLE_BOOK, "§4Admin Edit",
                            "§7Edit name, gender, birthdate,",
                            "§7alive status, inheritor, family,",
                            "§7mailbox, or delete this character."))
                    .consumer(e -> plugin.getGUIManager().openGUI(new ServerCharacterDetailGUI(plugin, effectiveUuid), (Player) e.getWhoClicked()))
            );
        } else {
            addButton(10, new InventoryButton()
                    .creator(p -> ItemUtil.buildItem(XMaterial.NAME_TAG, "§6Name",
                            "§7Manage your character's name.",
                            "§7Cost to change: §e" + CurrencyUtil.symbol() + "100,000"))
                    .consumer(e -> {
                        Player p = (Player) e.getWhoClicked();
                        plugin.getCharacterManager().initPendingNameChange(p.getUniqueId());
                        plugin.getGUIManager().openGUI(new NameManagementGUI(plugin), p);
                    })
            );

            if (plugin.getSettings().isCharacterBiosEnabled()) {
                String bioPreview = data != null && !data.getBio().isBlank() ? abbreviate(data.getBio(), 42) : "No bio set.";
                addButton(11, new InventoryButton()
                        .creator(p -> ItemUtil.buildItem(XMaterial.WRITABLE_BOOK, "§eCharacter Bio",
                                "§7" + bioPreview,
                                "§eClick to edit. §7Type §fcancel §7to abort."))
                        .consumer(e -> {
                            Player p = (Player) e.getWhoClicked();
                            plugin.getChatInputManager().requestInput(p,
                                    "§eEnter your character bio (max " + plugin.getSettings().getCharacterBioMaxLength() + " characters):",
                                    input -> {
                                        com.dirt.api.CharacterMutationResult result = plugin.getCharactersService()
                                                .updateCharacterBio(p.getUniqueId(), input);
                                        if (result.isSuccess()) {
                                            p.sendMessage("§aCharacter bio updated.");
                                        } else {
                                            p.sendMessage("§cBio must be at most " + plugin.getSettings().getCharacterBioMaxLength() + " characters.");
                                        }
                                        plugin.getGUIManager().openGUI(new CharacterManagementGUI(plugin), p);
                                    });
                        })
                );
            }
        }

        if (adminMode) {
            addButton(12, new InventoryButton()
                    .creator(p -> ItemUtil.buildItem(
                            data != null && data.isAlive() ? XMaterial.TOTEM_OF_UNDYING : XMaterial.WITHER_SKELETON_SKULL,
                            "§cLife Status",
                            "§7Current: " + (data != null && data.isAlive() ? "§aAlive" : "§cDead"),
                            "§7Use Admin Edit to toggle."))
                    .consumer(e -> {})
            );
        } else {
            // The life screen belongs to DirtLife; without it the slot stays empty.
            if (DirtApi.life() != null) {
                addButton(12, new InventoryButton()
                        .creator(p -> ItemUtil.buildItem(XMaterial.SKELETON_SKULL, "§cLife",
                                "§7Manage your character's life.",
                                "§7Here you can create a death."))
                        .consumer(e -> DirtApi.life().openLifeMenu((Player) e.getWhoClicked()))
                );
            }
        }

        if (plugin.isFamilyEnabled() && !adminMode) {
            addButton(14, new InventoryButton()
                    .creator(p -> ItemUtil.buildItem(XMaterial.HEART_OF_THE_SEA, "§dFamily",
                            "§7Manage your family."))
                    .consumer(e -> {
                        Player p = (Player) e.getWhoClicked();
                        plugin.getGUIManager().openGUI(new FamilyGUI(plugin), p);
                    })
            );
        } else if (plugin.isFamilyEnabled() && adminMode) {
            addButton(14, new InventoryButton()
                    .creator(p -> ItemUtil.buildItem(XMaterial.HEART_OF_THE_SEA, "§dFamily Info",
                            "§7Family: §f" + (data != null && data.getFamilyId() != null ? data.getFamilyId().toString() : "(none)"),
                            "§7Role: §f" + (data != null && data.getFamilyRole() != null ? data.getFamilyRole() : "(none)"),
                            "§7Use Admin Edit to modify."))
                    .consumer(e -> {})
            );
        }

        addButton(16, new InventoryButton()
                .creator(p -> {
                    int count = data != null ? data.getMailbox().size() : 0;
                    return ItemUtil.buildItem(XMaterial.PAPER, "§bMail",
                            "§7" + count + " message(s) in " + (adminMode ? "their" : "your") + " mailbox.",
                            "§7Click to view.");
                })
                .consumer(e -> {
                    Player p = (Player) e.getWhoClicked();
                    if (adminMode) {
                        plugin.getGUIManager().openGUI(new MailGUI(plugin, 0, effectiveUuid), p);
                    } else {
                        plugin.getGUIManager().openGUI(new MailGUI(plugin, 0), p);
                    }
                })
        );

        // Buttons contributed by DirtNations (properties), DirtBiz (job,
        // businesses) and anything else that registers a menu extension.
        occupied.clear();
        renderExtensions(effectiveUuid);

        if (plugin.isContractsEnabled() && !adminMode) {
            addButton(18, new InventoryButton()
                    .creator(p -> ItemUtil.buildItem(XMaterial.WRITABLE_BOOK, "§eContracts",
                            "§7View your signed contracts."))
                    .consumer(e -> {
                        Player p = (Player) e.getWhoClicked();
                        plugin.getGUIManager().openGUI(new ContractListGUI(plugin, null, p.getUniqueId(), 0), p);
                    })
            );
        }

        if (adminMode) {
            addButton(0, new InventoryButton()
                    .creator(p -> ItemUtil.buildItem(XMaterial.ARROW, "§cBack to List"))
                    .consumer(e -> plugin.getGUIManager().openGUI(new ServerCharacterListGUI(plugin, 0, null), (Player) e.getWhoClicked()))
            );
        }

        super.decorate(player);
    }

    /**
     * Places each registered {@link MenuExtension} at its preferred slot, sliding
     * to the next free slot on a collision so two plugins can never overwrite
     * each other's button.
     */
    private void renderExtensions(UUID effectiveUuid) {
        List<MenuExtension> extensions = DirtApi.getMenuExtensions(MenuExtension.Screen.CHARACTER_MENU);
        for (MenuExtension extension : extensions) {
            if (!extension.isVisible(effectiveUuid, adminMode)) continue;

            int slot = nextFreeSlot(extension.getPreferredSlot());
            if (slot < 0) continue;

            boolean clickable = extension.isClickable(effectiveUuid, adminMode);
            addButton(slot, new InventoryButton()
                    .creator(p -> ItemUtil.buildItem(
                            XMaterial.matchXMaterial(extension.getIconMaterial()).orElse(XMaterial.PAPER),
                            extension.getTitle(effectiveUuid, adminMode),
                            extension.getLore(effectiveUuid, adminMode).toArray(new String[0])))
                    .consumer(e -> {
                        if (!clickable) return;
                        extension.onClick((Player) e.getWhoClicked(), effectiveUuid, adminMode);
                    })
            );
        }
    }

    /** The preferred slot if it only holds filler glass, else the next such slot. */
    private int nextFreeSlot(int preferred) {
        int size = 27;
        for (int offset = 0; offset < size; offset++) {
            int slot = (preferred + offset) % size;
            if (slot == 4 || slot == 10 || slot == 11 || slot == 12 || slot == 14 || slot == 16 || slot == 18) continue;
            if (slot % 9 == 0 || slot % 9 == 8) continue;
            if (!occupied.contains(slot)) {
                occupied.add(slot);
                return slot;
            }
        }
        return -1;
    }

    private final java.util.Set<Integer> occupied = new java.util.HashSet<>();

    private String abbreviate(String value, int maxLength) {
        return value.length() <= maxLength ? value : value.substring(0, maxLength - 3) + "...";
    }
}
