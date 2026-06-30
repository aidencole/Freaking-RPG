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
import org.bukkit.entity.Player;

public final class RingRotationEngine {

    private final FreakingRpgPlugin plugin;
    private final Location center;
    private final List<ArenaRing> rings = new ArrayList<>();
    private final List<BlockDisplay> ringMarkers = new ArrayList<>();
    private boolean enabled;
    private double rotationBoost;

    public RingRotationEngine(FreakingRpgPlugin plugin, Location center, double arenaRadius) {
        this.plugin = plugin;
        this.center = center.clone();
        double third = arenaRadius / 3.0;
        rings.add(new ArenaRing("outer", third * 2.0, arenaRadius, 0.004));
        rings.add(new ArenaRing("middle", third, third * 2.0, -0.006));
        rings.add(new ArenaRing("inner", 4.0, third, 0.008));
    }

    public List<ArenaRing> rings() {
        return rings;
    }

    public void buildVisuals() {
        World world = center.getWorld();
        if (world == null) {
            return;
        }

        for (ArenaRing ring : rings) {
            double markerRadius = (ring.innerRadius() + ring.outerRadius()) / 2.0;
            for (int i = 0; i < 8; i++) {
                double angle = (Math.PI * 2 * i) / 8;
                Location spawn = center.clone().add(
                    Math.cos(angle) * markerRadius,
                    0.2,
                    Math.sin(angle) * markerRadius
                );
                BlockDisplay marker = world.spawn(spawn, BlockDisplay.class, display -> {
                    display.setBlock(Material.QUARTZ_BLOCK.createBlockData());
                    display.setTransformation(DisplayTransforms.groundCrack(0.7f));
                    display.setPersistent(false);
                });
                ringMarkers.add(marker);
            }
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setRotationBoost(double rotationBoost) {
        this.rotationBoost = rotationBoost;
    }

    public void tick(List<Player> players) {
        if (!enabled) {
            return;
        }

        for (ArenaRing ring : rings) {
            double boost = 1.0 + rotationBoost;
            if (!ring.locked()) {
                ring.tick(boost);
            }
            spawnRingParticles(ring);
        }

        rotatePlayers(players);
        updateMarkers();
    }

    private void rotatePlayers(List<Player> players) {
        for (Player player : players) {
            if (player.isDead()) {
                continue;
            }

            double dx = player.getLocation().getX() - center.getX();
            double dz = player.getLocation().getZ() - center.getZ();
            double distance = Math.hypot(dx, dz);
            if (distance < 3.0) {
                continue;
            }

            ArenaRing ring = ringFor(distance);
            if (ring == null || ring.locked()) {
                continue;
            }

            double delta = ring.angularVelocity() * (1.0 + rotationBoost);
            double angle = Math.atan2(dz, dx) + delta;
            double y = player.getLocation().getY();
            Location next = new Location(
                center.getWorld(),
                center.getX() + Math.cos(angle) * distance,
                y,
                center.getZ() + Math.sin(angle) * distance,
                player.getLocation().getYaw(),
                player.getLocation().getPitch()
            );
            player.teleport(next);
        }
    }

    private ArenaRing ringFor(double distance) {
        for (ArenaRing ring : rings) {
            if (ring.contains(distance)) {
                return ring;
            }
        }
        return null;
    }

    private void spawnRingParticles(ArenaRing ring) {
        World world = center.getWorld();
        if (world == null) {
            return;
        }

        double radius = (ring.innerRadius() + ring.outerRadius()) / 2.0;
        for (int i = 0; i < 6; i++) {
            double angle = ring.angleRadians() + (Math.PI * 2 * i) / 6;
            world.spawnParticle(
                Particle.END_ROD,
                center.getX() + Math.cos(angle) * radius,
                center.getY() + 0.3,
                center.getZ() + Math.sin(angle) * radius,
                1,
                0,
                0,
                0,
                0
            );
        }
    }

    private void updateMarkers() {
        int markerIndex = 0;
        for (ArenaRing ring : rings) {
            double markerRadius = (ring.innerRadius() + ring.outerRadius()) / 2.0;
            for (int i = 0; i < 8; i++) {
                if (markerIndex >= ringMarkers.size()) {
                    return;
                }
                BlockDisplay marker = ringMarkers.get(markerIndex++);
                if (!marker.isValid()) {
                    continue;
                }
                double angle = ring.angleRadians() + (Math.PI * 2 * i) / 8;
                marker.teleport(center.clone().add(
                    Math.cos(angle) * markerRadius,
                    0.2 + Math.sin(ring.tiltRadians()) * 0.5,
                    Math.sin(angle) * markerRadius
                ));
            }
        }
    }

    public void shutdown() {
        for (BlockDisplay marker : ringMarkers) {
            if (marker.isValid()) {
                marker.remove();
            }
        }
        ringMarkers.clear();
    }
}
