package com.dirt.characters;

import org.bukkit.configuration.file.FileConfiguration;

public class Settings {

    private final DirtCharacters plugin;

    // ──── Character ────
    private double nameChangeCost;
    private long deathRespawnDelayTicks;
    private long joinDelayTicks;
    private double dailyLoginReward;
    private boolean characterBiosEnabled;
    private int characterBioMaxLength;

    // ──── Family ────
    private boolean familyEnabled;
    private double marriageCost;
    private double divorcePayout;
    private int marriageEligibilityDays;

    // ──── DirtHands ────
    private boolean dirtHandsEnabled;

    // ──── Contracts ────
    private boolean contractsEnabled;
    private double paperCopyCost;

    // ──── Fast travel ────
    private boolean fastTravelEnabled;

    // ──── Inheritance ────
    private boolean inheritanceChestEnabled;

    public Settings(DirtCharacters plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        plugin.reloadConfig();
        FileConfiguration cfg = plugin.getConfig();

        nameChangeCost = cfg.getDouble("character.name-change-cost", 100_000.0);
        deathRespawnDelayTicks = cfg.getLong("character.death-respawn-delay-ticks", 10L);
        joinDelayTicks = cfg.getLong("character.join-delay-ticks", 20L);
        dailyLoginReward = cfg.getDouble("character.daily-login-reward", 500.0);
        characterBiosEnabled = cfg.getBoolean("character.bios.enabled", true);
        characterBioMaxLength = Math.max(1, cfg.getInt("character.bios.max-length", 500));

        familyEnabled = cfg.getBoolean("family.enabled", true);
        marriageCost = cfg.getDouble("family.marriage-cost", 1_000.0);
        divorcePayout = cfg.getDouble("family.divorce-payout", 1_000.0);
        marriageEligibilityDays = cfg.getInt("family.marriage-eligibility-days", 30);

        dirtHandsEnabled = cfg.getBoolean("dirthands.enabled", true);

        contractsEnabled = cfg.getBoolean("contracts.enabled", true);
        paperCopyCost = cfg.getDouble("contracts.paper-copy-cost", 500.0);

        fastTravelEnabled = cfg.getBoolean("fast-travel.enabled", true);

        inheritanceChestEnabled = cfg.getBoolean("inheritance.chest-enabled", true);
    }

    public double getNameChangeCost() { return nameChangeCost; }
    public long getDeathRespawnDelayTicks() { return deathRespawnDelayTicks; }
    public long getJoinDelayTicks() { return joinDelayTicks; }
    public double getDailyLoginReward() { return dailyLoginReward; }
    public boolean isCharacterBiosEnabled() { return characterBiosEnabled; }
    public int getCharacterBioMaxLength() { return characterBioMaxLength; }

    public boolean isFamilyEnabled() { return familyEnabled; }
    public double getMarriageCost() { return marriageCost; }
    public double getDivorcePayout() { return divorcePayout; }
    public int getMarriageEligibilityDays() { return marriageEligibilityDays; }
    public long getMarriageEligibilityMs() { return (long) marriageEligibilityDays * 24L * 60L * 60L * 1000L; }

    public boolean isDirtHandsEnabled() { return dirtHandsEnabled; }

    public boolean isContractsEnabled() { return contractsEnabled; }
    public double getPaperCopyCost() { return paperCopyCost; }

    public boolean isFastTravelEnabled() { return fastTravelEnabled; }

    public boolean isInheritanceChestEnabled() { return inheritanceChestEnabled; }
}
