package dev.royalcore.api.consumer;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class PlayerConsumer {

    @Getter
    private final Consumer<Player> playerConsumer = player -> {
    };

}
