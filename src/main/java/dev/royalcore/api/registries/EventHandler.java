package dev.royalcore.api.registries;

import dev.royalcore.Main;
import dev.royalcore.annotation.UnusableOnServerStart;
import lombok.Getter;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

@UnusableOnServerStart
public final class EventHandler {

    @Getter
    private static final EventHandler eventHandler = new EventHandler();
    final List<Listener> listeners = new ArrayList<>();

    private EventHandler() {
    }

    public void register(Listener listener) {
        listeners.add(listener);
    }

    public void unregister(Listener listener) {
        listeners.remove(listener);
    }

    public void finish() {
        for (Listener listener : listeners) {
            Main.getPlugin().getServer().getPluginManager().registerEvents(listener, Main.getPlugin());
        }
    }

}
