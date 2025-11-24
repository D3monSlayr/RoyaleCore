package dev.royalcore.api;

import dev.royalcore.Main;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class EventRegistry {

    private static final List<Listener> eventListeners = new ArrayList<>();

    public void add(Listener listener) {
        eventListeners.add(listener);
    }

    public void remove(Listener listener) {
        eventListeners.remove(listener);
    }

    public static void registerAll() {
        for (Listener listener : eventListeners) {
            Main.getPlugin().getServer().getPluginManager().registerEvents(listener, Main.getPlugin());
        }
    }

}
