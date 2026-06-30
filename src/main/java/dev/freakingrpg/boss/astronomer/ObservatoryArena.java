package dev.freakingrpg.boss.astronomer;

import dev.freakingrpg.FreakingRpgPlugin;
import dev.freakingrpg.vfx.DisplayTransforms;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.BlockDisplay;

public final class ObservatoryArena {

    private final Location center;
    private final double radius;
    private final List<BlockDisplay> scenery = new ArrayList<>();

    private ObservatoryArena(Location center, double radius) {
        this.center = center.clone();
        this.radius = radius;
    }

    public static ObservatoryArena build(FreakingRpgPlugin plugin, Location center, double radius) {
        ObservatoryArena arena = new ObservatoryArena(center, radius);
        arena.buildScenery();
        return arena;
    }

    public Location center() {
        return center.clone();
    }

    public double radius() {
        return radius;
    }

    private void buildScenery() {
        World world = center.getWorld();
        if (world == null) {
            return;
        }

        buildTelescope(world);
        buildFloorGears(world);
        world.spawnParticle(Particle.END_ROD, center.clone().add(0, 2, 0), 30, radius * 0.4, 0.5, radius * 0.4, 0.01);
    }

    private void buildTelescope(World world) {
        for (int height = 0; height < 12; height++) {
            Location spawn = center.clone().add(radius * 0.55, height * 0.8, 0);
            BlockDisplay segment = world.spawn(spawn, BlockDisplay.class, display -> {
                display.setBlock(Material.IRON_BLOCK.createBlockData());
                display.setTransformation(DisplayTransforms.rockChunk(0.9f));
                display.setPersistent(false);
            });
            scenery.add(segment);
        }
    }

    private void buildFloorGears(World world) {
        for (int gear = 0; gear < 4; gear++) {
            double angle = (Math.PI / 2) * gear;
            Location spawn = center.clone().add(Math.cos(angle) * (radius * 0.35), -0.5, Math.sin(angle) * (radius * 0.35));
            BlockDisplay floorGear = world.spawn(spawn, BlockDisplay.class, display -> {
                display.setBlock(Material.DEEPSLATE_BRICKS.createBlockData());
                display.setTransformation(DisplayTransforms.groundCrack(2.2f));
                display.setPersistent(false);
            });
            scenery.add(floorGear);
        }
    }

    public void tickScenery(int tick) {
        World world = center.getWorld();
        if (world == null) {
            return;
        }

        if (tick % 10 == 0) {
            world.spawnParticle(
                Particle.WAX_OFF,
                center.getX(),
                center.getY() + 0.2,
                center.getZ(),
                12,
                radius * 0.3,
                0.1,
                radius * 0.3,
                0.01
            );
        }
    }

    public void shutdown() {
        for (BlockDisplay display : scenery) {
            if (display.isValid()) {
                display.remove();
            }
        }
        scenery.clear();
    }
}
