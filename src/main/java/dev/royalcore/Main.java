package dev.royalcore;

import dev.royalcore.api.registries.CommandHandler;
import dev.royalcore.api.registries.EventHandler;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Getter
    private static Main plugin;

    @Override
    public void onEnable() {
        plugin = this;

        EventHandler.getEventHandler().finish();
        CommandHandler.getCommandHandler().finish();

    }

}
