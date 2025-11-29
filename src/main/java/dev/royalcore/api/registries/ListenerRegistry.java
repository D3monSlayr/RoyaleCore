package dev.royalcore.api.registries;

import dev.royalcore.Main;
import dev.royalcore.annotations.NotForDeveloperUse;
import dev.royalcore.annotations.UnstableOnServerStart;
import lombok.Getter;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

/**
 * Registry for deferred registration of Bukkit {@link Listener} instances.
 * <p>
 * Listeners are collected during setup and registered with the server's
 * plugin manager in a single batch via {@link #finish()}.
 */
@UnstableOnServerStart
public class ListenerRegistry {

    /**
     * Singleton instance of the {@link ListenerRegistry}.
     *
     */
    @Getter
    private static final ListenerRegistry listenerRegistry = new ListenerRegistry();

    private final List<Listener> listeners = new ArrayList<>();

    private ListenerRegistry() {
    }

    /**
     * Adds a {@link Listener} to the internal registry to be registered later.
     *
     * @param listener the listener to register
     */
    @UnstableOnServerStart
    public void register(Listener listener) {
        listeners.add(listener);
    }

    /**
     * Registers all collected listeners with the server's plugin manager.
     * <p>
     * This should be called once during startup after all listeners have
     * been registered with this registry.
     */
    @NotForDeveloperUse
    @UnstableOnServerStart
    public void finish() {
        for (Listener listener : listeners) {
            Main.getPlugin().getServer().getPluginManager().registerEvents(listener, Main.getPlugin());
        }
    }

}
