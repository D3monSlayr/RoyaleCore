package dev.royalcore.tests.unit1;

import dev.royalcore.annotations.NotForDeveloperUse;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@NotForDeveloperUse
public class DBL1 implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().sendRichMessage("<green>Works!");
    }

}
