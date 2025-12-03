package dev.royalcore.api.br;

import dev.royalcore.Main;
import dev.royalcore.annotations.UnstableOnServerStart;
import dev.royalcore.api.consumer.ResourcePackConsumer;
import dev.royalcore.api.consumer.SettingsConsumer;
import dev.royalcore.api.consumer.StructureConsumer;
import dev.royalcore.api.consumer.WorldConsumer;
import dev.royalcore.api.engine.BattleRoyaleEngine;
import dev.royalcore.api.enums.BattleRoyaleState;
import dev.royalcore.api.scenario.Scenario;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.function.Consumer;

/**
 * Represents a single Battle Royale game definition, including its unique ID,
 * scenarios, settings, lifecycle callbacks and current state.
 */
public final class BattleRoyale {
    private final UUID id;
    private final List<Scenario> scenarios;
    private final SettingsConsumer settingsConsumer;
    private final Runnable onStart;
    private final Runnable onStop;
    private final ResourcePackConsumer resourcepackConsumer;
    private final StructureConsumer structureConsumer;
    private final WorldConsumer worldConsumer;
    private BattleRoyaleState state;

    /**
     * Creates a new BattleRoyale instance and registers it in the legacy {@link BattleRoyaleEngine}.
     *
     * @param id                   the unique identifier of this Battle Royale definition
     * @param scenarios            the list of scenarios that belong to this Battle Royale (unmodifiable view)
     * @param settingsConsumer     the settings consumer for configuring Battle Royale settings
     * @param onStart              the callback invoked when the Battle Royale starts
     * @param onStop               the callback invoked when the Battle Royale stops
     * @param state                the initial state of the Battle Royale
     * @param resourcepackConsumer the resource pack consumer for managing client resource packs
     * @param structureConsumer    the structure consumer for world generation and placement
     * @param worldConsumer        the world consumer for world creation and management
     */
    public BattleRoyale(
            UUID id,
            List<Scenario> scenarios,
            SettingsConsumer settingsConsumer,
            Runnable onStart,
            Runnable onStop,
            BattleRoyaleState state,
            ResourcePackConsumer resourcepackConsumer,
            StructureConsumer structureConsumer,
            WorldConsumer worldConsumer
    ) {
        this.id = id;
        this.scenarios = Collections.unmodifiableList(new ArrayList<>(scenarios));
        this.settingsConsumer = settingsConsumer;
        this.onStart = onStart;
        this.onStop = onStop;
        this.state = state;
        this.resourcepackConsumer = resourcepackConsumer;
        this.structureConsumer = structureConsumer;
        this.worldConsumer = worldConsumer;

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
     * <p>
     * Marked as unstable during server startup due to potential registration timing issues.
     *
     * @param consumer a consumer that receives the current {@code onStart} runnable
     */
    @UnstableOnServerStart
    public void onStart(Consumer<Runnable> consumer) {
        consumer.accept(this.onStart);
    }

    /**
     * Exposes the configured {@code onStop} callback to external consumers.
     * <p>
     * Marked as unstable during server startup due to potential registration timing issues.
     *
     * @param consumer a consumer that receives the current {@code onStop} runnable
     */
    @UnstableOnServerStart
    public void onStop(Consumer<Runnable> consumer) {
        consumer.accept(this.onStop);
    }

    // Record-style getters (consistent with modern Java conventions)
    public UUID id() {
        return id;
    }

    public List<Scenario> scenarios() {
        return scenarios;
    }

    public SettingsConsumer settingsConsumer() {
        return settingsConsumer;
    }

    public Runnable onStart() {
        return onStart;
    }

    public Runnable onStop() {
        return onStop;
    }

    public BattleRoyaleState state() {
        return state;
    }

    public ResourcePackConsumer resourcepackConsumer() {
        return resourcepackConsumer;
    }

    public StructureConsumer structureConsumer() {
        return structureConsumer;
    }

    public WorldConsumer worldConsumer() {
        return worldConsumer;
    }

    /**
     * Updates the current state of this Battle Royale.
     *
     * @param state the new state to set
     */
    public void state(BattleRoyaleState state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (BattleRoyale) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.scenarios, that.scenarios) &&
                Objects.equals(this.settingsConsumer, that.settingsConsumer) &&
                Objects.equals(this.onStart, that.onStart) &&
                Objects.equals(this.onStop, that.onStop) &&
                Objects.equals(this.state, that.state) &&
                Objects.equals(this.resourcepackConsumer, that.resourcepackConsumer) &&
                Objects.equals(this.structureConsumer, that.structureConsumer) &&
                Objects.equals(this.worldConsumer, that.worldConsumer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, scenarios, settingsConsumer, onStart, onStop, state, resourcepackConsumer, structureConsumer, worldConsumer);
    }

    @Override
    public String toString() {
        return "BattleRoyale[" +
                "id=" + id + ", " +
                "scenarios=" + scenarios + ", " +
                "settingsConsumer=" + settingsConsumer + ", " +
                "onStart=" + onStart + ", " +
                "onStop=" + onStop + ", " +
                "state=" + state + ", " +
                "resourcepackConsumer=" + resourcepackConsumer + ", " +
                "structureConsumer=" + structureConsumer + ", " +
                "worldConsumer=" + worldConsumer + ']';
    }

    /**
     * Builder for {@link BattleRoyale} instances, providing a fluent API
     * to configure scenarios, settings, resource packs, structures, worlds,
     * and lifecycle callbacks.
     */
    public static class BattleRoyaleBuilder {

        private final UUID id;
        private final List<Scenario> scenarios = new ArrayList<>();
        private final SettingsConsumer settings = new SettingsConsumer();
        private final ResourcePackConsumer resourcepacks = new ResourcePackConsumer();
        private final StructureConsumer structures = new StructureConsumer();
        private final WorldConsumer world = new WorldConsumer();
        private final BattleRoyaleState state = BattleRoyaleState.NOT_STARTED;
        private Runnable onStart = () -> {
        };
        private Runnable onStop = () -> {
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
         * Adds one or more scenarios to this Battle Royale definition.
         *
         * @param scenarios one or more scenarios to include
         * @return this builder instance for chaining
         */
        public BattleRoyaleBuilder withScenarios(Scenario... scenarios) {
            this.scenarios.addAll(Arrays.asList(scenarios));
            return this;
        }

        /**
         * Configures world settings for this Battle Royale.
         *
         * @param worldSettings a consumer that configures the internal {@link WorldConsumer}
         * @return this builder instance for chaining
         */
        public BattleRoyaleBuilder withWorldSettings(Consumer<WorldConsumer> worldSettings) {
            worldSettings.accept(world);
            return this;
        }

        /**
         * Configures resource packs for this Battle Royale.
         *
         * @param resourcePacksConsumer a consumer that mutates the internal {@link ResourcePackConsumer}
         * @return this builder instance for chaining
         */
        public BattleRoyaleBuilder withResourcePacks(Consumer<ResourcePackConsumer> resourcePacksConsumer) {
            resourcePacksConsumer.accept(this.resourcepacks);
            return this;
        }

        /**
         * Configures settings for this Battle Royale.
         *
         * @param settingsConsumer a consumer that mutates the internal {@link SettingsConsumer}
         * @return this builder instance for chaining
         */
        public BattleRoyaleBuilder withSettings(Consumer<SettingsConsumer> settingsConsumer) {
            settingsConsumer.accept(this.settings);
            return this;
        }

        /**
         * Sets the {@code onStart} callback invoked when the Battle Royale starts.
         *
         * @param runnable the start callback
         * @return this builder instance for chaining
         */
        public BattleRoyaleBuilder onStart(Runnable runnable) {
            this.onStart = runnable;
            return this;
        }

        /**
         * Sets the {@code onStop} callback invoked when the Battle Royale stops.
         *
         * @param runnable the stop callback
         * @return this builder instance for chaining
         */
        public BattleRoyaleBuilder onStop(Runnable runnable) {
            this.onStop = runnable;
            return this;
        }

        /**
         * Provides access to the current {@code onStart} callback for modification.
         *
         * @param consumer a consumer that receives and can modify the current start callback
         * @return this builder instance for chaining
         */
        public BattleRoyaleBuilder onStart(Consumer<Runnable> consumer) {
            consumer.accept(onStart);
            return this;
        }

        /**
         * Provides access to the current {@code onStop} callback for modification.
         *
         * @param consumer a consumer that receives and can modify the current stop callback
         * @return this builder instance for chaining
         */
        public BattleRoyaleBuilder onStop(Consumer<Runnable> consumer) {
            consumer.accept(onStop);
            return this;
        }

        /**
         * Configures structures for this Battle Royale.
         *
         * @param structuresConsumer a consumer that mutates the internal {@link StructureConsumer}
         * @return this builder instance for chaining
         */
        public BattleRoyaleBuilder withStructures(Consumer<StructureConsumer> structuresConsumer) {
            structuresConsumer.accept(this.structures);
            return this;
        }

        /**
         * Builds and returns a new {@link BattleRoyale} instance with the current configuration.
         * <p>
         * Logs a warning if the ID is null and generates a random UUID as fallback.
         *
         * @return a new BattleRoyale instance
         */
        public BattleRoyale build() {
            if (id == null) {
                Main.getPlugin().getComponentLogger().warn(
                        Component.text("The ID of a Battle Royale is null! Defaulted to a random ID"),
                        new IllegalStateException()
                );
                return new BattleRoyale(UUID.randomUUID(), scenarios, settings, onStart, onStop, state, resourcepacks, structures, world);
            }
            return new BattleRoyale(id, scenarios, settings, onStart, onStop, state, resourcepacks, structures, world);
        }
    }
}
