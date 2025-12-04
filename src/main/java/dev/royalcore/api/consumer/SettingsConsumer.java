package dev.royalcore.api.consumer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Consumer for scenario or battle settings, backed by a simple key-value map.
 */
public class SettingsConsumer {

    private final Map<Setting, Object> settings = new HashMap<>();

    /**
     * Creates a new settings consumer with default values.
     */
    public SettingsConsumer() {
        settings.put(Setting.LIFESTEAL, true);
        settings.put(Setting.GRACE, Duration.ofMinutes(20));
        settings.put(Setting.LATE_JOIN_BEHAVIOUR, LateJoinHandling.ALLOW_PARTICIPATION);
        settings.put(Setting.MAX_LIFESTEAL_HEARTS, 40);
    }

    /**
     * Enables or disables lifesteal.
     *
     * @param bool whether lifesteal is enabled
     */
    public void lifesteal(boolean bool) {
        settings.replace(Setting.LIFESTEAL, bool);
    }

    public void maxLifestealHearts(double max) {
        settings.replace(Setting.MAX_LIFESTEAL_HEARTS, max);
    }

    /**
     * Sets the grace period duration.
     *
     * @param duration the grace duration
     */
    public void grace(Duration duration) {
        settings.replace(Setting.GRACE, duration);
    }

    /**
     * Sets how late-joining players are handled.
     *
     * @param handling the late join handling strategy
     */
    public void lateJoinBehaviour(LateJoinHandling handling) {
        settings.replace(Setting.LATE_JOIN_BEHAVIOUR, handling);
    }

    /**
     * Returns the value of a setting, or {@code null} if not present.
     *
     * @param setting the setting key
     * @return the stored value, or {@code null} if absent
     */
    public Object getSetting(Setting setting) {
        return settings.getOrDefault(setting, null);
    }

    /**
     * Well-known settings for scenarios and battles.
     */
    public enum Setting {
        /**
         * Whether lifesteal mechanics are enabled.
         */
        LIFESTEAL,

        MAX_LIFESTEAL_HEARTS,
        /**
         * Grace period duration.
         */
        GRACE,
        /**
         * Behaviour for late-joining players.
         */
        LATE_JOIN_BEHAVIOUR,

        // REVIVAL_HEART - later...
        // TEAMS - later...
    }

    /**
     * Strategies for handling players who join after the game has started.
     */
    public enum LateJoinHandling {
        /**
         * Spawn the player with spectator mode.
         */
        SPAWN_WITH_SPECTATOR,
        /**
         * Allow full participation regardless of timing.
         */
        ALLOW_PARTICIPATION,
        /**
         * Allow participation with balancing considerations.
         */
        ALLOW_BALANCED_PARTICIPATION,
        /**
         * Ban late-joining players from the game.
         */
        BAN
    }

}
