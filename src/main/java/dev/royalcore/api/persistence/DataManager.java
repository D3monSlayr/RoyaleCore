package dev.royalcore.api.persistence;

import dev.royalcore.Main;
import dev.royalcore.api.br.BattleRoyale;
import dev.royalcore.api.consumer.MessageConsumer;
import dev.royalcore.api.consumer.SettingsConsumer;
import dev.royalcore.api.db.Database;
import dev.royalcore.api.enums.BattleRoyaleState;
import dev.royalcore.api.enums.ScenarioPriority;
import dev.royalcore.api.scenario.Scenario;
import net.kyori.adventure.text.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Handles persistence of BattleRoyale and Scenario data to and from the database.
 * <p>
 * Uses data transfer records defined in {@link DataTypes} and the {@link Database}
 * abstraction to serialize and reconstruct domain objects at runtime. [web:218]
 */
public class DataManager {

    /**
     * Converts a {@link SettingsConsumer} into a serializable {@link DataTypes.SettingsData}.
     *
     * @param sc the settings consumer holding in-memory settings
     * @return a {@link DataTypes.SettingsData} representation of the settings
     */
    private DataTypes.SettingsData toSettingsData(SettingsConsumer sc) {
        boolean lifesteal = (boolean) sc.getSetting(SettingsConsumer.Setting.LIFESTEAL);
        Duration grace = (Duration) sc.getSetting(SettingsConsumer.Setting.GRACE);
        var lj = (SettingsConsumer.LateJoinHandling) sc.getSetting(SettingsConsumer.Setting.LATE_JOIN_BEHAVIOUR);

        return new DataTypes.SettingsData(
                lifesteal,
                grace.getSeconds(),
                lj.name()
        );
    }

    /**
     * Applies values from {@link DataTypes.SettingsData} to a {@link SettingsConsumer}.
     *
     * @param sc   the settings consumer to mutate
     * @param data the serialized settings data to apply
     */
    private void applySettings(SettingsConsumer sc, DataTypes.SettingsData data) {
        sc.lifesteal(data.lifesteal());
        sc.grace(Duration.ofSeconds(data.graceSeconds()));
        sc.lateJoinBehaviour(SettingsConsumer.LateJoinHandling.valueOf(data.lateJoinBehaviour()));
    }

    /**
     * Converts a {@link MessageConsumer} into a serializable {@link DataTypes.MessageData}.
     *
     * @param mc the message consumer holding in-memory messages
     * @return a {@link DataTypes.MessageData} representation of the messages
     */
    private DataTypes.MessageData toMessageData(MessageConsumer mc) {
        return new DataTypes.MessageData(
                mc.getDeathMessage().toString(),
                mc.getJoinMessage().toString(),
                mc.getLeaveMessage().toString()
        );
    }

    /**
     * Converts a {@link Scenario} into a serializable {@link DataTypes.ScenarioData}.
     *
     * @param s the scenario to convert
     * @return a {@link DataTypes.ScenarioData} representation of the scenario
     */
    private DataTypes.ScenarioData toScenarioData(Scenario s) {
        DataTypes.SettingsData settingsData = toSettingsData(s.settingsConsumer());
        DataTypes.MessageData messageData = toMessageData(s.messageConsumer());
        List<String> requires = s.requiredScenarios().stream()
                .map(sc -> sc.name().toString())
                .toList();
        List<String> conflicts = s.scenarioConflicts().stream()
                .map(sc -> sc.name().toString())
                .toList();

        return new DataTypes.ScenarioData(
                s.name().toString(),
                s.priority().name(),
                settingsData,
                messageData,
                requires,
                conflicts
        );
    }

    /**
     * Converts a {@link BattleRoyale} into a serializable {@link DataTypes.BattleRoyaleData}.
     *
     * @param royale the battle royale instance to convert
     * @return a {@link DataTypes.BattleRoyaleData} representation of the battle royale
     */
    private DataTypes.BattleRoyaleData toBattleRoyaleData(BattleRoyale royale) {
        DataTypes.SettingsData settingsData = toSettingsData(royale.settingsConsumer());
        List<String> scenarioNames = royale.scenarios().stream()
                .map(sc -> sc.name().toString())
                .toList();

        return new DataTypes.BattleRoyaleData(
                royale.id().toString(),
                royale.state().name(),
                settingsData,
                scenarioNames
        );
    }

    /**
     * Saves a single {@link Scenario} definition to the database.
     * <p>
     * Uses the {@code scenarios} table, with the scenario name as key
     * and {@link DataTypes.ScenarioData} as value.
     *
     * @param database the database instance to write to
     * @param scenario the scenario to persist
     */
    public void saveScenario(Database database, Scenario scenario) {
        Database.DatabaseSession session = database.use("scenarios");
        DataTypes.ScenarioData data = toScenarioData(scenario);

        session.ensureExists()
                .thenCompose(v -> session.write(scenario.name().toString(), data))
                .exceptionally(ex -> {
                    Main.getPlugin().getComponentLogger().error(
                            Component.text("Failed to save scenario '" + scenario.name() + "' to the database!"),
                            ex
                    );
                    return null;
                });
    }

    /**
     * Saves a {@link BattleRoyale} definition and all of its scenarios to the database.
     * <p>
     * Scenarios are stored in the {@code scenarios} table, while the battle royale
     * itself is stored in the {@code battles} table using its ID as key.
     *
     * @param database the database instance to write to
     * @param royale   the battle royale to persist
     */
    public void saveBattleRoyale(Database database, BattleRoyale royale) {
        for (Scenario scenario : royale.scenarios()) {
            saveScenario(database, scenario);
        }

        Database.DatabaseSession battle = database.use("battles");
        DataTypes.BattleRoyaleData data = toBattleRoyaleData(royale);

        battle.ensureExists()
                .thenCompose(v -> battle.write(royale.id().toString(), data))
                .exceptionally(ex -> {
                    Main.getPlugin().getComponentLogger().error(
                            Component.text("Failed to save battle royale '" + royale.id() + "'!"),
                            ex
                    );
                    return null;
                });
    }

    /**
     * Loads all stored {@link Scenario} definitions from the database and reconstructs them.
     * <p>
     * Returns a map from scenario name to the reconstructed {@link Scenario} instance.
     *
     * @param database the database instance to read from
     * @return a future completing with a map of scenario names to scenarios
     */
    public CompletableFuture<Map<String, Scenario>> loadScenarios(Database database) {
        Database.DatabaseSession session = database.use("scenarios");

        return session.ensureExists()
                .thenCompose(v -> session.readAll(DataTypes.ScenarioData.class))
                .thenApply(map -> {
                    Map<String, Scenario> result = new HashMap<>();

                    map.forEach((key, data) -> {
                        SettingsConsumer settings = new SettingsConsumer();
                        applySettings(settings, data.settings());

                        MessageConsumer messages = new MessageConsumer();
                        messages.deathMessage(Component.text(data.messages().deathMessage()));
                        messages.joinMessage(Component.text(data.messages().joinMessage()));
                        messages.leaveMessage(Component.text(data.messages().leaveMessage()));

                        Scenario.ScenarioBuilder builder = Scenario.scenario(Component.text(data.name()))
                                .settings(sc -> applySettings(sc, data.settings()))
                                .priority(ScenarioPriority.valueOf(data.priority()));
                        // TODO: items/listeners/commands/borders/scheduler/player: attach in code or extend DTO later

                        Scenario scenario = builder.build();
                        result.put(data.name(), scenario);
                    });

                    map.forEach((key, data) -> {
                        Scenario scenario = result.get(data.name());
                        if (scenario == null) return;

                        List<Scenario> requires = data.requires().stream()
                                .map(result::get)
                                .toList();
                        List<Scenario> conflicts = data.conflicts().stream()
                                .map(result::get)
                                .toList();

                        // TODO: wire requires/conflicts into rebuilt Scenario if needed
                    });

                    return result;
                });
    }

    /**
     * Loads all stored {@link BattleRoyale} definitions from the database and reconstructs them.
     * <p>
     * Uses the provided {@code scenariosByName} map to resolve scenario references
     * when creating {@link BattleRoyale} instances.
     *
     * @param database        the database instance to read from
     * @param scenariosByName a map of scenario names to preloaded {@link Scenario} instances
     * @return a future that completes when all battle royales have been reconstructed
     */
    public CompletableFuture<Void> loadBattleRoyales(Database database, Map<String, Scenario> scenariosByName) {
        Database.DatabaseSession session = database.use("battles");

        return session.ensureExists()
                .thenCompose(v -> session.readAll(DataTypes.BattleRoyaleData.class))
                .thenAccept(map -> {
                    map.forEach((key, data) -> {
                        List<Scenario> scenarios = data.scenarios().stream()
                                .map(scenariosByName::get)
                                .toList();

                        SettingsConsumer settings = new SettingsConsumer();
                        applySettings(settings, data.settings());

                        Runnable onStart = () -> {
                            //TODO: attach per-battle logic here, or delegate to BattleRoyaleEngine
                        };
                        Runnable onStop = () -> {
                            //TODo: attach stop logic
                        };

                        new BattleRoyale(
                                UUID.fromString(data.id()),
                                scenarios,
                                settings,
                                onStart,
                                onStop,
                                BattleRoyaleState.valueOf(data.state())
                        );
                    });
                });
    }

}
