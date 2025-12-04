package dev.royalcore.internal.start;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveListener implements Listener {

    @Getter
    @Setter
    private static boolean canMove = true;

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (!canMove) event.setCancelled(true);
    }

}
