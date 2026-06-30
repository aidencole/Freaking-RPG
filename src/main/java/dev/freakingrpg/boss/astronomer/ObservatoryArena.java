package dev.freakingrpg.boss.astronomer;

import dev.freakingrpg.FreakingRpgPlugin;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

public final class ObservatoryArena {

    private final Location center;
    private final double radius;

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

        world.spawnParticle(Particle.END_ROD, center.clone().add(0, 2, 0), 40, radius * 0.35, 0.5, radius * 0.35, 0.01);
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
    }
}
