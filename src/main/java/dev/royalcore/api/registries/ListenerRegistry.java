package dev.royalcore.api.registries;

import dev.royalcore.Main;
import org.bukkit.event.Listener;

public class ListenerRegistry {

    private ListenerRegistry() {
    }

    public static void register(Listener listener) {
        Main.getPlugin().getServer().getPluginManager().registerEvents(listener, Main.getPlugin());
    }

}
