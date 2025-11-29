package dev.royalcore.api.scenario;

import dev.royalcore.Main;
import dev.royalcore.annotations.UnstableOnServerStart;
import dev.royalcore.api.consumer.*;
import dev.royalcore.api.enums.ScenarioPriority;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Represents a single scenario definition for a Battle Royale game.
 * <p>
 * A scenario aggregates item, listener, command, border, settings, scheduler,
 * message and player behaviour, along with dependencies and lifecycle callbacks.
 *
 * @param name              the display name of the scenario
 * @param itemConsumer      item configuration and custom items used by this scenario
 * @param listenerConsumer  event listeners associated with this scenario
 * @param commandConsumer   commands exposed by this scenario
 * @param borderConsumer    world border configuration for this scenario
 * @param settingsConsumer  configurable settings for this scenario
 * @param schedulerConsumer scheduled tasks associated with this scenario
 * @param messageConsumer   messages used by this scenario
 * @param priority          priority of this scenario when resolving order
 * @param scenarioConflicts scenarios that cannot be enabled together with this one
 * @param requiredScenarios scenarios that must also be enabled for this one to be valid
 * @param playerConsumer    per-player behaviour applied when the scenario starts
 * @param onStart           callback invoked when the scenario starts
 * @param onStop            callback invoked when the scenario stops
 */
public record Scenario(
        Component name,
        ItemConsumer itemConsumer,
        ListenerConsumer listenerConsumer,
        CommandConsumer commandConsumer,
        BorderConsumer borderConsumer,
        SettingsConsumer settingsConsumer,
        SchedulerConsumer schedulerConsumer,
        MessageConsumer messageConsumer,
        ScenarioPriority priority,
        List<Scenario> scenarioConflicts,
        List<Scenario> requiredScenarios,
        PlayerConsumer playerConsumer,
        Runnable onStart,
        Runnable onStop
) {

    /**
     * Creates a new {@link ScenarioBuilder} for the given name.
     *
     * @param name the display name of the scenario
     * @return a builder instance for fluent configuration
     */
    public static ScenarioBuilder scenario(Component name) {
        return new ScenarioBuilder(name);
    }

    /**
     * Exposes the scenario start callback to external consumers.
     *
     * @param consumer a consumer receiving the current start runnable
     */
    @UnstableOnServerStart
    public void onStart(Consumer<Runnable> consumer) {
        consumer.accept(onStart);
    }

    /**
     * Exposes the scenario stop callback to external consumers.
     *
     * @param consumer a consumer receiving the current stop runnable
     */
    @UnstableOnServerStart
    public void onStop(Consumer<Runnable> consumer) {
        consumer.accept(onStop);
    }

    /**
     * Builder for {@link Scenario} instances, providing a fluent configuration API.
     */
    public static class ScenarioBuilder {
        private final Component name;

        private final ItemConsumer itemConsumer = new ItemConsumer();
        private final ListenerConsumer listenerConsumer = new ListenerConsumer();
        private final CommandConsumer commandConsumer = new CommandConsumer();
        private final BorderConsumer borderConsumer = new BorderConsumer();
        private final SettingsConsumer settingsConsumer = new SettingsConsumer();
        private final SchedulerConsumer schedulerConsumer = new SchedulerConsumer();
        private final MessageConsumer messageConsumer = new MessageConsumer();
        private final PlayerConsumer playerConsumer = new PlayerConsumer();

        private ScenarioPriority priority = ScenarioPriority.LOW;

        private List<Scenario> requiredScenarios = new ArrayList<>();
        private List<Scenario> conflictingScenarios = new ArrayList<>();

        private Runnable onStart = () -> {
        };
        private Runnable onStop = () -> {
        };

        /**
         * Creates a new builder for a scenario with the given name.
         *
         * @param sname the display name of the scenario
         */
        public ScenarioBuilder(Component sname) {
            name = sname;
        }

        /**
         * Configures scenario items using the provided consumer.
         *
         * @param consumer a consumer that receives the internal {@link ItemConsumer}
         * @return this builder for chaining
         */
        public ScenarioBuilder items(Consumer<ItemConsumer> consumer) {
            consumer.accept(itemConsumer);
            return this;
        }

        /**
         * Allows modification of the current start callback via a consumer.
         *
         * @param onStart a consumer that receives the current start runnable
         * @return this builder for chaining
         */
        public ScenarioBuilder onStart(Consumer<Runnable> onStart) {
            onStart.accept(this.onStart);
            return this;
        }

        /**
         * Sets the start callback for this scenario.
         *
         * @param runnable the runnable to invoke when the scenario starts
         * @return this builder for chaining
         */
        public ScenarioBuilder onStart(Runnable runnable) {
            this.onStart = runnable;
            return this;
        }

        /**
         * Allows modification of the current stop callback via a consumer.
         *
         * @param onStop a consumer that receives the current stop runnable
         * @return this builder for chaining
         */
        public ScenarioBuilder onStop(Consumer<Runnable> onStop) {
            onStop.accept(this.onStop);
            return this;
        }

        /**
         * Sets the stop callback for this scenario.
         *
         * @param runnable the runnable to invoke when the scenario stops
         * @return this builder for chaining
         */
        public ScenarioBuilder onStop(Runnable runnable) {
            this.onStop = runnable;
            return this;
        }

        /**
         * Configures scenario listeners using the provided consumer.
         *
         * @param consumer a consumer that receives the internal {@link ListenerConsumer}
         * @return this builder for chaining
         */
        public ScenarioBuilder listeners(Consumer<ListenerConsumer> consumer) {
            consumer.accept(listenerConsumer);
            return this;
        }

        /**
         * Configures scenario commands using the provided consumer.
         *
         * @param consumer a consumer that receives the internal {@link CommandConsumer}
         * @return this builder for chaining
         */
        public ScenarioBuilder commands(Consumer<CommandConsumer> consumer) {
            consumer.accept(commandConsumer);
            return this;
        }

        /**
         * Configures world borders for this scenario.
         *
         * @param consumer a consumer that receives the internal {@link BorderConsumer}
         * @return this builder for chaining
         */
        public ScenarioBuilder borders(Consumer<BorderConsumer> consumer) {
            consumer.accept(borderConsumer);
            return this;
        }

        /**
         * Configures scenario settings using the provided consumer.
         *
         * @param consumer a consumer that receives the internal {@link SettingsConsumer}
         * @return this builder for chaining
         */
        public ScenarioBuilder settings(Consumer<SettingsConsumer> consumer) {
            consumer.accept(settingsConsumer);
            return this;
        }

        /**
         * Sets the priority of this scenario.
         *
         * @param priority the scenario priority
         * @return this builder for chaining
         */
        public ScenarioBuilder priority(ScenarioPriority priority) {
            this.priority = priority;
            return this;
        }

        /**
         * Declares scenarios that must also be enabled for this scenario to be valid.
         *
         * @param scenario one or more required scenarios
         * @return this builder for chaining
         */
        public ScenarioBuilder requires(Scenario... scenario) {
            this.requiredScenarios = Arrays.asList(scenario);
            return this;
        }

        /**
         * Declares scenarios that conflict with this scenario.
         *
         * @param scenario one or more conflicting scenarios
         * @return this builder for chaining
         */
        public ScenarioBuilder conflictsWith(Scenario... scenario) {
            this.conflictingScenarios = Arrays.asList(scenario);
            return this;
        }

        /**
         * Configures per-player behaviour for this scenario.
         *
         * @param players a consumer that receives the internal {@link PlayerConsumer}
         * @return this builder for chaining
         */
        public ScenarioBuilder players(Consumer<PlayerConsumer> players) {
            players.accept(playerConsumer);
            return this;
        }

        /**
         * Builds the {@link Scenario} instance from the accumulated configuration.
         * <p>
         * If no name is provided, a placeholder scenario is created and an error is logged.
         *
         * @return a new {@link Scenario} instance
         */
        public Scenario build() {
            if (name == null) {
                Main.getPlugin().getComponentLogger().error(
                        Component.text("The name of a scenario cannot be null or empty!"),
                        new IllegalStateException()
                );
                return new Scenario(
                        Component.text("Unknown (not set!)").color(NamedTextColor.DARK_RED),
                        itemConsumer,
                        listenerConsumer,
                        commandConsumer,
                        borderConsumer,
                        settingsConsumer,
                        schedulerConsumer,
                        messageConsumer,
                        priority,
                        requiredScenarios,
                        conflictingScenarios,
                        playerConsumer,
                        onStart,
                        onStop
                );
            }

            return new Scenario(
                    name,
                    itemConsumer,
                    listenerConsumer,
                    commandConsumer,
                    borderConsumer,
                    settingsConsumer,
                    schedulerConsumer,
                    messageConsumer,
                    priority,
                    requiredScenarios,
                    conflictingScenarios,
                    playerConsumer,
                    onStart,
                    onStop
            );
        }

    }

}
