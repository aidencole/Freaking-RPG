package dev.freakingrpg.boss.astronomer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

public final class ArenaChunkLoader {

    private ArenaChunkLoader() {
    }

    public static void ensureLoaded(World world, Location center, double blockRadius) {
        forEachArenaChunk(world, center, blockRadius, chunk -> {
            if (!chunk.isLoaded()) {
                chunk.load(true);
            }
            chunk.setForceLoaded(true);
        });
    }

    public static void refreshArena(World world, Location center, double blockRadius) {
        forEachArenaChunk(world, center, blockRadius, chunk -> {
            if (!chunk.isLoaded()) {
                chunk.load(true);
            }
            chunk.setForceLoaded(true);
            world.refreshChunk(chunk.getX(), chunk.getZ());
        });
    }

    public static void releaseArena(World world, Location center, double blockRadius) {
        forEachArenaChunk(world, center, blockRadius, chunk -> chunk.setForceLoaded(false));
    }

    /**
     * Fallback when chunk packets lag behind teleport — pushes block states directly to the client.
     */
    public static void syncBlocksToPlayer(Player player, Collection<Block> blocks, double radius) {
        Location origin = player.getLocation();
        double radiusSquared = radius * radius;
        Map<Location, BlockData> changes = new HashMap<>();

        for (Block block : blocks) {
            Location location = block.getLocation();
            if (location.getWorld() != origin.getWorld()) {
                continue;
            }
            if (location.distanceSquared(origin) > radiusSquared) {
                continue;
            }
            changes.put(location, block.getBlockData());
        }

        if (!changes.isEmpty()) {
            player.sendMultiBlockChange(changes);
        }
    }

    private static void forEachArenaChunk(
        World world,
        Location center,
        double blockRadius,
        java.util.function.Consumer<Chunk> action
    ) {
        int centerChunkX = center.getBlockX() >> 4;
        int centerChunkZ = center.getBlockZ() >> 4;
        int chunkRadius = (int) Math.ceil(blockRadius / 16.0) + 1;

        for (int dx = -chunkRadius; dx <= chunkRadius; dx++) {
            for (int dz = -chunkRadius; dz <= chunkRadius; dz++) {
                action.accept(world.getChunkAt(centerChunkX + dx, centerChunkZ + dz));
            }
        }
    }
}
