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

public class BorderConsumer {

    @Getter
    private final Map<World, WorldBorder> borders = new HashMap<>();

    public void addBorder(World world, Consumer<WorldBorder> borderConsumer) {
        WorldBorder border = world.getWorldBorder();
        borderConsumer.accept(border);

        if (world == null) {
            Main.getPlugin().getComponentLogger().error(Component.text("A world is null!"), new IllegalStateException());
            return;
        }

        if (borders.containsKey(world)) {
            Main.getPlugin().getComponentLogger().error(Component.text("A border has already be set for " + world.getName() + "!"), new AlreadyBoundException());
            return;
        }

        borders.put(world, border);
    }

    public void removeBorder(World world) {

        if (world == null) {
            Main.getPlugin().getComponentLogger().error(Component.text("A world is null!"), new IllegalStateException());
            return;
        }

        world.getWorldBorder().reset();
    }

}
