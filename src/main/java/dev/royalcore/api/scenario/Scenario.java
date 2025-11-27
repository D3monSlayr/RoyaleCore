package dev.royalcore.api.scenario;

import dev.royalcore.Main;
import dev.royalcore.api.consumer.*;
import dev.royalcore.api.enums.ScenarioPriority;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public record Scenario(Component name, ItemConsumer itemConsumer,
                       ListenerConsumer listenerConsumer, CommandConsumer commandConsumer,
                       BorderConsumer borderConsumer, SettingsConsumer settingsConsumer,
                       SchedulerConsumer schedulerConsumer, MessageConsumer messageConsumer, ScenarioPriority priority,
                       List<Scenario> scenarioConflicts,
                       List<Scenario> requiredScenarios,
                       PlayerConsumer playerConsumer) {

    public static ScenarioBuilder scenario(Component name) {
        return new ScenarioBuilder(name);
    }

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

        public ScenarioBuilder(Component sname) {
            name = sname;
        }

        public ScenarioBuilder items(Consumer<ItemConsumer> consumer) {
            consumer.accept(itemConsumer);
            return this;
        }

        public ScenarioBuilder listeners(Consumer<ListenerConsumer> consumer) {
            consumer.accept(listenerConsumer);
            return this;
        }

        public ScenarioBuilder commands(Consumer<CommandConsumer> consumer) {
            consumer.accept(commandConsumer);
            return this;
        }

        public ScenarioBuilder borders(Consumer<BorderConsumer> consumer) {
            consumer.accept(borderConsumer);
            return this;
        }

        public ScenarioBuilder settings(Consumer<SettingsConsumer> consumer) {
            consumer.accept(settingsConsumer);
            return this;
        }

        public ScenarioBuilder priority(ScenarioPriority priority) {
            this.priority = priority;
            return this;
        }

        public ScenarioBuilder requires(Scenario... scenario) {
            this.requiredScenarios = Arrays.asList(scenario);
            return this;
        }

        public ScenarioBuilder conflictsWith(Scenario... scenario) {
            this.conflictingScenarios = Arrays.asList(scenario);
            return this;
        }

        public ScenarioBuilder players(Consumer<PlayerConsumer> players) {
            players.accept(playerConsumer);
            return this;
        }

        public Scenario build() {
            if (name == null) {
                Main.getPlugin().getComponentLogger().error(Component.text("The name of a scenario cannot be null or empty!"), new IllegalStateException());
                return new Scenario(Component.text("Unknown (not set!)").color(NamedTextColor.DARK_RED), itemConsumer, listenerConsumer, commandConsumer, borderConsumer, settingsConsumer, schedulerConsumer, messageConsumer, priority, requiredScenarios, conflictingScenarios, playerConsumer);
            }

            return new Scenario(name, itemConsumer, listenerConsumer, commandConsumer, borderConsumer, settingsConsumer, schedulerConsumer, messageConsumer, priority, requiredScenarios, conflictingScenarios, playerConsumer);

        }

    }

}
