package dev.freakingrpg.vfx.effects;

import dev.freakingrpg.FreakingRpgPlugin;
import dev.freakingrpg.vfx.DisplayTransforms;
import dev.freakingrpg.vfx.VfxRunner;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.util.Vector;

public final class ImpactCraterEffect {

    private ImpactCraterEffect() {
    }

    public static void burst(FreakingRpgPlugin plugin, Location center, double radius, int rocks) {
        World world = center.getWorld();
        if (world == null) {
            return;
        }

        VfxRunner vfx = plugin.vfxRunner();
        for (int i = 0; i < rocks; i++) {
            double angle = Math.random() * Math.PI * 2;
            double distance = Math.random() * radius;
            Location spawn = center.clone().add(
                Math.cos(angle) * distance,
                0.1,
                Math.sin(angle) * distance
            );

            Material material = DisplayTransforms.randomRockMaterial();
            float scale = 0.35f + (float) (Math.random() * 0.45f);
            BlockDisplay display = world.spawn(spawn, BlockDisplay.class, entity -> {
                entity.setBlock(material.createBlockData());
                entity.setTransformation(DisplayTransforms.rockChunk(scale));
                entity.setInterpolationDuration(2);
                entity.setPersistent(false);
            });

            Vector velocity = new Vector(
                Math.cos(angle) * (0.2 + Math.random() * 0.35),
                0.45 + Math.random() * 0.55,
                Math.sin(angle) * (0.2 + Math.random() * 0.35)
            );
            vfx.launchRock(display, velocity, 35, 0.04f);
        }
    }
}
