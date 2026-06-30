package dev.freakingrpg.vfx.effects;

import dev.freakingrpg.FreakingRpgPlugin;
import dev.freakingrpg.vfx.DisplayTransforms;
import dev.freakingrpg.vfx.VfxRunner;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.util.Vector;

public final class RockShockwaveEffect {

    private RockShockwaveEffect() {
    }

    public static void spawnRing(
        FreakingRpgPlugin plugin,
        Location center,
        double radius,
        int segments,
        double lift,
        double outwardBoost
    ) {
        World world = center.getWorld();
        if (world == null) {
            return;
        }

        VfxRunner vfx = plugin.vfxRunner();
        double groundY = center.getY();

        for (int i = 0; i < segments; i++) {
            double angle = (Math.PI * 2 * i) / segments;
            double x = center.getX() + Math.cos(angle) * radius;
            double z = center.getZ() + Math.sin(angle) * radius;
            Location spawn = new Location(world, x, groundY, z);

            Material material = DisplayTransforms.randomRockMaterial();
            BlockData blockData = material.createBlockData();
            float scale = 0.25f + (float) (Math.random() * 0.35f);

            BlockDisplay display = world.spawn(spawn, BlockDisplay.class, entity -> {
                entity.setBlock(blockData);
                entity.setTransformation(DisplayTransforms.rockChunk(scale));
                entity.setInterpolationDuration(2);
                entity.setTeleportDuration(2);
                entity.setPersistent(false);
                entity.setBrightness(new Display.Brightness(15, 15));
            });

            Vector velocity = new Vector(
                Math.cos(angle) * outwardBoost,
                lift + Math.random() * 0.08,
                Math.sin(angle) * outwardBoost
            );
            vfx.launchRock(display, velocity, 28 + (int) (Math.random() * 10), 0.035f);
        }
    }

    public static void spawnPulse(
        FreakingRpgPlugin plugin,
        Location center,
        double radius,
        int density
    ) {
        spawnRing(plugin, center, radius, density, 0.35, 0.12);
        spawnRing(plugin, center, radius * 0.85, Math.max(8, density - 4), 0.22, 0.08);
    }
}
