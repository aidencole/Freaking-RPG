package dev.freakingrpg.boss.astronomer;

import java.util.Random;
import org.bukkit.Material;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

/**
 * Bakes the observatory floor into chunk data so clients receive real blocks on first load.
 * Manual edits (planetfall, etc.) still use {@link ObservatoryPlatformBuilder}.
 */
public final class ObservatoryChunkGenerator extends ChunkGenerator {

    private final double arenaRadius;
    private final int floorY;

    public ObservatoryChunkGenerator(double arenaRadius, int floorY) {
        this.arenaRadius = arenaRadius;
        this.floorY = floorY;
    }

    @Override
    public boolean shouldGenerateNoise() {
        return true;
    }

    @Override
    public boolean shouldGenerateSurface() {
        return false;
    }

    @Override
    public boolean shouldGenerateCaves() {
        return false;
    }

    @Override
    public boolean shouldGenerateDecorations() {
        return false;
    }

    @Override
    public boolean shouldGenerateMobs() {
        return false;
    }

    @Override
    public boolean shouldGenerateStructures() {
        return false;
    }

    @Override
    public void generateNoise(
        @NotNull WorldInfo worldInfo,
        @NotNull Random random,
        int chunkX,
        int chunkZ,
        @NotNull ChunkData chunkData
    ) {
        for (int localX = 0; localX < 16; localX++) {
            for (int localZ = 0; localZ < 16; localZ++) {
                int worldX = chunkX * 16 + localX;
                int worldZ = chunkZ * 16 + localZ;
                double distance = Math.hypot(worldX, worldZ);
                if (distance > arenaRadius) {
                    continue;
                }

                Material floor = ObservatoryPlatformBuilder.ringMaterial(distance, arenaRadius);
                chunkData.setBlock(localX, floorY - 1, localZ, Material.BEDROCK);
                chunkData.setBlock(localX, floorY, localZ, Material.STONE);
                chunkData.setBlock(localX, floorY + 1, localZ, floor);
                if (ObservatoryPlatformBuilder.isRingEdge(distance, arenaRadius)) {
                    chunkData.setBlock(localX, floorY + 2, localZ, Material.SEA_LANTERN);
                }
            }
        }
    }
}
