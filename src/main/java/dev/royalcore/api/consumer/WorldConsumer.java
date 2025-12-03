package dev.royalcore.api.consumer;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.util.ArrayList;
import java.util.List;

public class WorldConsumer {

    @Getter
    private final List<Location> spawnLocations = new ArrayList<>();
    @Getter
    private World brWorld;

    public WorldConsumer() {
        brWorld = Bukkit.getWorlds().getFirst();

        if (brWorld == null) {
            brWorld = Bukkit.getWorld("world");
        }

        if (brWorld == null) {
            brWorld = Bukkit.createWorld(WorldCreator.name("br-world").hardcore(true));
        }

    }

    public void setWorld(World world) {
        brWorld = world;
    }

    public void addLocation(Location location) {
        spawnLocations.add(location);
    }

}
