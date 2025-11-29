package dev.royalcore.api.enums;

/**
 * Represents the lifecycle state of a Battle Royale session.
 */
public enum BattleRoyaleState {

    /**
     * The battle royale has not been initialized or started yet.
     */
    NOT_STARTED,

    /**
     * The battle royale is waiting for players or other pre-game conditions.
     */
    WAITING,

    /**
     * The battle royale is currently running.
     */
    IN_GAME,

    /**
     * The battle royale has finished.
     */
    ENDED

}
