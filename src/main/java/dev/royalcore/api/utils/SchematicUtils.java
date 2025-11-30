package dev.royalcore.api.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Random;

public class SchematicUtils {

    private static final Random random = new Random();

    /**
     * Finds the highest possible flat ground level inside the given XZ bounds where a
     * cuboid of the given size fits.
     *
     * @param world           target world
     * @param minX            minimum X of the search area
     * @param minZ            minimum Z of the search area
     * @param maxX            maximum X of the search area
     * @param maxZ            maximum Z of the search area
     * @param structureWidth  width of the cuboid (X)
     * @param structureLength length of the cuboid (Z)
     * @param structureHeight height of the cuboid (Y)
     * @param minY            minimum allowed ground Y
     * @param maxY            maximum allowed ground Y
     * @return origin location or {@code null} if none fits
     */
    public static Location findHighestFittingCuboid(
            World world,
            int minX, int minZ,
            int maxX, int maxZ,
            int structureWidth,
            int structureLength,
            int structureHeight,
            int minY,
            int maxY
    ) {
        Location best = null;

        for (int x = minX; x <= maxX - structureWidth + 1; x++) {
            for (int z = minZ; z <= maxZ - structureLength + 1; z++) {

                int minGroundY = Integer.MAX_VALUE;
                int maxGroundY = Integer.MIN_VALUE;

                for (int dx = 0; dx < structureWidth; dx++) {
                    for (int dz = 0; dz < structureLength; dz++) {
                        Block highest = world.getHighestBlockAt(x + dx, z + dz);
                        int gy = highest.getY();
                        if (gy < minGroundY) minGroundY = gy;
                        if (gy > maxGroundY) maxGroundY = gy;
                    }
                }

                if (minGroundY != maxGroundY) {
                    continue;
                }

                int groundY = maxGroundY;

                if (groundY < minY || groundY > maxY) {
                    continue;
                }

                boolean clear = true;
                for (int dx = 0; dx < structureWidth && clear; dx++) {
                    for (int dz = 0; dz < structureLength && clear; dz++) {
                        for (int dy = 1; dy <= structureHeight; dy++) {
                            Block b = world.getBlockAt(x + dx, groundY + dy, z + dz);
                            if (!b.isEmpty()) {
                                clear = false;
                            }
                        }
                    }
                }

                if (!clear) {
                    continue;
                }

                if (best == null || groundY > best.getBlockY() - 1) {
                    best = new Location(world, x, groundY + 1, z);
                }
            }
        }

        return best;
    }

    /**
     * Returns a random integer in the inclusive range [min, max].
     *
     * @param min minimum value (inclusive)
     * @param max maximum value (inclusive)
     * @return random integer in range
     */
    public static int getRandomInRange(int min, int max) {
        if (min >= max) {
            return min;
        }
        return random.nextInt((max - min) + 1) + min;
    }

}
