package dev.royalcore.api.br;

import dev.royalcore.api.module.Module;
import dev.royalcore.api.scenario.Scenario;
import dev.royalcore.api.settings.Settings;
import dev.royalcore.internal.BattleRoyaleEngine;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BattleRoyale {

    @Getter
    private final List<Module> modules;
    @Getter
    private final List<Scenario> scenarios;
    @Getter
    private final Settings settings;
    @Setter
    @Getter
    int index;

    public BattleRoyale(List<Module> modules, List<Scenario> scenarios, Settings settings) {
        this.modules = modules;
        this.scenarios = scenarios;
        this.settings = settings;

        BattleRoyaleEngine.getBattleRoyaleEngine().register(this);

    }

    public static BattleRoyaleBuilder battleroyale() {
        return new BattleRoyaleBuilder();
    }

    public static class BattleRoyaleBuilder {
        private final List<Module> modules = new ArrayList<>();
        private final List<Scenario> scenarios = new ArrayList<>();
        private final Settings settings = new Settings();

        public BattleRoyaleBuilder modules(Consumer<List<Module>> module) {
            module.accept(this.modules);
            return this;
        }

        public BattleRoyaleBuilder settings(Consumer<Settings> settings) {
            settings.accept(this.settings);
            return this;
        }

        public BattleRoyaleBuilder scenarios(Consumer<List<Scenario>> scenarios) {
            scenarios.accept(this.scenarios);
            return this;
        }

        public BattleRoyale build() {
            return new BattleRoyale(modules, scenarios, settings);
        }

    }

}
