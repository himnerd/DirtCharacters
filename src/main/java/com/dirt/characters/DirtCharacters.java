package com.dirt.characters;

import com.dirt.api.DirtApi;
import com.dirt.api.events.CharacterDeathEvent;
import com.dirt.api.MailHandler;
import com.dirt.characters.api.CharactersServiceImpl;
import com.dirt.characters.commands.DirtCCommand;
import com.dirt.characters.commands.DirtCharactersCommand;
import com.dirt.characters.commands.DirtIDCommand;
import com.dirt.characters.commands.FastTravelCommand;
import com.dirt.characters.commands.HomeCommand;
import com.dirt.characters.commands.MenuCommand;
import com.dirt.characters.data.CharacterData;
import com.dirt.characters.data.FamilyData;
import com.dirt.characters.inventory.gui.GUIListener;
import com.dirt.characters.inventory.gui.GUIManager;
import com.dirt.characters.inventory.impl.CharacterCreationGUI;
import com.dirt.characters.listeners.ChestProtectionListener;
import com.dirt.characters.listeners.DirtHandsListener;
import com.dirt.characters.listeners.FastTravelListener;
import com.dirt.characters.listeners.IDInteractListener;
import com.dirt.characters.listeners.JoinListener;
import com.dirt.characters.managers.CharacterManager;
import com.dirt.characters.managers.ChestProtectionManager;
import com.dirt.characters.managers.ContractManager;
import com.dirt.characters.managers.FamilyManager;
import com.dirt.characters.managers.FastTravelManager;
import com.dirt.characters.managers.IDManager;
import com.dirt.characters.scheduler.PlatformScheduler;
import com.dirt.characters.util.ChatInputManager;
import com.dirt.characters.util.ConfigValidator;
import com.dirt.characters.util.CurrencyUtil;
import com.dirt.characters.util.LegacyImporter;
import com.dirt.characters.util.LocationSelectionManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DirtCharacters — the character, identity, family, mail and contract core of
 * the Dirt suite.
 *
 * <p>Runs standalone on Vault. It also ships the {@code com.dirt.api} contract
 * that lets DirtNations, DirtBiz, DirtShops and DirtLife plug into the character
 * menu, the mailbox and the death pipeline.
 */
public class DirtCharacters extends JavaPlugin {

    private Economy economy;
    private Settings settings;
    private PlatformScheduler platformScheduler;
    private GUIManager guiManager;

    private CharacterManager characterManager;
    private FamilyManager familyManager;
    private IDManager idManager;
    private ChestProtectionManager chestProtectionManager;
    private ContractManager contractManager;
    private FastTravelManager fastTravelManager;

    private CharactersServiceImpl charactersService;

    private ChatInputManager chatInputManager;
    private LocationSelectionManager locationSelectionManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        ConfigValidator.validateAll(this);
        LegacyImporter.run(this);

        this.settings = new Settings(this);
        this.platformScheduler = new PlatformScheduler(this);

        if (!setupEconomy()) {
            getLogger().severe("Vault or a compatible economy plugin was not found. Disabling DirtCharacters.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        CurrencyUtil.init();

        this.idManager = new IDManager(this);
        this.characterManager = new CharacterManager(this, idManager);
        this.familyManager = new FamilyManager(this);
        this.chestProtectionManager = new ChestProtectionManager(this);
        this.guiManager = new GUIManager(this);
        this.chatInputManager = new ChatInputManager(this);
        this.locationSelectionManager = new LocationSelectionManager();

        if (settings.isContractsEnabled()) {
            this.contractManager = new ContractManager(this);
            platformScheduler.runGlobalTimer(() -> contractManager.checkExpiredContracts(), 24000L, 24000L);
        }

        if (settings.isFastTravelEnabled()) {
            this.fastTravelManager = new FastTravelManager(this);
            getServer().getPluginManager().registerEvents(new FastTravelListener(this), this);
        }

        getServer().getPluginManager().registerEvents(new GUIListener(guiManager), this);
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        getServer().getPluginManager().registerEvents(new ChestProtectionListener(this), this);
        getServer().getPluginManager().registerEvents(new IDInteractListener(this), this);
        getServer().getPluginManager().registerEvents(chatInputManager, this);
        getServer().getPluginManager().registerEvents(locationSelectionManager, this);

        if (settings.isDirtHandsEnabled()) {
            getServer().getPluginManager().registerEvents(new DirtHandsListener(this), this);
        }

        registerCommand("dirtcharacters", new DirtCharactersCommand(this));
        registerCommand("dc", new DirtCCommand(this));
        registerCommand("did", new DirtIDCommand(this));
        registerCommand("menu", new MenuCommand(this));

        HomeCommand homeCommand = new HomeCommand(this);
        registerCommand("sethome", homeCommand);
        registerCommand("home", homeCommand);

        if (settings.isFastTravelEnabled()) {
            registerCommand("fasttravel", new FastTravelCommand(this));
        }

        this.charactersService = new CharactersServiceImpl(this);
        DirtApi.registerCharacters(charactersService);
        getLogger().info("DirtCharacters enabled.");
    }

    @Override
    public void onDisable() {
        DirtApi.unregisterAll(getClass().getClassLoader());
        if (platformScheduler != null) platformScheduler.cancelAllTasks();
        getLogger().info("DirtCharacters disabled.");
    }

    private void registerCommand(String name, Object executor) {
        var command = getCommand(name);
        if (command == null) {
            getLogger().warning("Command /" + name + " is missing from plugin.yml — skipping registration.");
            return;
        }
        command.setExecutor((org.bukkit.command.CommandExecutor) executor);
        if (executor instanceof org.bukkit.command.TabCompleter completer) {
            command.setTabCompleter(completer);
        }
    }

    public void reloadData() {
        ConfigValidator.validateAll(this);
        settings.reload();
        if (characterManager != null) characterManager.reloadCache();
        if (familyManager != null) familyManager.reloadCache();
        if (contractManager != null) contractManager.reloadCache();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        economy = rsp.getProvider();
        return economy != null;
    }

    // ──── Accessors ────

    public Economy getEconomy() { return economy; }
    public Settings getSettings() { return settings; }
    public PlatformScheduler getPlatformScheduler() { return platformScheduler; }
    public GUIManager getGUIManager() { return guiManager; }
    public CharacterManager getCharacterManager() { return characterManager; }
    public FamilyManager getFamilyManager() { return familyManager; }
    public IDManager getIDManager() { return idManager; }
    public ChestProtectionManager getChestProtectionManager() { return chestProtectionManager; }
    public ContractManager getContractManager() { return contractManager; }
    public FastTravelManager getFastTravelManager() { return fastTravelManager; }
    public ChatInputManager getChatInputManager() { return chatInputManager; }
    public CharactersServiceImpl getCharactersService() { return charactersService; }

    /**
     * The handler another plugin registered for a mail type, or null when
     * DirtCharacters renders it itself.
     */
    public MailHandler getMailHandler(String type) {
        return charactersService != null ? charactersService.mailImpl().getHandler(type) : null;
    }

    public LocationSelectionManager getLocationSelectionManager() { return locationSelectionManager; }

    public boolean isFamilyEnabled() { return settings.isFamilyEnabled(); }
    public boolean isContractsEnabled() { return settings.isContractsEnabled() && contractManager != null; }

    // ──── Death pipeline ────

    /**
     * Ends a character permanently: transfers the balance, dissolves family ties,
     * drops an inheritance chest, notifies the rest of the suite through
     * {@link CharacterDeathEvent}, then reopens character creation.
     *
     * <p>DirtLife drives this from the death screen; it is also reachable from
     * the in-game "create a death" flow.
     */
    public void executeDeath(Player player, Location chestLocation) {
        CharacterData character = characterManager.getCharacter(player.getUniqueId());
        if (character == null) return;

        UUID inheritorUuid = resolveInheritor(player.getUniqueId());

        double balance = economy.getBalance(player);
        if (balance > 0) {
            economy.withdrawPlayer(player, balance);
            if (inheritorUuid != null) {
                economy.depositPlayer(Bukkit.getOfflinePlayer(inheritorUuid), balance);
                Player inheritor = Bukkit.getPlayer(inheritorUuid);
                if (inheritor != null) {
                    inheritor.sendMessage("§e" + character.getFirstName() + " " + character.getLastName()
                            + " has died and left you §a" + CurrencyUtil.symbol() + String.format("%.0f", balance)
                            + "§e and an inheritance chest.");
                }
            }
        }

        // Let the rest of the suite move properties, businesses and shops across
        // while the character record still exists.
        Bukkit.getPluginManager().callEvent(new CharacterDeathEvent(player, player.getUniqueId(), inheritorUuid));

        if (character.getFamilyId() != null) {
            FamilyData family = familyManager.loadFamily(character.getFamilyId());
            if (family != null) removeFromFamily(player.getUniqueId(), family);
        }

        if (inheritorUuid != null && settings.isInheritanceChestEnabled() && chestLocation != null) {
            spawnInheritanceChest(player, chestLocation, inheritorUuid);
        } else {
            player.getInventory().clear();
        }

        characterManager.deleteCharacter(player.getUniqueId());
        characterManager.addForcedCreation(player.getUniqueId());
        characterManager.initPendingCreation(player.getUniqueId());

        player.sendMessage("§cYour character has died. Please create a new character.");

        platformScheduler.runAtEntityLater(player, () ->
                guiManager.openGUI(new CharacterCreationGUI(this), player), settings.getDeathRespawnDelayTicks());
    }

    /** Walks the inheritor chain until it finds someone who still has a living character. */
    private UUID resolveInheritor(UUID deceasedUuid) {
        CharacterData deceased = characterManager.getCharacter(deceasedUuid);
        if (deceased == null) return null;

        UUID next = deceased.getInheritorUuid();
        java.util.Set<UUID> seen = new java.util.HashSet<>();
        seen.add(deceasedUuid);
        while (next != null && seen.add(next)) {
            if (characterManager.hasCharacter(next)) return next;
            CharacterData nextData = characterManager.getCharacter(next);
            next = nextData != null ? nextData.getInheritorUuid() : null;
        }
        return null;
    }

    private void removeFromFamily(UUID playerUuid, FamilyData family) {
        if (playerUuid.equals(family.getSpouse1())) {
            family.setSpouse1(null);
        } else if (playerUuid.equals(family.getSpouse2())) {
            family.setSpouse2(null);
        } else {
            family.getChildren().remove(playerUuid);
        }

        boolean noSpousesLeft = family.getSpouse1() == null && family.getSpouse2() == null;
        if (noSpousesLeft) {
            for (UUID memberUuid : new ArrayList<>(family.getChildren())) {
                CharacterData memberData = characterManager.getCharacter(memberUuid);
                if (memberData != null) {
                    memberData.setFamilyId(null);
                    characterManager.saveCharacter(memberData);
                }
            }
            familyManager.deleteFamily(family.getFamilyId());
        } else {
            UUID survivingSpouse = family.getSpouse1() != null ? family.getSpouse1() : family.getSpouse2();
            Player spousePlayer = Bukkit.getPlayer(survivingSpouse);
            if (spousePlayer != null) {
                spousePlayer.sendMessage("§cYour spouse has passed away.");
            }
            familyManager.saveFamily(family);
        }
    }

    private void spawnInheritanceChest(Player player, Location loc, UUID inheritorUuid) {
        Block block = loc.getBlock();
        block.setType(Material.CHEST);

        Block adjacent = block.getRelative(BlockFace.EAST);
        boolean placedDouble = adjacent.getType() == Material.AIR || adjacent.getType() == Material.CAVE_AIR;
        if (placedDouble) {
            adjacent.setType(Material.CHEST);
        }

        BlockState state = block.getState();
        if (state instanceof Chest chest) {
            Inventory chestInv = chest.getInventory();
            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && item.getType() != Material.AIR) chestInv.addItem(item);
            }
            for (ItemStack item : player.getInventory().getArmorContents()) {
                if (item != null && item.getType() != Material.AIR) chestInv.addItem(item);
            }
            ItemStack offhand = player.getInventory().getItemInOffHand();
            if (offhand.getType() != Material.AIR) chestInv.addItem(offhand);
        }

        player.getInventory().clear();

        chestProtectionManager.addProtectedChest(loc, inheritorUuid);
        if (placedDouble) {
            chestProtectionManager.addProtectedChest(adjacent.getLocation(), inheritorUuid);
        }

        player.sendMessage("§eAn inheritance chest has been placed at your chosen location for your inheritor.");
    }

    /** Every living character, for pickers and admin screens. */
    public List<CharacterData> getAllCharacters() {
        return characterManager.getAllCharacters();
    }
}
