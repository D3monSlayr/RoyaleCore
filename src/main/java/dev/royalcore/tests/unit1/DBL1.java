package dev.royalcore.tests.unit1;

import dev.royalcore.annotations.MarkedForRemoval;
import dev.royalcore.annotations.NotForDeveloperUse;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Test listener used for database-related unit tests.
 */
@MarkedForRemoval
@NotForDeveloperUse
public class DBL1 implements Listener {

    /**
     * Creates a new test listener.
     */
    public DBL1() {
    }

    /**
     * Handles player join events for testing.
     *
     * @param event the join event
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().sendRichMessage("<green>Works!");
    }

}
