package dev.royalcore.api.start;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class Queue {

    @Getter
    private final List<UUID> players = new ArrayList<>();

    public static Queue queue(Collection<? extends Player> players) {
        Queue queue = new Queue();

        for (Player player : players) {
            if (!queue.inInQueue(player)) queue.add(player);
        }

        return queue;

    }

    public static Queue queue(Collection<? extends Player> players, Consumer<Player> actions) {
        Queue queue = Queue.queue(players);

        for (UUID id : queue.getPlayers()) {
            Player player = Bukkit.getPlayer(id);
            if (player != null && player.isOnline()) {
                actions.accept(player);
            }
        }

        return queue;

    }

    public static Queue queue(Collection<? extends Player> players, Consumer<Player> playerActions, Consumer<Queue> queueActions) {
        Queue queue = Queue.queue(players, playerActions);
        queueActions.accept(queue);

        return queue;

    }

    public void add(Player player) {
        players.add(player.getUniqueId());
    }

    public void remove(Player player) {
        players.remove(player.getUniqueId());
    }

    public boolean inInQueue(UUID uuid) {
        return players.contains(uuid);
    }

    public boolean inInQueue(Player player) {
        return players.contains(player.getUniqueId());
    }

}
