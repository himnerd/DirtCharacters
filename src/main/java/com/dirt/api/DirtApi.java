package com.dirt.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service locator shared by the Dirt plugin suite.
 *
 * <p>The five plugins — DirtCharacters, DirtNations, DirtBiz, DirtShops and
 * DirtLife — each run standalone on Vault alone. When more than one is
 * installed they find each other here: every plugin registers its own service
 * in {@code onEnable} and looks the others up lazily, so a missing plugin is
 * simply a null service rather than a crash.
 *
 * <p>These contract classes ship inside DirtCharacters, which is why the other
 * four declare it as a {@code softdepend}: install DirtCharacters to link the
 * suite together, omit it and each plugin still works on its own.
 *
 * <p>Never hold a service reference across reloads — always call the accessor.
 */
public final class DirtApi {

    private static volatile CharactersService characters;
    private static volatile NationsService nations;
    private static volatile BizService biz;
    private static volatile ShopsService shops;
    private static volatile LifeService life;

    private static final List<MenuExtension> MENU_EXTENSIONS = Collections.synchronizedList(new ArrayList<>());
    private static final List<TerritoryProvider> TERRITORY_PROVIDERS = Collections.synchronizedList(new ArrayList<>());
    private static final List<TravelDestinationProvider> TRAVEL_PROVIDERS = Collections.synchronizedList(new ArrayList<>());
    private static final List<CharacterAbilityProvider> CHARACTER_ABILITY_PROVIDERS = Collections.synchronizedList(new ArrayList<>());

    private DirtApi() {
    }

    // ──── Services ────

    public static CharactersService characters() {
        return characters;
    }

    public static NationsService nations() {
        return nations;
    }

    public static BizService biz() {
        return biz;
    }

    public static ShopsService shops() {
        return shops;
    }

    public static LifeService life() {
        return life;
    }

    public static boolean hasCharacters() {
        return characters != null;
    }

    public static boolean hasNations() {
        return nations != null;
    }

    public static boolean hasBiz() {
        return biz != null;
    }

    public static boolean hasShops() {
        return shops != null;
    }

    public static boolean hasLife() {
        return life != null;
    }

    // ──── Registration (called by each plugin's onEnable/onDisable) ────

    public static void registerCharacters(CharactersService service) {
        characters = service;
    }

    public static void registerNations(NationsService service) {
        nations = service;
    }

    public static void registerBiz(BizService service) {
        biz = service;
    }

    public static void registerShops(ShopsService service) {
        shops = service;
    }

    public static void registerLife(LifeService service) {
        life = service;
    }

    // ──── Menu extensions ────

    public static void registerMenuExtension(MenuExtension extension) {
        MENU_EXTENSIONS.add(extension);
    }

    public static void unregisterMenuExtensions(ClassLoader owner) {
        MENU_EXTENSIONS.removeIf(e -> e.getClass().getClassLoader() == owner);
    }

    /** Extensions for one hub screen, in registration order. */
    public static List<MenuExtension> getMenuExtensions(MenuExtension.Screen screen) {
        List<MenuExtension> result = new ArrayList<>();
        synchronized (MENU_EXTENSIONS) {
            for (MenuExtension e : MENU_EXTENSIONS) {
                if (e.getScreen() == screen) result.add(e);
            }
        }
        return result;
    }

    // ──── Territory providers ────

    public static void registerTerritoryProvider(TerritoryProvider provider) {
        TERRITORY_PROVIDERS.add(provider);
    }

    public static void unregisterTerritoryProviders(ClassLoader owner) {
        TERRITORY_PROVIDERS.removeIf(p -> p.getClass().getClassLoader() == owner);
    }

    /** The first claim covering the chunk, or null when nobody claims it. */
    public static TerritoryClaim claimAt(String chunkKey) {
        synchronized (TERRITORY_PROVIDERS) {
            for (TerritoryProvider provider : TERRITORY_PROVIDERS) {
                TerritoryClaim claim = provider.claimAt(chunkKey);
                if (claim != null) return claim;
            }
        }
        return null;
    }

    // ──── Fast-travel destination providers ────

    public static void registerTravelDestinationProvider(TravelDestinationProvider provider) {
        TRAVEL_PROVIDERS.add(provider);
    }

    public static void unregisterTravelDestinationProviders(ClassLoader owner) {
        TRAVEL_PROVIDERS.removeIf(p -> p.getClass().getClassLoader() == owner);
    }

    public static List<TravelDestinationProvider> getTravelDestinationProviders() {
        synchronized (TRAVEL_PROVIDERS) {
            return new ArrayList<>(TRAVEL_PROVIDERS);
        }
    }

    // ──── Character abilities ────

    /**
     * Registers an integration that grants or denies named character abilities.
     * Ability IDs must be lowercase namespaced keys such as {@code nerdianmc:magic}.
     */
    public static void registerCharacterAbilityProvider(CharacterAbilityProvider provider) {
        if (provider == null) throw new IllegalArgumentException("provider cannot be null");
        CHARACTER_ABILITY_PROVIDERS.add(provider);
    }

    public static void unregisterCharacterAbilityProviders(ClassLoader owner) {
        if (owner == null) return;
        CHARACTER_ABILITY_PROVIDERS.removeIf(provider -> provider.getClass().getClassLoader() == owner);
    }

    /**
     * Resolves a living character's ability. Providers are evaluated from highest
     * priority to lowest; the first non-abstaining result wins.
     */
    public static boolean hasCharacterAbility(java.util.UUID characterUuid, String abilityId) {
        if (characterUuid == null || !isValidAbilityId(abilityId)) return false;

        CharactersService charactersService = characters;
        if (charactersService == null) return false;

        CharacterView character = charactersService.getCharacter(characterUuid);
        if (character == null || !character.isAlive()) return false;

        List<CharacterAbilityProvider> providers;
        synchronized (CHARACTER_ABILITY_PROVIDERS) {
            providers = new ArrayList<>(CHARACTER_ABILITY_PROVIDERS);
        }
        providers.sort((first, second) -> Integer.compare(second.getPriority(), first.getPriority()));

        CharacterAbilityContext context = new CharacterAbilityContext(characterUuid, character);
        for (CharacterAbilityProvider provider : providers) {
            CharacterAbilityDecision decision;
            try {
                decision = provider.evaluateAbility(context, abilityId);
            } catch (RuntimeException ignored) {
                continue;
            }
            if (decision == CharacterAbilityDecision.GRANT) return true;
            if (decision == CharacterAbilityDecision.DENY) return false;
        }
        return false;
    }

    private static boolean isValidAbilityId(String abilityId) {
        return abilityId != null && abilityId.matches("[a-z0-9_.-]+:[a-z0-9/_.-]+");
    }

    /**
     * Clears everything a plugin registered. Call from {@code onDisable} so a
     * {@code /reload} does not leave stale services behind.
     */
    public static void unregisterAll(ClassLoader owner) {
        unregisterMenuExtensions(owner);
        unregisterTerritoryProviders(owner);
        unregisterTravelDestinationProviders(owner);
        unregisterCharacterAbilityProviders(owner);
        if (characters != null && characters.getClass().getClassLoader() == owner) characters = null;
        if (nations != null && nations.getClass().getClassLoader() == owner) nations = null;
        if (biz != null && biz.getClass().getClassLoader() == owner) biz = null;
        if (shops != null && shops.getClass().getClassLoader() == owner) shops = null;
        if (life != null && life.getClass().getClassLoader() == owner) life = null;
    }
}
