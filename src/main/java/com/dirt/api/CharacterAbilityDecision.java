package com.dirt.api;

/** A provider's result when evaluating a character ability. */
public enum CharacterAbilityDecision {
    /** This provider does not manage the requested ability. */
    ABSTAIN,
    /** This provider grants the requested ability. */
    GRANT,
    /** This provider explicitly denies the requested ability. */
    DENY
}