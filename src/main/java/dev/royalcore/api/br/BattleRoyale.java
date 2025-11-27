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

public record BattleRoyale(UUID id, List<Scenario> scenarios,
                           SettingsConsumer settingsConsumer, Runnable onStart, Runnable onStop,
                           BattleRoyaleState state) {
    public BattleRoyale(UUID id, List<Scenario> scenarios, SettingsConsumer settingsConsumer, Runnable onStart, Runnable onStop, BattleRoyaleState state) {
        this.id = id;
        this.scenarios = scenarios;
        this.settingsConsumer = settingsConsumer;
        this.onStart = onStart;
        this.onStop = onStop;
        this.state = state;

        BattleRoyaleEngine.getBattleRoyaleEngine().register(this);

    }

    public void start() {
        Bukkit.getScheduler().runTask(Main.getPlugin(), onStart);
    }

    public void stop() {
        Bukkit.getScheduler().runTask(Main.getPlugin(), onStop);
    }

    @UnstableOnServerStart
    public void onStart(Consumer<Runnable> onStart) {
        onStart.accept(this.onStart);
    }

    public static BattleRoyaleBuilder battleroyale(UUID uuid) {
        return new BattleRoyaleBuilder(uuid);
    }

    public static class BattleRoyaleBuilder {

        private final UUID id;

        private final List<Scenario> scenarios = new ArrayList<>();

        private final SettingsConsumer settings = new SettingsConsumer();

        private final BattleRoyaleState state = BattleRoyaleState.WAITING;

        private Runnable onStart = () -> {

        };
        private Runnable onStop = () -> {
        };

        public BattleRoyaleBuilder(UUID id) {
            this.id = id;
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

        public BattleRoyaleBuilder onStart(Consumer<Runnable> runnable) {
            runnable.accept(onStart);
            return this;
        }

        public BattleRoyaleBuilder onStop(Consumer<Runnable> runnable) {
            runnable.accept(onStop);
            return this;
        }

        public BattleRoyale build() {
            if (id == null) {
                Main.getPlugin().getComponentLogger().warn(Component.text("The ID of a Battle Royale is null! Defaulted to a random ID"), new IllegalStateException());
                return new BattleRoyale(UUID.randomUUID(), scenarios, settings, onStart, onStop, state);
            }

            return new BattleRoyale(id, scenarios, settings, onStart, onStop, state);

        }

    }

}
