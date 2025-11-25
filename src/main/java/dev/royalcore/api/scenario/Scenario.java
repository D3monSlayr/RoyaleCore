package dev.royalcore.api.scenario;

import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.royalcore.annotation.SubjectToChange;
import dev.royalcore.api.item.BattleRoyaleItem;
import dev.royalcore.api.module.Module;
import dev.royalcore.api.settings.Settings;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class Scenario {

    @Getter
    private final String name;

    @Getter
    private final List<Listener> listeners;

    @Getter
    private final List<BattleRoyaleItem> items;

    @Getter
    private final long startTimeMillis;

    @Getter
    private final long stopTimeMillis;

    @Getter
    private final BooleanSupplier startCondition;

    @Getter
    private final BooleanSupplier stopCondition;
    @Getter
    private final List<LiteralCommandNode<CommandSourceStack>> commands;
    @Getter
    private final List<Module> modules = new ArrayList<>();
    @Getter
    private final Runnable onStart;
    @Getter
    private final Runnable onStop;
    @Getter
    private boolean active = false;

    public Scenario(String name, List<Listener> listeners, List<BattleRoyaleItem> items, long startTimeMillis, long stopTimeMillis, BooleanSupplier startCondition, BooleanSupplier stopCondition, List<LiteralCommandNode<CommandSourceStack>> commands, Runnable onStart, Runnable onStop) {
        this.name = name;
        this.listeners = listeners;
        this.items = items;
        this.startTimeMillis = startTimeMillis;
        this.stopTimeMillis = stopTimeMillis;
        this.startCondition = startCondition;
        this.stopCondition = stopCondition;
        this.commands = commands;
        this.onStart = onStart;
        this.onStop = onStop;
    }

    public static ScenarioBuilder scenario(String name) {
        return new ScenarioBuilder(name);
    }

    public void activate(Plugin plugin) {
        if (!active) {
            active = true;

            if (onStart == null) {
                System.out.println("Looks like on start tasks have not been set for " + name);
            } else {
                Bukkit.getScheduler().runTask(plugin, onStart);
            }

            System.out.println("Scenario '" + name + "' activated.");
        }
    }

    public void deactivate(Plugin plugin) {
        if (active) {
            active = false;

            if (onStop == null) {
                System.out.println("Looks like on stop tasks have not been set for " + name);
            } else {
                Bukkit.getScheduler().runTask(plugin, onStop);
            }

            System.out.println("Scenario '" + name + "' deactivated.");
        }
    }

    public static class ScenarioBuilder {
        private final String name;

        private final List<Listener> listeners = new ArrayList<>();
        private final List<BattleRoyaleItem> items = new ArrayList<>();
        private final List<LiteralCommandNode<CommandSourceStack>> commands = new ArrayList<>();
        private final List<Module> modules = new ArrayList<>();
        private final Settings settings = new Settings();

        private long startTimeMillis = 0L;
        private long stopTimeMillis = Long.MAX_VALUE;

        private BooleanSupplier startCondition = () -> true;
        private BooleanSupplier stopCondition = () -> false;

        private Runnable onStart = () -> {
        };
        private Runnable onStop = () -> {
        };

        public ScenarioBuilder(String name) {
            this.name = name;
        }

        public ScenarioBuilder modules(Consumer<List<Module>> modules) {
            modules.accept(this.modules);
            return this;
        }

        public ScenarioBuilder onStart(Runnable runnable) {
            this.onStart = runnable;
            return this;
        }

        public ScenarioBuilder onStop(Runnable runnable) {
            this.onStop = runnable;
            return this;
        }

        public ScenarioBuilder startTime(Duration duration) {
            this.startTimeMillis = duration.toMillis();
            return this;
        }

        public ScenarioBuilder stopTime(Duration duration) {
            this.stopTimeMillis = duration.toMillis();
            return this;
        }

        public ScenarioBuilder startCondition(BooleanSupplier startCondition) {
            this.startCondition = startCondition;
            return this;
        }

        public ScenarioBuilder stopCondition(BooleanSupplier stopCondition) {
            this.stopCondition = stopCondition;
            return this;
        }

        @SubjectToChange
        public ScenarioBuilder listeners(Consumer<List<Listener>> listeners) {
            listeners.accept(this.listeners);
            return this;
        }

        @SubjectToChange
        public ScenarioBuilder items(Consumer<List<BattleRoyaleItem>> items) {
            items.accept(this.items);
            return this;
        }

        @SubjectToChange
        public ScenarioBuilder commands(Consumer<List<LiteralCommandNode<CommandSourceStack>>> commands) {
            commands.accept(this.commands);
            return this;
        }

        public ScenarioBuilder settings(Consumer<Settings> settings) {
            settings.accept(this.settings);
            return this;
        }

        public Scenario build() {
            if (name == null || name.isEmpty()) {
                throw new IllegalStateException("Scenario name must be set");
            }
            return new Scenario(name, listeners, items, startTimeMillis, stopTimeMillis, startCondition, stopCondition, commands, onStart, onStop);
        }

    }

}
