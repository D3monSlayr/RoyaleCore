package dev.royalcore.api.consumer;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

/**
 * Consumer for per-player operations associated with a scenario.
 */
public class PlayerConsumer {

    /**
     * The underlying player consumer, invoked for each relevant player.
     */
    @Getter
    private final Consumer<Player> playerConsumer = player -> {
    };

    /**
     * Creates a new player consumer.
     */
    public PlayerConsumer() {
    }

}
