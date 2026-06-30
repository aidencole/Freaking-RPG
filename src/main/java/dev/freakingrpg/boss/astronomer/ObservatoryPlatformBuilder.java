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

    public static List<Block> build(Location center, double radius) {
        World world = center.getWorld();
        if (world == null) {
            return List.of();
        }

        List<Block> placed = new ArrayList<>();
        int centerX = center.getBlockX();
        int centerY = center.getBlockY();
        int centerZ = center.getBlockZ();
        int intRadius = (int) Math.ceil(radius);

        for (int x = -intRadius; x <= intRadius; x++) {
            for (int z = -intRadius; z <= intRadius; z++) {
                double distance = Math.hypot(x, z);
                if (distance > radius) {
                    continue;
                }

                Material material = ringMaterial(distance, radius);
                Block block = world.getBlockAt(centerX + x, centerY, centerZ + z);
                block.setType(material, false);
                placed.add(block);

                if (isRingEdge(distance, radius)) {
                    Block accent = world.getBlockAt(centerX + x, centerY + 1, centerZ + z);
                    accent.setType(Material.LIGHTNING_ROD, false);
                    placed.add(accent);
                }
            }
        }

        return placed;
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
        return Math.abs(distance - outer) < 0.8 || Math.abs(distance - inner) < 0.8;
    }
}
