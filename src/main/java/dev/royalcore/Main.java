package dev.royalcore;

import dev.royalcore.api.registries.FailedBRRegistry;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Getter
    private static Main plugin;

    @Override
    public void onEnable() {
        plugin = this;

        FailedBRRegistry.declare();

    }

}
