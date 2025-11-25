package dev.royalcore;

import dev.royalcore.api.registrar.CommandRegistrar;
import dev.royalcore.api.registrar.ItemRegistrar;
import dev.royalcore.api.registrar.ListenerRegistrar;
import dev.royalcore.internal.BattleRoyaleEngine;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Getter
    private static Main plugin;

    @Override
    public void onEnable() {
        plugin = this;

        ListenerRegistrar.getListenerRegistrar().finish();
        CommandRegistrar.getCommandRegistrar().finish();
        ItemRegistrar.getItemRegistrar().finish();
        BattleRoyaleEngine.getBattleRoyaleEngine().finish();

    }

}
