package dev.royalcore;

import dev.royalcore.annotations.NotForDeveloperUse;
import dev.royalcore.api.registries.CommandRegistry;
import dev.royalcore.api.registries.FailedBRRegistry;
import dev.royalcore.api.registries.ListenerRegistry;
import dev.royalcore.api.registries.RecipeRegistry;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin entry point for RoyaleCore.
 */
public class Main extends JavaPlugin {

    @Getter
    private static Main plugin;
    /**
     * Global flag indicating whether debug logging is enabled for the plugin.
     * <p>
     * When {@code true}, calls to {@link #debug(net.kyori.adventure.text.TextComponent)}
     * and {@link #debug(net.kyori.adventure.text.TextComponent, Throwable)} will emit output.
     */
    @Getter
    @Setter
    private static boolean debug = false;

    /**
     * Creates the main plugin instance.
     */
    public Main() {
        super();
    }

    /**
     * Logs a debug message if debug mode is enabled.
     *
     * @param message the debug message to log
     *
     */
    @NotForDeveloperUse
    public static void debug(TextComponent message) {
        if (debug) {
            Main.getPlugin().getComponentLogger().debug(message);
        }
    }

    /**
     * Logs a debug message and associated throwable if debug mode is enabled.
     *
     * @param message the debug message to log
     * @param e       the throwable to include in the debug output
     */
    @NotForDeveloperUse
    public static void debug(TextComponent message, Throwable e) {
        if (debug) {
            Main.getPlugin().getComponentLogger().debug(message, new Throwable(e));
        }
    }

    @Override
    public void onEnable() {
        plugin = this;
        FailedBRRegistry.declare();

        RecipeRegistry.getRecipeRegistry().finish();
        CommandRegistry.getCommandRegistry().finish();
        ListenerRegistry.getListenerRegistry().finish();

    }

    @Override
    public void onDisable() {
    }


}
