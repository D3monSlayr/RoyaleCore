package dev.royalcore.api.persistence;

import java.util.List;

/**
 * Container for data-transfer types used to persist and restore
 * BattleRoyale and Scenario state from the database.
 */
public class DataTypes {

    /**
     * Serializable representation of global or per-entity settings.
     *
     * @param lifesteal         whether lifesteal is enabled
     * @param graceSeconds      grace period length in seconds
     * @param lateJoinBehaviour the late-join handling mode (enum name)
     */
    public record SettingsData(
            boolean lifesteal,
            long graceSeconds,
            String lateJoinBehaviour
    ) {
    }

    /**
     * Serializable representation of scenario-related messages.
     *
     * @param deathMessage the death/elimination message as plain text
     * @param joinMessage  the join message as plain text
     * @param leaveMessage the leave message as plain text
     */
    public record MessageData(
            String deathMessage,
            String joinMessage,
            String leaveMessage
    ) {
    }

    /**
     * Serializable representation of a {@code Scenario}.
     *
     * @param name      the scenario name
     * @param priority  the scenario priority (enum name)
     * @param settings  the scenario-specific settings
     * @param messages  the scenario-specific messages
     * @param requires  names of scenarios this one depends on
     * @param conflicts names of scenarios this one conflicts with
     */
    public record ScenarioData(
            String name,
            String priority,
            SettingsData settings,
            MessageData messages,
            List<String> requires,
            List<String> conflicts
    ) {
    }

    /**
     * Serializable representation of a {@code BattleRoyale}.
     *
     * @param id        the battle royale ID as a stringified UUID
     * @param state     the current state (enum name)
     * @param settings  the battle royale settings
     * @param scenarios names of scenarios included in the battle royale
     */
    public record BattleRoyaleData(
            String id,
            String state,
            SettingsData settings,
            List<String> scenarios
    ) {
    }

}
