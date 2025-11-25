package dev.royalcore.api.registrar;

import dev.royalcore.Main;
import dev.royalcore.annotation.UnusableOnServerStart;
import lombok.Getter;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

@UnusableOnServerStart
public class ListenerRegistrar {

    @Getter
    private static final ListenerRegistrar listenerRegistrar = new ListenerRegistrar();

    private final List<Listener> listeners = new ArrayList<>();

    private ListenerRegistrar() {
    }

    public final void register(Listener listener) {
        listeners.add(listener);
    }

    public final void finish() {
        for (Listener listener : listeners) {
            Main.getPlugin().getServer().getPluginManager().registerEvents(listener, Main.getPlugin());
        }
    }

}
