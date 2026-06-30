package dev.freakingrpg.boss.astronomer;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public final class ObservatoryPlatformBuilder {

    private ObservatoryPlatformBuilder() {
    }

    public static List<Block> build(Location floorCenter, double radius) {
        World world = floorCenter.getWorld();
        if (world == null) {
            return List.of();
        }

        ArenaChunkLoader.ensureLoaded(world, floorCenter, radius + 4);

        List<Block> placed = new ArrayList<>();
        int centerX = floorCenter.getBlockX();
        int floorY = floorCenter.getBlockY();
        int centerZ = floorCenter.getBlockZ();
        int intRadius = (int) Math.ceil(radius);

        for (int x = -intRadius; x <= intRadius; x++) {
            for (int z = -intRadius; z <= intRadius; z++) {
                double distance = Math.hypot(x, z);
                if (distance > radius) {
                    continue;
                }

                Material material = ringMaterial(distance, radius);
                place(placed, world, centerX + x, floorY - 1, centerZ + z, Material.BEDROCK);
                place(placed, world, centerX + x, floorY, centerZ + z, Material.STONE);
                place(placed, world, centerX + x, floorY + 1, centerZ + z, material);

                if (isRingEdge(distance, radius)) {
                    place(placed, world, centerX + x, floorY + 2, centerZ + z, Material.SEA_LANTERN);
                }
            }
        }

        buildTelescopeBlocks(placed, world, centerX, floorY + 1, centerZ, radius);
        return placed;
    }

    private static void buildTelescopeBlocks(
        List<Block> placed,
        World world,
        int centerX,
        int baseY,
        int centerZ,
        double radius
    ) {
        int telescopeX = centerX + (int) (radius * 0.45);
        int telescopeZ = centerZ;
        for (int height = 0; height < 10; height++) {
            place(placed, world, telescopeX, baseY + height, telescopeZ, Material.IRON_BLOCK);
        }
        place(placed, world, telescopeX, baseY + 10, telescopeZ, Material.LIGHTNING_ROD);
    }

    private static void place(List<Block> placed, World world, int x, int y, int z, Material material) {
        Block block = world.getBlockAt(x, y, z);
        block.setType(material, false);
        placed.add(block);
    }

    public static void clear(List<Block> blocks) {
        for (Block block : blocks) {
            if (block.getType() != Material.AIR) {
                block.setType(Material.AIR, false);
            }
        }
        blocks.clear();
    }

    private static Material ringMaterial(double distance, double radius) {
        double ratio = distance / radius;
        if (ratio < 0.33) {
            return Material.POLISHED_DEEPSLATE;
        }
        if (ratio < 0.66) {
            return Material.DEEPSLATE_BRICKS;
        }
        return Material.QUARTZ_BLOCK;
    }

    private static boolean isRingEdge(double distance, double radius) {
        double outer = radius * 0.66;
        double inner = radius * 0.33;
        return Math.abs(distance - outer) < 0.9 || Math.abs(distance - inner) < 0.9;
    }
}
