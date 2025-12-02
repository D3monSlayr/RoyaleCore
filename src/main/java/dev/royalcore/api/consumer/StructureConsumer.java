package dev.royalcore.api.consumer;

import dev.royalcore.Main;
import dev.royalcore.annotations.Experimental;
import dev.royalcore.api.errors.Result;
import net.kyori.adventure.text.Component;
import net.sandrohc.schematic4j.SchematicLoader;
import net.sandrohc.schematic4j.exception.ParsingException;
import net.sandrohc.schematic4j.schematic.Schematic;
import net.sandrohc.schematic4j.schematic.types.SchematicBlock;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import static dev.royalcore.api.utils.SchematicUtils.findHighestFittingCuboid;
import static dev.royalcore.api.utils.SchematicUtils.getRandomInRange;

/**
 * Consumes and manages schematic structures for automatic placement in a world.
 * <p>
 * This class is responsible for:
 * <ul>
 *     <li>Loading schematics from different sources (path, string, file).</li>
 *     <li>Tracking per-schematic vertical placement constraints (minY, maxY).</li>
 *     <li>Finding suitable locations and pasting the loaded schematics into a world.</li>
 * </ul>
 *
 */
@Experimental
public class StructureConsumer {

    /**
     * Default constructor
     */
    public StructureConsumer() {
    }

    private final Map<TrackedSchematic, Double> schematics = new LinkedHashMap<>();

    /**
     * Adds a schematic by path.
     *
     * @param path path to schematic
     * @return result of the operation
     */
    public Result add(Path path) {
        try {
            Schematic schematic = SchematicLoader.load(path);
            schematics.put(new TrackedSchematic(schematic), 1.0D);
        } catch (ParsingException | IOException e) {
            return Result.Err(
                    Component.text("Failed to load a schematic!"),
                    new RuntimeException(e),
                    false
            );
        }
        return Result.Ok();
    }

    /**
     * Adds a schematic by string path.
     *
     * @param path path to schematic
     * @return result of the operation
     */
    public Result add(String path) {
        try {
            Schematic schematic = SchematicLoader.load(path);
            schematics.put(new TrackedSchematic(schematic), 1.0D);
        } catch (ParsingException | IOException e) {
            return Result.Err(
                    Component.text("Failed to load a schematic!"),
                    new RuntimeException(e),
                    false
            );
        }
        return Result.Ok();
    }

    /**
     * Adds a schematic by file.
     *
     * @param file schematic file
     * @return result of the operation
     */
    public Result add(File file) {
        try {
            Schematic schematic = SchematicLoader.load(file);
            schematics.put(new TrackedSchematic(schematic), 1.0D);
        } catch (ParsingException | IOException e) {
            return Result.Err(
                    Component.text("Failed to load a schematic!"),
                    new RuntimeException(e),
                    false
            );
        }
        return Result.Ok();
    }

    /**
     * Spawns all schematics in random positions in the world while
     * respecting each schematic's minY and maxY constraints.
     *
     * @param world target world
     * @return result of the operation
     */
    public Result spawnAll(World world) {
        int radius = (int) world.getWorldBorder().getSize() / 2;
        int centerX = world.getWorldBorder().getCenter().getBlockX();
        int centerZ = world.getWorldBorder().getCenter().getBlockZ();

        for (TrackedSchematic tracked : schematics.keySet()) {
            int randomX = getRandomInRange(centerX - radius, centerX + radius);
            int randomZ = getRandomInRange(centerZ - radius, centerZ + radius);

            int searchRadius = 32;

            Schematic schematic = tracked.schematic();
            int width = schematic.width();
            int length = schematic.length();
            int height = schematic.height();

            Location origin = findHighestFittingCuboid(
                    world,
                    randomX - searchRadius,
                    randomZ - searchRadius,
                    randomX + searchRadius,
                    randomZ + searchRadius,
                    width,
                    length,
                    height,
                    tracked.minY(),
                    tracked.maxY()
            );

            if (origin == null) {
                continue;
            }

            pasteSchematic(world, origin, schematic);
        }

        return Result.Ok();
    }

    /**
     * Spawns all schematics around a base location in the given world.
     *
     * @param world        target world
     * @param baseLocation base location used as search center
     * @return result of the operation
     */
    public Result spawnAll(World world, Location baseLocation) {
        int baseX = baseLocation.getBlockX();
        int baseZ = baseLocation.getBlockZ();
        int searchRadius = 64;

        for (TrackedSchematic tracked : schematics.keySet()) {
            Schematic schematic = tracked.schematic();
            int width = schematic.width();
            int length = schematic.length();
            int height = schematic.height();

            Location origin = findHighestFittingCuboid(
                    world,
                    baseX - searchRadius,
                    baseZ - searchRadius,
                    baseX + searchRadius,
                    baseZ + searchRadius,
                    width,
                    length,
                    height,
                    tracked.minY(),
                    tracked.maxY()
            );

            if (origin == null) {
                continue;
            }

            pasteSchematic(world, origin, schematic);
        }

        return Result.Ok();
    }

    /**
     * Pastes a schematic into the world at the given origin location.
     *
     * @param world     target world
     * @param origin    origin location for schematic (min corner)
     * @param schematic schematic to paste
     */
    private void pasteSchematic(World world, Location origin, Schematic schematic) {
        int ox = origin.getBlockX();
        int oy = origin.getBlockY();
        int oz = origin.getBlockZ();

        int width = schematic.width();
        int height = schematic.height();
        int length = schematic.length();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < length; z++) {
                    SchematicBlock sBlock = schematic.block(x, y, z);
                    if (sBlock == null) {
                        continue;
                    }

                    String blockName = sBlock.name();
                    BlockData blockData;
                    try {
                        blockData = Main.getPlugin().getServer().createBlockData(blockName);
                    } catch (IllegalArgumentException ex) {
                        continue;
                    }

                    Block target = world.getBlockAt(ox + x, oy + y, oz + z);
                    target.setBlockData(blockData, false);
                }

            }
        }
    }

    /**
     * Internal schematic wrapper that tracks vertical placement constraints.
     */
    private record TrackedSchematic(Schematic schematic, int minY, int maxY) {

        /**
         * Creates a tracked schematic with default Y constraints.
         * Default range is 0 to 319.
         *
         * @param schematic wrapped schematic
         */
        private TrackedSchematic(Schematic schematic) {
            this(schematic, 0, 319);
        }

        /**
         * Creates a tracked schematic with explicit Y constraints.
         *
         * @param schematic wrapped schematic
         * @param minY      minimum allowed ground Y
         * @param maxY      maximum allowed ground Y
         */
        private TrackedSchematic {
        }
    }
}
