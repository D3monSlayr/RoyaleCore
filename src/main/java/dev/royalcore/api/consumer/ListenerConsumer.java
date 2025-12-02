package dev.royalcore.api.consumer;

import dev.royalcore.Main;
import dev.royalcore.annotations.UnstableOnServerStart;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Listener;

import java.rmi.AlreadyBoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Collects Bukkit {@link Listener} instances for later registration.
 */
@UnstableOnServerStart
public class ListenerConsumer {

    @Getter
    private final List<Listener> listeners = new ArrayList<>();

    /**
     * Creates a new listener consumer.
     */
    public ListenerConsumer() {
    }

    /**
     * Registers a listener with this consumer if it is not already present.
     *
     * @param listener the listener to register
     */
    public void register(Listener listener) {

        if (listeners.contains(listener)) {
            Main.getPlugin().getComponentLogger().error(
                    Component.text("A listener is already included in the registry!"),
                    new AlreadyBoundException()
            );
            return;
        }

        listeners.add(listener);
    }

}
