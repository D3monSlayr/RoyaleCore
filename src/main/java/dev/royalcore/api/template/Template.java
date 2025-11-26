package dev.royalcore.api.template;

import dev.royalcore.api.consumer.*;
import dev.royalcore.api.enums.ScenarioPriority;
import dev.royalcore.api.scenario.Scenario;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public record Template(TemplateConsumer templateConsumer, ItemConsumer itemConsumer, ListenerConsumer listenerConsumer,
                       CommandConsumer commandConsumer, BorderConsumer borderConsumer,
                       SettingsConsumer settingsConsumer, SchedulerConsumer schedulerConsumer,
                       MessageConsumer messageConsumer, ScenarioPriority priority, List<Template> requiredTemplates,
                       List<Scenario> requiredScenarios, List<Template> templateConflicts,
                       List<Scenario> scenarioConflicts, PlayerConsumer playerConsumer) {

    public static TemplateBuilder template() {
        return new TemplateBuilder();
    }

    public static class TemplateBuilder {

        private final TemplateConsumer templateConsumer = new TemplateConsumer();
        private final ItemConsumer itemConsumer = new ItemConsumer();
        private final ListenerConsumer listenerConsumer = new ListenerConsumer();
        private final CommandConsumer commandConsumer = new CommandConsumer();
        private final BorderConsumer borderConsumer = new BorderConsumer();
        private final SettingsConsumer settingsConsumer = new SettingsConsumer();
        private final SchedulerConsumer schedulerConsumer = new SchedulerConsumer();
        private final MessageConsumer messageConsumer = new MessageConsumer();
        private final PlayerConsumer playerConsumer = new PlayerConsumer();

        private ScenarioPriority priority = ScenarioPriority.LOW;

        private List<Template> requiredTemplates = new ArrayList<>();
        private List<Scenario> requiredScenarios = new ArrayList<>();

        private List<Template> conflictingTemplates = new ArrayList<>();
        private List<Scenario> conflictingScenarios = new ArrayList<>();

        public TemplateBuilder() {
        }

        public TemplateBuilder templates(Consumer<TemplateConsumer> consumer) {
            consumer.accept(this.templateConsumer);
            return this;
        }

        public TemplateBuilder items(Consumer<ItemConsumer> consumer) {
            consumer.accept(itemConsumer);
            return this;
        }

        public TemplateBuilder listeners(Consumer<ListenerConsumer> consumer) {
            consumer.accept(listenerConsumer);
            return this;
        }

        public TemplateBuilder commands(Consumer<CommandConsumer> consumer) {
            consumer.accept(commandConsumer);
            return this;
        }

        public TemplateBuilder borders(Consumer<BorderConsumer> consumer) {
            consumer.accept(borderConsumer);
            return this;
        }

        public TemplateBuilder settings(Consumer<SettingsConsumer> consumer) {
            consumer.accept(settingsConsumer);
            return this;
        }

        public TemplateBuilder priority(ScenarioPriority priority) {
            this.priority = priority;
            return this;
        }

        public TemplateBuilder requires(Template... template) {
            this.requiredTemplates = Arrays.asList(template);
            return this;
        }

        public TemplateBuilder requires(Scenario... scenario) {
            this.requiredScenarios = Arrays.asList(scenario);
            return this;
        }

        public TemplateBuilder conflictsWith(Template... template) {
            this.conflictingTemplates = Arrays.asList(template);
            return this;
        }

        public TemplateBuilder conflictsWith(Scenario... scenario) {
            this.conflictingScenarios = Arrays.asList(scenario);
            return this;
        }

        public TemplateBuilder players(Consumer<PlayerConsumer> players) {
            players.accept(playerConsumer);
            return this;
        }

        public Template build() {
            return new Template(templateConsumer, itemConsumer, listenerConsumer, commandConsumer, borderConsumer, settingsConsumer, schedulerConsumer, messageConsumer, priority, requiredTemplates, requiredScenarios, conflictingTemplates, conflictingScenarios, playerConsumer);
        }

    }

}
