package dev.freakingrpg.boss.astronomer;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

public final class ArenaChunkLoader {

    private ArenaChunkLoader() {
    }

    public static void ensureLoaded(World world, Location center, double blockRadius) {
        int centerChunkX = center.getBlockX() >> 4;
        int centerChunkZ = center.getBlockZ() >> 4;
        int chunkRadius = (int) Math.ceil(blockRadius / 16.0) + 1;

        for (int dx = -chunkRadius; dx <= chunkRadius; dx++) {
            for (int dz = -chunkRadius; dz <= chunkRadius; dz++) {
                Chunk chunk = world.getChunkAt(centerChunkX + dx, centerChunkZ + dz);
                if (!chunk.isLoaded()) {
                    chunk.load(true);
                }
            }
        }
    }
}
