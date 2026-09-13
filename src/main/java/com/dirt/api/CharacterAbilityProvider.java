package com.dirt.api;

/**
 * Supplies character-specific abilities for an external integration.
 * Providers own their persistence and may return {@link CharacterAbilityDecision#ABSTAIN}
 * for abilities outside their domain.
 */
@FunctionalInterface
public interface CharacterAbilityProvider {

    CharacterAbilityDecision evaluateAbility(CharacterAbilityContext context, String abilityId);

    /** Higher-priority providers resolve first. Equal priorities retain registration order. */
    default int getPriority() {
        return 0;
    }
}