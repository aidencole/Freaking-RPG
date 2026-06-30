package dev.freakingrpg.vfx.effects;

import dev.freakingrpg.FreakingRpgPlugin;
import dev.freakingrpg.vfx.DisplayTransforms;
import dev.freakingrpg.vfx.VfxRunner;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;

public final class GroundCrackTelegraph {

    private GroundCrackTelegraph() {
    }

    public static void spawnRing(FreakingRpgPlugin plugin, Location center, double radius, int segments) {
        World world = center.getWorld();
        if (world == null) {
            return;
        }

        VfxRunner vfx = plugin.vfxRunner();
        BlockData crack = Material.STONE.createBlockData();

        for (int i = 0; i < segments; i++) {
            double angle = (Math.PI * 2 * i) / segments;
            Location spawn = center.clone().add(Math.cos(angle) * radius, 0, Math.sin(angle) * radius);
            float scale = 0.55f + (float) (Math.random() * 0.2f);

            BlockDisplay display = world.spawn(spawn, BlockDisplay.class, entity -> {
                entity.setBlock(crack);
                entity.setTransformation(DisplayTransforms.groundCrack(scale));
                entity.setInterpolationDuration(4);
                entity.setPersistent(false);
            });
            vfx.track(display, 30);
        }
    }
}
