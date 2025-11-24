package dev.royalcore;

import dev.royalcore.api.EventRegistry;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Getter
    private static Main plugin;

    @Override
    public void onEnable() {
        plugin = this;

        EventRegistry.registerAll();

    }

}
