package dev.royalcore.api.br;

import dev.royalcore.Main;
import dev.royalcore.api.consumer.SettingsConsumer;
import dev.royalcore.api.engine.BattleRoyaleEngine;
import dev.royalcore.api.scenario.Scenario;
import dev.royalcore.api.template.Template;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public record BattleRoyale(UUID id, List<Scenario> scenarios, List<Template> templates,
                           SettingsConsumer settingsConsumer, Runnable onStart, Runnable onStop) {
    public BattleRoyale(UUID id, List<Scenario> scenarios, List<Template> templates, SettingsConsumer settingsConsumer, Runnable onStart, Runnable onStop) {
        this.id = id;
        this.scenarios = scenarios;
        this.templates = templates;
        this.settingsConsumer = settingsConsumer;
        this.onStart = onStart;
        this.onStop = onStop;

        BattleRoyaleEngine.getBattleRoyaleEngine().register(this);

    }

    public static BattleRoyaleBuilder battleroyale(UUID uuid) {
        return new BattleRoyaleBuilder(uuid);
    }

    public static class BattleRoyaleBuilder {

        private final UUID id;

        private final List<Scenario> scenarios = new ArrayList<>();
        private final List<Template> templates = new ArrayList<>();

        private final SettingsConsumer settings = new SettingsConsumer();

        private Runnable onStart = () -> {
        };
        private Runnable onStop = () -> {
        };

        public BattleRoyaleBuilder(UUID id) {
            this.id = id;
        }

        public BattleRoyaleBuilder withTemplates(Template... templates) {
            this.templates.addAll(Arrays.asList(templates));
            return this;
        }

        public BattleRoyaleBuilder withScenarios(Scenario... scenarios) {
            this.scenarios.addAll(Arrays.asList(scenarios));
            return this;
        }

        public BattleRoyaleBuilder withSettings(Consumer<SettingsConsumer> settings) {
            settings.accept(this.settings);
            return this;
        }

        public BattleRoyaleBuilder onStart(Runnable runnable) {
            this.onStart = runnable;
            return this;
        }

        public BattleRoyaleBuilder onStop(Runnable runnable) {
            this.onStop = runnable;
            return this;
        }

        public BattleRoyale build() {
            if (id == null) {
                Main.getPlugin().getComponentLogger().warn(Component.text("The ID of a Battle Royale is null! Defaulted to a random ID"), new IllegalStateException());
                return new BattleRoyale(UUID.randomUUID(), scenarios, templates, settings, onStart, onStop);
            }

            return new BattleRoyale(id, scenarios, templates, settings, onStart, onStop);

        }

    }

}
