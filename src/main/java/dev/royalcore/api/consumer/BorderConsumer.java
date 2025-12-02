package dev.royalcore.api.consumer;

import dev.royalcore.Main;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.bukkit.WorldBorder;

import java.rmi.AlreadyBoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Collects and manages custom world border configuration for scenarios.
 */
public class BorderConsumer {

    @Getter
    private final Map<World, Consumer<WorldBorder>> borders = new HashMap<>();

    /**
     * Creates a new border consumer.
     */
    public BorderConsumer() {
    }

    /**
     * Associates a world with a border configuration callback.
     *
     * @param world          the world to configure
     * @param borderConsumer the consumer that configures the world's border
     */
    public void addBorder(World world, Consumer<WorldBorder> borderConsumer) {

        if (world == null) {
            Main.getPlugin().getComponentLogger().error(
                    Component.text("A world is null!"),
                    new IllegalStateException()
            );
            return;
        }

        if (borders.containsKey(world)) {
            Main.getPlugin().getComponentLogger().error(
                    Component.text("A border has already be set for " + world.getName() + "!"),
                    new AlreadyBoundException()
            );
            return;
        }

        borders.put(world, borderConsumer);
    }

    /**
     * Removes a custom border for the given world and resets it to defaults.
     *
     * @param world the world whose border should be reset
     */
    public void removeBorder(World world) {

        if (world == null) {
            Main.getPlugin().getComponentLogger().error(
                    Component.text("A world is null!"),
                    new IllegalStateException()
            );
            return;
        }

        if (!borders.containsKey(world)) return;
        world.getWorldBorder().reset();

    }

}
