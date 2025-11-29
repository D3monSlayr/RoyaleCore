package dev.royalcore.api.br;

import dev.royalcore.Main;
import dev.royalcore.annotations.UnstableOnServerStart;
import dev.royalcore.api.consumer.SettingsConsumer;
import dev.royalcore.api.engine.BattleRoyaleEngine;
import dev.royalcore.api.enums.BattleRoyaleState;
import dev.royalcore.api.scenario.Scenario;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Represents a single Battle Royale game definition, including its unique ID,
 * scenarios, settings, lifecycle callbacks and current state.
 *
 * @param id               the unique identifier of this Battle Royale definition
 * @param scenarios        the list of scenarios that belong to this Battle Royale
 * @param settingsConsumer the settings associated with this Battle Royale
 * @param onStart          the callback to invoke when the Battle Royale is started
 * @param onStop           the callback to invoke when the Battle Royale is stopped
 * @param state            the current state of the Battle Royale
 */
public record BattleRoyale(
        UUID id,
        List<Scenario> scenarios,
        SettingsConsumer settingsConsumer,
        Runnable onStart,
        Runnable onStop,
        BattleRoyaleState state
) {

    /**
     * Creates a new BattleRoyale instance and registers it in the legacy {@link BattleRoyaleEngine}.
     *
     * @param id               the unique identifier of this Battle Royale definition
     * @param scenarios        the list of scenarios that belong to this Battle Royale
     * @param settingsConsumer the settings associated with this Battle Royale
     * @param onStart          the callback to invoke when the Battle Royale is started
     * @param onStop           the callback to invoke when the Battle Royale is stopped
     * @param state            the initial state of the Battle Royale
     */
    public BattleRoyale(
            UUID id,
            List<Scenario> scenarios,
            SettingsConsumer settingsConsumer,
            Runnable onStart,
            Runnable onStop,
            BattleRoyaleState state
    ) {
        this.id = id;
        this.scenarios = scenarios;
        this.settingsConsumer = settingsConsumer;
        this.onStart = onStart;
        this.onStop = onStop;
        this.state = state;

        BattleRoyaleEngine.getBattleRoyaleEngine().register(this);
    }

    /**
     * Creates a new {@link BattleRoyaleBuilder} for the given ID.
     *
     * @param uuid the unique ID to use for the new Battle Royale
     * @return a builder instance configured with the given ID
     */
    public static BattleRoyaleBuilder battleroyale(UUID uuid) {
        return new BattleRoyaleBuilder(uuid);
    }

    /**
     * Starts this Battle Royale by scheduling its {@code onStart} callback on the main thread.
     */
    public void start() {
        Bukkit.getScheduler().runTask(Main.getPlugin(), onStart);
    }

    /**
     * Stops this Battle Royale by scheduling its {@code onStop} callback on the main thread.
     */
    public void stop() {
        Bukkit.getScheduler().runTask(Main.getPlugin(), onStop);
    }

    /**
     * Exposes the configured {@code onStart} callback to external consumers.
     *
     * @param onStart a consumer that receives the current {@code onStart} runnable
     */
    @UnstableOnServerStart
    public void onStart(Consumer<Runnable> onStart) {
        onStart.accept(this.onStart);
    }

    /**
     * Returns a list of scenario IDs for persistence.
     *
     * @return a list of scenario identifiers associated with this Battle Royale
     */
    public List<String> scenarioIdsForDatabase() {
        List<String> names = new ArrayList<>();
        for (Scenario scenario : scenarios) {
            names.add(scenario.name().toString());
        }
        return names;
    }

    /**
     * Returns the current state name for persistence.
     *
     * @return the {@link BattleRoyaleState#name()} of this Battle Royale
     */
    public String stateForDatabase() {
        return state.name();
    }

    /**
     * Returns the settings object for persistence.
     *
     * @return the settings associated with this Battle Royale
     */
    public SettingsConsumer settingsForDatabase() {
        return settingsConsumer;
    }

    /**
     * Builder for {@link BattleRoyale} instances, providing a fluent API
     * to configure scenarios, settings, and lifecycle callbacks.
     */
    public static class BattleRoyaleBuilder {

        private final UUID id;
        private final List<Scenario> scenarios = new ArrayList<>();
        private final SettingsConsumer settings = new SettingsConsumer();
        private BattleRoyaleState state = BattleRoyaleState.NOT_STARTED;

        private Runnable onStart = () -> {
            state = BattleRoyaleState.WAITING;
        };
        private Runnable onStop = () -> {
            state = BattleRoyaleState.ENDED;
        };

        /**
         * Creates a builder for a BattleRoyale with the given ID.
         *
         * @param id the ID to assign to the Battle Royale
         */
        public BattleRoyaleBuilder(UUID id) {
            this.id = id;
        }

        /**
         * Adds scenarios to this Battle Royale definition.
         *
         * @param scenarios one or more scenarios to include
         * @return this builder instance for chaining
         */
        public BattleRoyaleBuilder withScenarios(Scenario... scenarios) {
            this.scenarios.addAll(Arrays.asList(scenarios));
            return this;
        }

        /**
         * Configures settings for this Battle Royale via the provided consumer.
         *
         * @param settings a consumer that mutates the internal {@link SettingsConsumer}
         * @return this builder instance for chaining
         */
        public BattleRoyaleBuilder withSettings(Consumer<SettingsConsumer> settings) {
            settings.accept(this.settings);
            return this;
        }

        /**
         * Sets the {@code onStart} callback that will be invoked when the Battle Royale starts.
         *
         * @param runnable the start callback
         * @return this builder instance for chaining
         */
        public BattleRoyaleBuilder onStart(Runnable runnable) {
            this.onStart = runnable;
            return this;
        }

        /**
         * Sets the {@code onStop} callback that will be invoked when the Battle Royale stops.
         *
         * @param runnable the stop callback
         * @return this builder instance for chaining
         */
        public BattleRoyaleBuilder onStop(Runnable runnable) {
            this.onStop = runnable;
            return this;
        }

        /**
         * Allows external code to access and modify the currently configured
         * {@code onStart} callback.
         *
         * @param runnable a consumer that receives the current start callback
         * @return this builder instance for chaining
         */
        public BattleRoyaleBuilder onStart(Consumer<Runnable> runnable) {
            runnable.accept(onStart);
            return this;
        }

        /**
         * Allows external code to access and modify the currently configured
         * {@code onStop} callback.
         *
         * @param runnable a consumer that receives the current stop callback
         * @return this builder instance for chaining
         */
        public BattleRoyaleBuilder onStop(Consumer<Runnable> runnable) {
            runnable.accept(onStop);
            return this;
        }

        /**
         * Builds a {@link BattleRoyale} instance with the current builder state.
         *
         * @return a new BattleRoyale instance
         */
        public BattleRoyale build() {
            if (id == null) {
                Main.getPlugin().getComponentLogger().warn(
                        Component.text("The ID of a Battle Royale is null! Defaulted to a random ID"),
                        new IllegalStateException()
                );
                return new BattleRoyale(UUID.randomUUID(), scenarios, settings, onStart, onStop, state);
            }

            return new BattleRoyale(id, scenarios, settings, onStart, onStop, state);
        }

    }

}
